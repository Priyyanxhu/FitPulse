package com.example.fitpulsegym.data.remote

import kotlinx.serialization.Serializable

/** Retrofit + Kotlin Serialization DTOs (backend contract). */
@Serializable
data class GymClassDto(
  val id: String,
  val title: String,
  val category: String, // CROSSFIT | YOGA | WEIGHTLIFTING | ZUMBA ...
  val trainerId: String = "",
  val trainerName: String = "",
  val dayIndex: Int = 0,
  val startHour: Int = 6,
  val startMinute: Int = 0,
  val durationMin: Int = 60,
  val capacity: Int = 20,
  val bookedCount: Int = 0,
  val station: String = "",
  val isCancelled: Boolean = false
)

@Serializable
data class BookingDto(
  val id: String,
  val classId: String,
  val userId: String,
  val status: String, // CONFIRMED | WAITLIST | CANCELLED | ATTENDED
  val qrToken: String = "",
  val createdAt: Long = 0L
)

@Serializable
data class MembershipDto(
  val id: String,
  val userId: String,
  val planName: String,
  val startMillis: Long = 0L,
  val expiryMillis: Long = 0L,
  val isActive: Boolean = true
)

@Serializable
data class BookSlotRequest(val classId: String, val userId: String)
