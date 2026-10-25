package com.example.fitpulsegym

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FitPulseApp : Application() {
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        "booking_alerts",
        "Booking alerts",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply { description = "Waitlist promotions and booking confirmations" }
      val nm = getSystemService(NotificationManager::class.java)
      nm?.createNotificationChannel(channel)
    }
  }
}
