package com.example.healthkiosk

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService

const val LOGIN_BY_INFO: Int = 1
const val REGISTER_FACE: Int = 2

private lateinit var imageCapture: ImageCapture

@Composable
fun CameraPreview(modifier: Modifier = Modifier,
                  cameraExecutor: ExecutorService) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                startCamera(previewView, context, lifecycleOwner, cameraExecutor)
                previewView
            }
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("需要相机权限", color = Color.White)
        }
    }
}

private fun startCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraExecutor: ExecutorService
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

@Composable
fun LoginDialog(
    userViewModel: UserViewModel,
    cameraExecutor: ExecutorService,
    onDismiss: () -> Unit,
    onSubmit: (behavior: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                /** District of photo */
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(3f / 4f)
                        .background(Color.Black)
                ) {
                    CameraPreview(modifier = Modifier.fillMaxSize(),cameraExecutor)
                }

                Spacer(modifier = Modifier.width(16.dp))

                /** District of login/register Form */
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = userViewModel.id,
                        onValueChange = { userViewModel.updateId(it) },
                        label = { Text("ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userViewModel.name,
                        onValueChange = { userViewModel.updateName(it) },
                        label = { Text("姓名") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userViewModel.pwd,
                        onValueChange = { userViewModel.updatePwd(it) },
                        label = { Text("密码") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Button( /** Login Button */
                            modifier = Modifier.weight(1f),
                            onClick = {
                                coroutineScope.launch {
                                    val res = ApiService.login(userViewModel.id, userViewModel.pwd)
                                    onSubmit(LOGIN_BY_INFO)
                                }
                            }
                        ) {
                            Text("登录")
                        }

                        val context = LocalContext.current
                        Button( /** Register Button */
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                imageCapture.takePicture(
                                    outputOptions,
                                    cameraExecutor,
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            coroutineScope.launch {
                                                try {
                                                    /**This part is using for examine whether the photo could work normally and get the picture from it.*/
                                                    /*val contentValues = ContentValues().apply {
                                                        put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
                                                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/HealthKiosk")
                                                    }

                                                    val resolver = context.contentResolver
                                                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                                    uri?.let {
                                                        resolver.openOutputStream(it)?.use { outputStream ->
                                                            photoFile.inputStream().copyTo(outputStream)
                                                        }
                                                    }*/

                                                    /**This is part is the code which works in productive environment*/
                                                    val res = ApiService.register(
                                                        userViewModel.id,
                                                        userViewModel.name,
                                                        userViewModel.pwd,
                                                        photoFile
                                                    )
                                                    onSubmit(REGISTER_FACE)
                                                } catch (e: Exception) {
                                                    Log.e("Register", "error: ${e.message}", e)
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e("Camera", "拍照失败: ${exception.message}", exception)
                                        }
                                    }
                                )
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