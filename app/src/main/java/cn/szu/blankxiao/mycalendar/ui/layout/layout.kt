package cn.szu.blankxiao.mycalendar.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author BlankXiao
 * @description layout
 * @date 2025-11-03 20:44
 */


@Composable
fun Footer(){


}

@Composable
fun FooterItem(text: String){
	Column(
		modifier = Modifier.padding(10.dp),
		verticalArrangement = Arrangement.SpaceAround,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = text,
			style = MaterialTheme.typography.bodyLarge)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewFooterItem(){
	FooterItem("日历")
}

