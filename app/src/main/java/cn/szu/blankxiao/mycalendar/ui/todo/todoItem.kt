package cn.szu.blankxiao.mycalendar.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.data.TodoItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author BlankXiao
 * @description todoItem
 * @date 2025-11-03 21:06
 */

private const val TAG = "TodoItem"

@Composable
fun TodoItem(
	itemData: TodoItemData,
	modifier: Modifier = Modifier,
	onChecked: () -> Unit,
) {

	val customColors = MaterialTheme.customColors
	// 日期格式化
	val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.CHINA) }
	val formattedDate = itemData.date.format(dateFormatter)

	Card(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = Dimensions.Padding.medium, vertical = Dimensions.Padding.tiny),
		shape = RoundedCornerShape(Dimensions.CornerRadius.medium),
		colors = CardDefaults.cardColors(
			containerColor = customColors.todoCardBackground
		),
		elevation = CardDefaults.cardElevation(
			defaultElevation = Dimensions.Elevation.small
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = itemData.isChecked, onCheckedChange = { checked ->
					onChecked()
				}, colors = CheckboxDefaults.colors(
					checkedColor = customColors.todoCheckboxChecked,
					uncheckedColor = customColors.todoCheckboxUnchecked,
					checkmarkColor = customColors.todoCheckboxCheckmark
				)
			)

			Column(
				modifier = Modifier
					.weight(1f)
					.padding(start = Dimensions.Padding.tiny),
				verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.tiny)
			) {
				// 描述文本
				Text(
					text = itemData.title,
					style = Typography.titleSmall,
					fontWeight = FontWeight.Medium,
					color = if (itemData.isChecked) {
						customColors.todoCompletedText
					} else {
						customColors.todoUncompletedText
					},
					// 划线
					textDecoration = if (itemData.isChecked) TextDecoration.LineThrough else TextDecoration.None
				)

				// 日期文本
				Text(
					text = formattedDate, fontSize = 14.sp, color = customColors.todoDateText
				)
			}
		}
	}
}


@Composable()
@Preview(showBackground = true)
fun PreviewTodoItem() {
	var isChecked by remember { mutableStateOf(false) }
	MyCalendarTheme {
		TodoItem(TodoItemData("吃饭", LocalDate.now(), "", false)) {
			isChecked = !isChecked
		}
	}
}

