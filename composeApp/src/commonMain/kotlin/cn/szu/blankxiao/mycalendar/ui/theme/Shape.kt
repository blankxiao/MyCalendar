package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
	// 小组件：复选框、小按钮等
	extraSmall = RoundedCornerShape(4.dp),
	
	// 标准组件：按钮、输入框等
	small = RoundedCornerShape(8.dp),
	
	// 中等组件：卡片等
	medium = RoundedCornerShape(12.dp),
	
	// 大组件：对话框、底部表单等
	large = RoundedCornerShape(16.dp),
	
	// 超大组件：全屏弹窗等
	extraLarge = RoundedCornerShape(24.dp)
)

