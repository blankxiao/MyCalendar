package cn.szu.blankxiao.mycalendar.data.calendar

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cn.szu.blankxiao.mycalendar.utils.CalendarDataCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "CustomCalendarState"

// 状态转换的阈值 单位dp
const val ModeChangeDragThreshold = 200


/**
 * 月份数据缓存
 * 类似 KCalendar 的 DataStore，但更简单
 */
class MonthDataCache(
    private val calculator: (offset: Int) -> CustomCalendarMonth
) {
    private val cache = mutableMapOf<Int, CustomCalendarMonth>()
    
    /**
     * 获取指定偏移量的月份数据
     * 如果缓存中没有，则计算并缓存
     */
    fun get(offset: Int): CustomCalendarMonth {
        return cache.getOrPut(offset) {
            calculator(offset).also {
                Log.d(TAG, "Cache miss: calculated month at offset=$offset (${it.yearMonth})")
            }
        }
    }
    
    /**
     * 清空缓存
     */
    fun clear() {
        cache.clear()
        Log.d(TAG, "Cache cleared")
    }
    
    /**
     * 获取缓存大小
     */
    val size: Int get() = cache.size
}

// ============ 状态管理 ============

/**
 * 创建并记住自定义日历状态
 *
 * @param startDate 日历起始日期
 * @param endDate 日历结束日期
 * @param firstDayOfWeek 一周的第一天
 * @param outDateStyle 月份外日期样式
 * @param initialVisibleDate 初始可见日期
 * @param initialMode 初始显示模式
 */
