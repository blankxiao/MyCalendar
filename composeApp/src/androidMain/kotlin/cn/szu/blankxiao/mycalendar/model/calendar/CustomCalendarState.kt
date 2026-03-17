package cn.szu.blankxiao.mycalendar.model.calendar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cn.szu.blankxiao.mycalendar.model.calendar.CalendarDataCacheManager
import cn.szu.blankxiao.mycalendar.util.CalendarDataCalculator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

// 状态转换的阈值 单位dp
const val ModeChangeDragThreshold = 200

@Composable
fun rememberCustomCalendarState(
    startDate: LocalDate = run {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        YearMonth.from(today).atDay(1)
    },
    endDate: LocalDate = run {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        YearMonth.from(today).plusMonths(12).atEndOfMonth()
    },
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    outDateStyle: OutDateStyle = OutDateStyle.EndOfRow,
    initialVisibleDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    initialMode: CalendarMode = CalendarMode.MONTH,
): CustomCalendarState {
    return rememberSaveable(
        inputs = arrayOf<Any>(
            startDate,
            endDate,
            firstDayOfWeek,
            outDateStyle,
            initialVisibleDate,
            initialMode,
        ),
        saver = CustomCalendarState.Saver,
    ) {
        CustomCalendarState(
            startDate = startDate,
            endDate = endDate,
            firstDayOfWeek = firstDayOfWeek,
            outDateStyle = outDateStyle,
            initialVisibleDate = initialVisibleDate,
            initialMode = initialMode,
        )
    }
}

