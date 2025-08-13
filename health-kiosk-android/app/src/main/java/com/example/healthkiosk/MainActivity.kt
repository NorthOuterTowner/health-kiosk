package com.example.healthkiosk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showDialog by remember { mutableStateOf(true) }

            if (showDialog) {
                val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
                LoginDialog(
                    userViewModel = userViewModel,
                    onDismiss = { showDialog = false },
                    cameraExecutor = cameraExecutor,
                    onSubmit = { behavior ->
                        when(behavior){
                            1 -> {
                                println("登录按钮被点击")
                            }
                            2 -> {
                                println("注册按钮被点击")
                            }
                        }
                        showDialog = false
                    }
                )
            }
            MainScreen(userViewModel)
        }
    }
}

@Composable
fun MainScreen(userViewModel: UserViewModel) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val name = userViewModel.name
            val id = userViewModel.id
            val age = userViewModel.age
            Text("姓名：" + name + " ID: " + id + " 年龄: " + age, fontWeight = FontWeight.Bold, fontSize = 10.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { /*TODO*/ }) {
                    Text("停止", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /*TODO*/ }) {
                    Text("提示", fontSize = 10.sp)
                }
            }

            Text(currentTime, fontSize = 10.sp)
        }

        Box(modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
            .padding(24.dp)
            .background(Color.Black),
            contentAlignment = Alignment.Center
        ){
            Text("[具体数据区域]", color = Color.White)
        }
        /**
         * TODO: 此处需根据传感器的测量数据绘制折线图
         */
        // 横向布局：左边是标签+数据，右边是摄像头
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 占据剩余高度
        ) {
            // 左边：体征标签+数据
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // 标签行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("体温", "酒精", "血压", "情绪", "HRV").forEach {
                        Text(it, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 数据行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    /**
                     * TODO:当前为模拟数据进行的硬编码，需根据传感器数据进行实时获取
                     */
                    listOf("36.5℃", "0.00mg/L", "120/80", "愉快", "65ms").forEach {
                        Text(it)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // 左右间隔

            // 右边：摄像头区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                /**
                 * TODO: 此处需请求摄像头权限
                 */
                Text("[摄像头预览区域]", color = Color.White)
            }

        }
    }

}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

