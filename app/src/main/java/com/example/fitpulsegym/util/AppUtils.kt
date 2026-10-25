package com.example.fitpulsegym.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fitpulsegym.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.TimeUnit

object QrCodeGenerator {
  fun makeImageBitmap(content: String, sizePx: Int = 640): ImageBitmap {
    val writer = QRCodeWriter()
    val matrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
    for (x in 0 until sizePx) for (y in 0 until sizePx) {
      bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
    }
    return bmp.asImageBitmap()
  }
}

fun daysUntil(expiryMillis: Long): Long {
  val diff = expiryMillis - System.currentTimeMillis()
  return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
}

fun pushLocalNotification(ctx: Context, title: String, text: String) {
  try {
    val notif = NotificationCompat.Builder(ctx, "booking_alerts")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(text)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()
    NotificationManagerCompat.from(ctx).notify((System.currentTimeMillis() % 100000).toInt(), notif)
  } catch (_: SecurityException) {
    // POST_NOTIFICATIONS not granted — in-app alert list still shows the message.
  }
}
