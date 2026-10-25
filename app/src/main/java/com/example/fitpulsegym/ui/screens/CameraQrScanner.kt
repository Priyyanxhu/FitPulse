package com.example.fitpulsegym.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Daily Attendance Tracker — CameraX + ML Kit QR scanner.
 * Uses LifecycleCameraController (no manual surfaceProvider wiring).
 */
@Composable
fun CameraQrScanner(onScanned: (String) -> Unit, modifier: Modifier = Modifier) {
  val ctx = LocalContext.current
  val lifecycle = LocalLifecycleOwner.current
  var hasCam by remember {
    mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
  }
  val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasCam = granted }
  LaunchedEffect(Unit) { if (!hasCam) permLauncher.launch(Manifest.permission.CAMERA) }

  if (!hasCam) {
    Column(modifier.fillMaxWidth()) {
      Text("Camera permission needed for QR check-in.", style = MaterialTheme.typography.bodySmall)
      Spacer(Modifier.height(6.dp))
      Button(onClick = { permLauncher.launch(Manifest.permission.CAMERA) }) { Text("Grant camera") }
    }
    return
  }

  val scanner = remember { BarcodeScanning.getClient() }
  val executor = remember { Executors.newSingleThreadExecutor() }
  val controller = remember {
    LifecycleCameraController(ctx).apply {
      cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
      setImageAnalysisAnalyzer(executor) { imageProxy ->
        val media = imageProxy.image
        if (media != null) {
          val input = InputImage.fromMediaImage(media, imageProxy.imageInfo.rotationDegrees)
          scanner.process(input)
            .addOnSuccessListener { codes ->
              codes.firstOrNull()?.rawValue?.takeIf { it.isNotBlank() }?.let(onScanned)
            }
            .addOnCompleteListener { imageProxy.close() }
        } else imageProxy.close()
      }
    }
  }
  LaunchedEffect(lifecycle) { controller.bindToLifecycle(lifecycle) }

  AndroidView(
    modifier = modifier.fillMaxWidth().height(220.dp),
    factory = { c -> PreviewView(c).also { it.controller = controller } }
  )
}
