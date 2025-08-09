package com.example.healthkiosk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

const val LOGIN_BY_INFO: Int = 1
const val REGISTER_FACE: Int = 2

@Composable
fun LoginDialog(
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onSubmit: (behavior:Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // 模拟头像
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(20.dp)
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "[摄像展示区域]", color = Color.White)
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = userViewModel.name,
                        onValueChange = { userViewModel.updateName(it) },
                        label = { Text("姓名") })
                    OutlinedTextField(
                        value = userViewModel.id,
                        onValueChange = { userViewModel.updateId(it) },
                        label = { Text("ID") })
                    OutlinedTextField(
                        value = userViewModel.age,
                        onValueChange = { userViewModel.updateAge(it) },
                        label = { Text("年龄") })
                    OutlinedTextField(
                        value = userViewModel.sex,
                        onValueChange = { userViewModel.updateSex(it) },
                        label = { Text("性别") })
                    OutlinedTextField(
                        value = userViewModel.height,
                        onValueChange = { userViewModel.updateHeight(it) },
                        label = { Text("身高") })
                    OutlinedTextField(
                        value = userViewModel.weight,
                        onValueChange = { userViewModel.updateWeight(it) },
                        label = { Text("体重") })
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Button(modifier = Modifier,
                            onClick = {
                                coroutineScope.launch {
                                    val res = ApiService.login("admin", "111")
                                }
                                //ApiService.login("admin","111")
                                onSubmit(LOGIN_BY_INFO)
                            }
                        ){
                            Text("登录")
                        }
                        Button(modifier = Modifier,
                            onClick = {
                                onSubmit(REGISTER_FACE)
                            }
                        ) {
                            Text("注册")
                        }
                    }
                }
            }
        }
    }
}