@Stable
class CustomCalendarState internal constructor(
    startDate: LocalDate,
    endDate: LocalDate,
    firstDayOfWeek: DayOfWeek,
    outDateStyle: OutDateStyle,
    initialVisibleDate: LocalDate,
    initialMode: CalendarMode,
) : ScrollableState {

    private var _startDate by mutableStateOf(startDate)
    val startDate: LocalDate get() = _startDate

    private var _endDate by mutableStateOf(endDate)
    val endDate: LocalDate get() = _endDate

    private var _firstDayOfWeek by mutableStateOf(firstDayOfWeek)
    var firstDayOfWeek: DayOfWeek
        get() = _firstDayOfWeek
        set(value) {
            if (_firstDayOfWeek != value) {
                _firstDayOfWeek = value
                onConfigChanged()
            }
        }

    private var _outDateStyle by mutableStateOf(outDateStyle)
    var outDateStyle: OutDateStyle
        get() = _outDateStyle
        set(value) {
            if (_outDateStyle != value) {
                _outDateStyle = value
                onConfigChanged()
            }
        }

    val startMonth: YearMonth get() = YearMonth.from(_startDate)
    val endMonth: YearMonth get() = YearMonth.from(_endDate)
    val totalMonthCount: Int get() = CalendarDataCalculator.getMonthCount(startMonth, endMonth)
    val totalWeekCount: Int get() = CalendarDataCalculator.getTotalWeekCount(_startDate, _endDate, _firstDayOfWeek)

    var selectedDate by mutableStateOf(initialVisibleDate)
        private set

    private var _calendarMode by mutableStateOf(initialMode)
    val calendarMode: CalendarMode get() = _calendarMode

    private var _isSwitchingMode = false

    internal val progressAnimatable: Animatable<Float, AnimationVector1D> = Animatable(
        if (initialMode == CalendarMode.WEEK) 1f else 0f
    )

    val transitionProgress: Float get() = progressAnimatable.value

    private var cacheManager = CalendarDataCacheManager(
        startDate = _startDate,
        startMonth = startMonth,
        firstDayOfWeek = _firstDayOfWeek,
        outDateStyle = _outDateStyle
    )

    val pageCount: Int
        get() = when (_calendarMode) {
            CalendarMode.MONTH -> totalMonthCount
            CalendarMode.WEEK -> totalWeekCount
        }

    internal val pagerState = PagerState(
        currentPage = calCurrentPage(initialMode, initialVisibleDate),
        pageCount = { pageCount }
    )

    val isInTransition: Boolean
        get() = (transitionProgress > 0f && transitionProgress < 1f) || progressAnimatable.isRunning

    fun getDataForPage(pageIndex: Int): CustomCalendarData {
        if (isInTransition) {
            val monthIndex = when (_calendarMode) {
                CalendarMode.MONTH -> pageIndex
                CalendarMode.WEEK -> {
                    val weekStartDate = CalendarDataCalculator.getDateByWeekIndex(
                        _startDate, pageIndex, _firstDayOfWeek
                    )
                    CalendarDataCalculator.getMonthIndex(startMonth, YearMonth.from(weekStartDate))
                }
            }
            return cacheManager.getMonthData(monthIndex)
        }

        return when (_calendarMode) {
            CalendarMode.MONTH -> cacheManager.getMonthData(pageIndex)
            CalendarMode.WEEK -> cacheManager.getWeekData(pageIndex)
        }
    }

    fun onPageChanged(newPageIndex: Int) {
        if (_isSwitchingMode) return

        val newDate = when (_calendarMode) {
            CalendarMode.MONTH -> {
                val newMonth = CalendarDataCalculator.getMonthByOffset(startMonth, newPageIndex)
                val dayOfMonth = selectedDate.dayOfMonth.coerceAtMost(newMonth.lengthOfMonth)
                newMonth.atDay(dayOfMonth)
            }
            CalendarMode.WEEK -> {
                val weekStartDate = CalendarDataCalculator.getDateByWeekIndex(
                    _startDate, newPageIndex, _firstDayOfWeek
                )
                val dayOffset = selectedDate.dayOfWeek.isoDayNumber - _firstDayOfWeek.isoDayNumber
                val adjustedOffset = if (dayOffset < 0) dayOffset + 7 else dayOffset
                weekStartDate.plus(adjustedOffset, DateTimeUnit.DAY)
            }
        }

        if (newDate != selectedDate && newDate >= _startDate && newDate <= _endDate) {
            selectedDate = newDate
        }
    }

    suspend fun scrollToDate(date: LocalDate, animate: Boolean = false) {
        if (date < _startDate || date > _endDate) return

        selectedDate = date
        val targetPageIndex = calCurrentPage(_calendarMode, date)

        if (animate) {
            pagerState.animateScrollToPage(targetPageIndex)
        } else {
            pagerState.scrollToPage(targetPageIndex)
        }
    }

    suspend fun updateTransitionProgress(progress: Float) {
        progressAnimatable.snapTo(progress.coerceIn(0f, 1f))
    }

    suspend fun finishDragAndSnap(velocity: Float = 0f) {
        val threshold = if (abs(velocity) > 1000f) 0.3f else 0.5f
        val currentProgress = progressAnimatable.value
        val targetProgress = if (currentProgress <= threshold) 0f else 1f
        val targetMode = if (targetProgress == 0f) CalendarMode.MONTH else CalendarMode.WEEK

        progressAnimatable.animateTo(
            targetValue = targetProgress,
            animationSpec = ModeTransitionAnimationSpec
        )

        switchPagerMode(targetMode)
    }

    private suspend fun switchPagerMode(newPagerMode: CalendarMode) {
        if (_calendarMode == newPagerMode) return

        _isSwitchingMode = true
        _calendarMode = newPagerMode

        val targetPageIndex = calCurrentPage(newPagerMode, selectedDate)
        pagerState.scrollToPage(targetPageIndex)

        _isSwitchingMode = false
    }

    private fun calCurrentPage(mode: CalendarMode, targetDate: LocalDate): Int = when (mode) {
        CalendarMode.MONTH -> CalendarDataCalculator.getMonthIndex(
            startMonth,
            targetDate.yearMonth
        )
        CalendarMode.WEEK -> CalendarDataCalculator.getWeekIndex(
            _startDate,
            targetDate,
            _firstDayOfWeek
        )
    }

    override val isScrollInProgress: Boolean get() = pagerState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float {
        return pagerState.dispatchRawDelta(delta)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        pagerState.scroll(scrollPriority, block)
    }

    private fun onConfigChanged() {
        cacheManager = CalendarDataCacheManager(
            startDate = _startDate,
            startMonth = startMonth,
            firstDayOfWeek = _firstDayOfWeek,
            outDateStyle = _outDateStyle
        )
    }

    companion object {
        private val ModeTransitionAnimationSpec = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        internal val Saver: Saver<CustomCalendarState, Any> = listSaver(
            save = {
                listOf(
                    it.startDate.toString(),
                    it.endDate.toString(),
                    it.firstDayOfWeek.isoDayNumber,
                    it.outDateStyle.ordinal,
                    it.selectedDate.toString(),
                    it.calendarMode.ordinal,
                )
            },
            restore = {
                val restored = it as List<*>
                CustomCalendarState(
                    startDate = LocalDate.parse(restored[0] as String),
                    endDate = LocalDate.parse(restored[1] as String),
                    firstDayOfWeek = DayOfWeek.entries.first { d -> d.isoDayNumber == (restored[2] as Int) },
                    outDateStyle = OutDateStyle.entries[restored[3] as Int],
                    initialVisibleDate = LocalDate.parse(restored[4] as String),
                    initialMode = CalendarMode.entries[restored[5] as Int],
                )
            },
        )
    }
}
