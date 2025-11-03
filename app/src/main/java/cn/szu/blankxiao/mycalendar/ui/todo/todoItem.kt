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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.data.TodoItemData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author BlankXiao
 * @description todoItem
 * @date 2025-11-03 21:06
 */

const val checkedColor = 0xFF6200EE

const val uncheckedColor = 0xFF757575

val normalFontColor = 0xFF9E9E9E
val pastFontColor = 0xFF212121


@Composable
fun TodoItem(
	itemData: TodoItemData,
	onChecked: () -> Unit,
	modifier: Modifier = Modifier
) {
	val isChecked = remember { mutableStateOf(false) }

	// 日期格式化
	val dateFormatter = remember { SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA) }
	val formattedDate = dateFormatter.format(itemData.date)

	Card(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		),
		elevation = CardDefaults.cardElevation(
			defaultElevation = 2.dp
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
				checked = isChecked.value,
				onCheckedChange = { checked ->
					isChecked.value = checked
					onChecked()
				},
				colors = CheckboxDefaults.colors(
					checkedColor = Color(checkedColor),
					uncheckedColor = Color(uncheckedColor),
					checkmarkColor = Color.White
				)
			)

			Column(
				modifier = Modifier
					.weight(1f)
					.padding(start = 8.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				// 描述文本
				Text(
					text = itemData.desc,
					fontSize = 16.sp,
					fontWeight = FontWeight.Medium,
					color = if (isChecked.value) {
						Color(normalFontColor)
					} else {
						Color(pastFontColor)
					},
					// 划线
					textDecoration = if (isChecked.value) TextDecoration.LineThrough else TextDecoration.None
				)
				
				// 日期文本
				Text(
					text = formattedDate,
					fontSize = 14.sp,
					color = Color(uncheckedColor)
				)
			}
		}
	}
}


@Composable()
@Preview(showBackground = true)
fun PreviewTodoItem() {
	TodoItem(TodoItemData("吃饭", Date()), onChecked = {})
}