@Composable
fun rememberCustomCalendarState(
    startDate: LocalDate = YearMonth.now().atDay(1),
    endDate: LocalDate = YearMonth.now().plusYears(1).atEndOfMonth(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    outDateStyle: OutDateStyle = OutDateStyle.EndOfRow,
    initialVisibleDate: LocalDate = LocalDate.now(),
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

/**
 * 支持月/周模式切换的自定义日历状态
 *
 * 核心设计：
 * 1. 使用 LocalDate 统一表示日期范围
 * 2. 内部维护双轨制位置（YearMonth 用于月模式，LocalDate 用于周模式）
 * 3. 使用 LazyListState 管理滚动（按月滚动）
 * 4. 通过 targetWeekIndex 在周模式下过滤显示
 */
@Stable
class CustomCalendarState internal constructor(
    startDate: LocalDate,
    endDate: LocalDate,
    firstDayOfWeek: DayOfWeek,
    outDateStyle: OutDateStyle,
    initialVisibleDate: LocalDate,
    initialMode: CalendarMode,
) : ScrollableState {
    
    // ============ 基础配置 ============
    
    /** 日历起始日期（私有可变状态）*/
    private var _startDate by mutableStateOf(startDate)
    
    /** 日历起始日期（公开只读）*/
    val startDate: LocalDate
        get() = _startDate
    
    /** 日历结束日期（私有可变状态）*/
    private var _endDate by mutableStateOf(endDate)
    
    /** 日历结束日期（公开只读）*/
    val endDate: LocalDate
        get() = _endDate
    
    /** 一周的第一天（私有可变状态）*/
    private var _firstDayOfWeek by mutableStateOf(firstDayOfWeek)
    
    /** 一周的第一天（公开可读写）*/
    var firstDayOfWeek: DayOfWeek
        get() = _firstDayOfWeek
        set(value) {
            if (_firstDayOfWeek != value) {
                _firstDayOfWeek = value
                onConfigChanged()
            }
        }
    
    /** 月份外日期样式（私有可变状态）*/
    private var _outDateStyle by mutableStateOf(outDateStyle)
    
    /** 月份外日期样式（公开可读写）*/
    var outDateStyle: OutDateStyle
        get() = _outDateStyle
        set(value) {
            if (_outDateStyle != value) {
                _outDateStyle = value
                onConfigChanged()
            }
        }
    
    /** 起始月份（派生状态）*/
    val startMonth: YearMonth
        get() = YearMonth.from(_startDate)
    
    /** 结束月份（派生状态）*/
    val endMonth: YearMonth
        get() = YearMonth.from(_endDate)
    
    /** 月份总数（派生状态）*/
    val monthCount: Int
        get() = CalendarDataCalculator.getMonthCount(startMonth, endMonth)
    
    // ============ 当前位置（双轨制）============
    
    /** 当前月份（用于月模式）*/
    private var _currentMonth by mutableStateOf(YearMonth.from(initialVisibleDate))
    
    /** 当前周日期（用于周模式）*/
    private var _currentWeekDate by mutableStateOf(initialVisibleDate)
    
    /** 当前选中的日期 */
    var selectedDate by mutableStateOf(initialVisibleDate)
        private set
    
    /** 当前可见日期*/
    val currentVisibleDate: LocalDate
        get() = when (calendarMode) {
            CalendarMode.MONTH -> _currentMonth.atDay(1)
            CalendarMode.WEEK -> _currentWeekDate
        }

    // ============ 模式状态 ============
    
    /** 当前显示模式（私有可变状态）*/
    private var _calendarMode by mutableStateOf(initialMode)
    
    /** 当前显示模式（公开只读）*/
    val calendarMode: CalendarMode
        get() = _calendarMode
    
    /** 
     * 过渡进度动画：0f = 完全月视图，1f = 完全周视图
     */
    internal val progressAnimatable: Animatable<Float, AnimationVector1D> = Animatable(
        if (initialMode == CalendarMode.WEEK) 1f else 0f
    )
    
    /**
     * 过渡进度
     */
    val transitionProgress: Float
        get() = progressAnimatable.value
    
    // ============ 数据层 ============
    
    /** 月份数据缓存 */
    private val monthDataCache = MonthDataCache { offset ->
        val month = CalendarDataCalculator.getMonthByOffset(startMonth, offset)
        CalendarDataCalculator.calculateMonth(month, _firstDayOfWeek, _outDateStyle)
    }
    
    /** Pager 状态（水平翻页）*/
    internal val pagerState = PagerState(
        currentPage = CalendarDataCalculator.getMonthIndex(
                        startMonth,
            YearMonth.from(initialVisibleDate)),
        pageCount = { monthCount }
    )
    
    /** 当前第一个可见的月份数据（派生状态）*/
    val firstVisibleMonth: CustomCalendarMonth by derivedStateOf {
        val offset = pagerState.currentPage.coerceIn(0, monthCount - 1)
        monthDataCache.get(offset)
    }
    
    /** 目标周索引（在当前月份中的第几周，派生状态）*/
    val targetWeekIndex: Int by derivedStateOf {
        firstVisibleMonth.findWeekIndex(selectedDate).coerceAtLeast(0)
    }
    
    /** 当前可见的周数据（派生状态）*/
    val firstVisibleWeek: List<CustomCalendarDay> by derivedStateOf {
        firstVisibleMonth.getWeek(targetWeekIndex) ?: emptyList()
    }
    
    /** 当前月份的周数（派生状态）*/
    val weekCountInMonth: Int by derivedStateOf {
        firstVisibleMonth.weekCount
    }
    
    // ============ 显示信息（派生状态）============
    
    /** 当前显示的年月（YearMonth 格式）*/
    val currentYearMonth: YearMonth by derivedStateOf {
        firstVisibleMonth.yearMonth
    }
    
    /** 
     * 当前显示的周索引（1-based，仅在周模式下有意义）
     * 返回选中日期在当前月份中是第几周
     */
    val currentWeekOfMonth: Int by derivedStateOf {
        when (calendarMode) {
            CalendarMode.WEEK -> targetWeekIndex + 1 // 转换为 1-based
            CalendarMode.MONTH -> 0 // 月模式下返回 0 表示不适用
        }
    }
    
    /** 
     * 当前显示信息的格式化文本
     * 月模式：「2025年1月」
     * 周模式：「2025年1月 第3周」
     */
    val displayText: String by derivedStateOf {
        val month = currentYearMonth
        when (calendarMode) {
            CalendarMode.MONTH -> "${month.year}年${month.monthValue}月"
            CalendarMode.WEEK -> "${month.year}年${month.monthValue}月 第${currentWeekOfMonth}周"
        }
    }
    
    /**
     * 根据索引获取月份数据
     * @param index 月份索引（从 0 开始）
     */
    fun getMonthByIndex(index: Int): CustomCalendarMonth {
        return monthDataCache.get(index)
    }
    
    // ============ 滚动方法 ============
    
    /**
     * 滚动到指定日期
     *
     * @param date 目标日期
     * @param animate 是否使用动画
     */
    suspend fun scrollToDate(date: LocalDate, animate: Boolean = false) {
        if (date !in _startDate.._endDate) {
            Log.w(TAG, "Attempting to scroll out of range: $date")
            return
        }
        
        selectedDate = date
        _currentMonth = YearMonth.from(date)
        _currentWeekDate = date
        
        val monthIndex = CalendarDataCalculator.getMonthIndex(startMonth, YearMonth.from(date))
        
        Log.d(TAG, "Scrolling to date=$date, monthIndex=$monthIndex, animate=$animate")
        
        if (animate) {
            pagerState.animateScrollToPage(monthIndex)
        } else {
            pagerState.scrollToPage(monthIndex)
        }
    }
    
    /**
     * 滚动到指定月份
     *
     * @param month 目标月份
     * @param animate 是否使用动画
     */
    suspend fun scrollToMonth(month: YearMonth, animate: Boolean = false) {
        scrollToDate(month.atDay(1), animate)
    }
    
    /**
     * 切换到下一周/月
     * - 月模式：下一个月
     * - 周模式：下一周（可能跨月）
     *
     * @param animate 是否使用动画
     */
    suspend fun scrollToNext(animate: Boolean = true) {
        when (_calendarMode) {
            CalendarMode.MONTH -> {
                val nextMonth = _currentMonth.plusMonths(1)
                if (nextMonth <= endMonth) {
                    Log.d(TAG, "Scrolling to next month: $nextMonth")
                    scrollToMonth(nextMonth, animate)
                } else {
                    Log.d(TAG, "Already at last month")
                }
            }
            CalendarMode.WEEK -> {
                val nextWeek = _currentWeekDate.plusWeeks(1)
                if (nextWeek <= _endDate) {
                    Log.d(TAG, "Scrolling to next week: $nextWeek")
                    scrollToDate(nextWeek, animate)
                } else {
                    Log.d(TAG, "Already at last week")
                }
            }
        }
    }
    
    /**
     * 切换到上一周/月
     * - 月模式：上一个月
     * - 周模式：上一周（可能跨月）
     *
     * @param animate 是否使用动画
     */
    suspend fun scrollToPrevious(animate: Boolean = true) {
        when (_calendarMode) {
            CalendarMode.MONTH -> {
                val prevMonth = _currentMonth.minusMonths(1)
                if (prevMonth >= startMonth) {
                    Log.d(TAG, "Scrolling to previous month: $prevMonth")
                    scrollToMonth(prevMonth, animate)
                } else {
                    Log.d(TAG, "Already at first month")
                }
            }
            CalendarMode.WEEK -> {
                val prevWeek = _currentWeekDate.minusWeeks(1)
                if (prevWeek >= _startDate) {
                    Log.d(TAG, "Scrolling to previous week: $prevWeek")
                    scrollToDate(prevWeek, animate)
                } else {
                    Log.d(TAG, "Already at first week")
                }
            }
        }
    }
    
    // ============ 模式切换 ============
    
    /**
     * 切换到指定模式
     *
     * @param mode 目标模式
     * @param animate 是否使用动画
     */
    suspend fun switchToMode(mode: CalendarMode, animate: Boolean = true) {
        if (_calendarMode == mode) {
            Log.d(TAG, "Already in $mode mode, skipping")
            return
        }
        
        Log.d(TAG, "Switching from $_calendarMode to $mode, animate=$animate")
        
        // 更新模式
        _calendarMode = mode
        
        // 更新进度值
        val targetProgress = if (mode == CalendarMode.WEEK) 1f else 0f
        
        if (animate) {
            progressAnimatable.animateTo(
                targetValue = targetProgress,
                animationSpec = ModeTransitionAnimationSpec
            )
        } else {
            progressAnimatable.snapTo(targetProgress)
        }
    }
    
    /**
     * 切换到月视图
     */
    suspend fun switchToMonthMode(animate: Boolean = true) {
        switchToMode(CalendarMode.MONTH, animate)
    }
    
    /**
     * 切换到周视图
     */
    suspend fun switchToWeekMode(animate: Boolean = true) {
        switchToMode(CalendarMode.WEEK, animate)
    }
    
    /**
     * 切换模式（在月/周之间切换）
     */
    suspend fun toggleMode(animate: Boolean = true) {
        val newMode = if (_calendarMode == CalendarMode.MONTH) {
            CalendarMode.WEEK
        } else {
            CalendarMode.MONTH
        }
        switchToMode(newMode, animate)
    }
    
    /**
     * 手动设置过渡进度（用于手势拖动）
     *
     * @param progress 0f~1f，0 为月视图，1 为周视图
     */
    suspend fun updateTransitionProgress(progress: Float) {
        progressAnimatable.snapTo(progress.coerceIn(0f, 1f))
    }
    
    /**
     * 拖动停止后，吸附到目标模式并执行动画
     *
     * @param velocity 拖动速度
     */
    suspend fun finishDragAndSnap(velocity: Float = 0f) {
        // 考虑速度：快速滑动可以更容易切换
        val threshold = if (kotlin.math.abs(velocity) > 1000f) {
            0.3f
        } else {
            0.5f
        }
        
        val currentProgress = progressAnimatable.value
        val targetProgress = if (currentProgress <= threshold) 0f else 1f
        val targetMode = if (targetProgress == 0f) CalendarMode.MONTH else CalendarMode.WEEK
        
        Log.d(TAG, "Finishing drag: progress=$currentProgress, target=$targetProgress, mode=$targetMode")
        
        // 更新模式
        _calendarMode = targetMode
        
        // 执行弹簧动画到目标值
        progressAnimatable.animateTo(
            targetValue = targetProgress,
            animationSpec = ModeTransitionAnimationSpec
        )
    }
    
    /**
     * 选择日期
     *
     * @param date 要选择的日期
     * @param scrollToDate 是否滚动到该日期
     */
    suspend fun selectDate(date: LocalDate, scrollToDate: Boolean = true) {
        selectedDate = date
        _currentWeekDate = date
        
        if (scrollToDate) {
            scrollToDate(date, animate = true)
        }
    }
    
    // ============ ScrollableState 实现 ============
    
    override val isScrollInProgress: Boolean
        get() = pagerState.isScrollInProgress
    
    override fun dispatchRawDelta(delta: Float): Float {
        return pagerState.dispatchRawDelta(delta)
    }
    
    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        pagerState.scroll(scrollPriority, block)
    }
    
    // ============ 内部方法 ============
    
    /**
     * 配置变化时清空缓存
     */
    private fun onConfigChanged() {
        monthDataCache.clear()
        Log.d(TAG, "Configuration changed, cache cleared")
    }
    
    // ============ 状态保存 ============
    
    companion object {

        private val ModeTransitionAnimationSpec = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        /**
         * 状态保存器，用于配置变更时保存和恢复状态
         */
        internal val Saver: Saver<CustomCalendarState, Any> = listSaver(
            save = {
                listOf(
                    it.startDate.toString(),
                    it.endDate.toString(),
                    it.firstDayOfWeek.value,
                    it.outDateStyle.ordinal,
                    it.selectedDate.toString(),
                    it.calendarMode.ordinal,
                )
            },
            restore = {
                CustomCalendarState(
                    startDate = LocalDate.parse(it[0] as String),
                    endDate = LocalDate.parse(it[1] as String),
                    firstDayOfWeek = DayOfWeek.of(it[2] as Int),
                    outDateStyle = OutDateStyle.entries[it[3] as Int],
                    initialVisibleDate = LocalDate.parse(it[4] as String),
                    initialMode = CalendarMode.entries[it[5] as Int],
                )
            },
        )
    }
}
