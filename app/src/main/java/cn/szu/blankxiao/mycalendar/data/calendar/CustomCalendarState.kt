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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cn.szu.blankxiao.mycalendar.utils.CalendarDataCalculator
import com.kizitonwose.calendar.core.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "CustomCalendarState"

// 状态转换的阈值 单位dp
const val ModeChangeDragThreshold = 200


/**
 * 日历数据缓存管理器
 * 统一管理月份和周数据的缓存，避免重复计算
 */
class CalendarDataCacheManager(
	private val startDate: LocalDate,
	private val startMonth: YearMonth,
	private val firstDayOfWeek: DayOfWeek,
	private val outDateStyle: OutDateStyle
) {
	// 月份数据缓存 (key: 月份索引)
	private val monthCache = mutableMapOf<Int, CustomCalendarData>()

	// 周数据缓存 (key: 周索引)
	private val weekCache = mutableMapOf<Int, CustomCalendarData>()

	/**
	 * 获取月份数据（带缓存）
	 */
	fun getMonthData(monthIndex: Int): CustomCalendarData {
		return monthCache.getOrPut(monthIndex) {
			val month = CalendarDataCalculator.getMonthByOffset(startMonth, monthIndex)
			CalendarDataCalculator.calculateMonth(month, firstDayOfWeek, outDateStyle)
		}
	}

	/**
	 * 获取周数据（带缓存）
	 * @param weekIndex 全局周索引（从起始日期开始计算）
	 */
	fun getWeekData(weekIndex: Int): CustomCalendarData {
		return weekCache.getOrPut(weekIndex) {
			// 根据周索引计算该周的第一天
			val weekStartDate = CalendarDataCalculator.getDateByWeekIndex(
				startDate, weekIndex, firstDayOfWeek
			)
			Log.d(TAG, "getWeekData: 周索引: $weekIndex, 周第一天: $weekStartDate")
			// 找到对应的月份索引
			val monthIndex = CalendarDataCalculator.getMonthIndex(
				startMonth, YearMonth.from(weekStartDate)
			)
			// 获取月份数据（复用缓存）
			val monthData = getMonthData(monthIndex)
			Log.d(TAG, "getWeekData: 月索引: $monthIndex, 对应年月: ${monthData.yearMonth}")

			// 在月份数据中找到该周
			val weekIndexInMonth = monthData.calDateIndexInWeeks(weekStartDate)
			
			monthData.copy(
				yearMonth = monthData.yearMonth,
				weeks = listOf(monthData.weeks[weekIndexInMonth.coerceAtLeast(0)])
			)
		}
	}

	fun clear() {
		monthCache.clear()
		weekCache.clear()
	}

	val monthCacheSize: Int get() = monthCache.size
	val weekCacheSize: Int get() = weekCache.size
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
	val totalMonthCount: Int
		get() = CalendarDataCalculator.getMonthCount(startMonth, endMonth)

	/** 总周数（派生状态）*/
	val totalWeekCount: Int
		get() = CalendarDataCalculator.getTotalWeekCount(_startDate, _endDate, _firstDayOfWeek)

	// ============ 当前位置（双轨制）============

	/** 当前选中的日期 */
	var selectedDate by mutableStateOf(initialVisibleDate)
		private set

	// ============ 模式状态 ============

	/** 当前显示模式（私有可变状态）*/
	private var _calendarMode by mutableStateOf(initialMode)

	/** 当前显示模式（公开只读）*/
	val calendarMode: CalendarMode
		get() = _calendarMode

	/** 是否正在切换模式（用于防止 onPageChanged 错误更新 selectedDate）*/
	private var _isSwitchingMode = false

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

	/** 日历数据缓存管理器 */
	private var cacheManager = CalendarDataCacheManager(
		startDate = _startDate,
		startMonth = startMonth,
		firstDayOfWeek = _firstDayOfWeek,
		outDateStyle = _outDateStyle
	)

	/** 动态页面数（根据 pagerMode 返回月数或周数）*/
	val pageCount: Int
		get() = when (_calendarMode) {
			CalendarMode.MONTH -> totalMonthCount
			CalendarMode.WEEK -> totalWeekCount
		}

	/** Pager 状态（水平翻页）*/
	internal val pagerState = PagerState(
		currentPage = calCurrentPage(initialMode, initialVisibleDate),
		pageCount = { pageCount }
	)

	/**
	 * 是否正在过渡动画中
	 * - transitionProgress 在 (0, 1) 之间
	 * - 或者动画正在运行中
	 */
	val isInTransition: Boolean
		get() = (transitionProgress > 0f && transitionProgress < 1f) || progressAnimatable.isRunning

	/**
	 * 根据当前模式和页面索引获取对应的数据
	 * - 月模式：pageIndex = 月索引，返回月份数据
	 * - 周模式：pageIndex = 周索引，返回周数据
	 * - 过渡期间：始终返回月份数据（用于正确计算动画高度）
	 */
	fun getDataForPage(pageIndex: Int): CustomCalendarData {
		// 过渡动画期间或动画运行中，始终返回完整月份数据
		// 这样才能正确计算从周到月的高度变化
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

	/**
	 * 当 Pager 页面变化时调用，更新 selectedDate
	 * @param newPageIndex 新的页面索引
	 */
	fun onPageChanged(newPageIndex: Int) {
		// 模式切换期间不更新 selectedDate
		if (_isSwitchingMode) {
			Log.d(TAG, "onPageChanged: 跳过（正在切换模式）")
			return
		}

		val newDate = when (_calendarMode) {
			CalendarMode.MONTH -> {
				// 月模式：计算新月份的对应日期，尽量保持同一天
				val newMonth = CalendarDataCalculator.getMonthByOffset(startMonth, newPageIndex)
				val dayOfMonth = selectedDate.dayOfMonth.coerceAtMost(newMonth.lengthOfMonth())
				newMonth.atDay(dayOfMonth)
			}
			CalendarMode.WEEK -> {
				// 周模式：计算新周的对应日期，保持同一星期几
				val weekStartDate = CalendarDataCalculator.getDateByWeekIndex(
					_startDate, newPageIndex, _firstDayOfWeek
				)
				// 保持星期几不变
				val dayOffset = selectedDate.dayOfWeek.value - _firstDayOfWeek.value
				val adjustedOffset = if (dayOffset < 0) dayOffset + 7 else dayOffset
				weekStartDate.plusDays(adjustedOffset.toLong())
			}
		}

		// 只有日期真正改变时才更新
		if (newDate != selectedDate && newDate >= _startDate && newDate <= _endDate) {
			selectedDate = newDate
			Log.d(TAG, "onPageChanged: pageIndex=$newPageIndex, newDate=$newDate")
		}
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

		// 根据当前 Pager 模式计算目标页面
		val targetPageIndex = calCurrentPage(_calendarMode, date)
		Log.d(TAG, "scrollToDate: 切换日期, 新日期: $date, 当前模式$_calendarMode, 总pagerIndex: $pageCount 新pagerIndex: $targetPageIndex")

		if (animate) {
			pagerState.animateScrollToPage(targetPageIndex)
		} else {
			pagerState.scrollToPage(targetPageIndex)
		}
	}

	// ============ 模式切换 ============

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

		// 执行弹簧动画到目标值
		progressAnimatable.animateTo(
			targetValue = targetProgress,
			animationSpec = ModeTransitionAnimationSpec
		)

		// 动画完成后切换 Pager 模式
		switchPagerMode(targetMode)
	}

	/**
	 * 切换 Pager 的分页模式
	 * @param newPagerMode 目标分页模式
	 */
	private suspend fun switchPagerMode(newPagerMode: CalendarMode) {
		if (_calendarMode == newPagerMode) return

		// 设置标志位，防止 onPageChanged 错误更新 selectedDate
		_isSwitchingMode = true

		// 切换分页模式
		_calendarMode = newPagerMode

		// 计算新模式下的目标页面索引
		val targetPageIndex = calCurrentPage(newPagerMode, selectedDate)

		Log.d(TAG, "switchPagerMode: 当前模式: ${_calendarMode}, 总索引${pageCount}, 当前page索引: $targetPageIndex")
		// 跳转到目标页面
		pagerState.scrollToPage(targetPageIndex)

		// 清除标志位
		_isSwitchingMode = false
	}

	private fun calCurrentPage(
		mode: CalendarMode,
		targetDate: LocalDate,
	): Int = when (mode) {
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
	 * 配置变化时重建缓存管理器
	 */
	private fun onConfigChanged() {
		cacheManager = CalendarDataCacheManager(
			startDate = _startDate,
			startMonth = startMonth,
			firstDayOfWeek = _firstDayOfWeek,
			outDateStyle = _outDateStyle
		)
		Log.d(TAG, "Configuration changed, cache rebuilt")
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
