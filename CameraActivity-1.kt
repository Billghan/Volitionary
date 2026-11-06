package com.example.tasklocker

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = intent.getIntExtra("TASK_ID", -1)

        setContent {
            CameraScreen(taskId = taskId, onProofCaptured = {
                // Görevi tamamlandı olarak işaretle
                TaskManager.markTaskCompleted(this, taskId)
                Toast.makeText(this, "Kanıt doğrulandı! Kilit kaldırıldı.", Toast.LENGTH_SHORT).show()
                finish()
            })
        }
    }
}

@Composable
fun CameraScreen(taskId: Int, onProofCaptured: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Canlı Kamera Önizlemesi (Galeriden dosya seçme seçeneği yok)
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Anlık Çekim ve Onay Butonu
        Button(
            onClick = {
                val capture = imageCapture ?: return@Button
                val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

                capture.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        image.close()
                        onProofCaptured()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(context, "Hata oluştu!", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Anlık Kanıt Çek ve Kilidi Aç")
        }
    }
}