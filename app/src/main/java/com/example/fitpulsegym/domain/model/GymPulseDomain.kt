package com.example.fitpulsegym.domain.model

/**
 * GymPulse Clean Architecture — Domain layer (framework-agnostic).
 * Presentation ↔ Domain ↔ Data. Domain never imports Room/Retrofit/Compose.
 */

enum class MembershipStatus { ACTIVE, EXPIRING_SOON, EXPIRED }

enum class SlotCategory { CROSSFIT, YOGA, WEIGHTLIFTING, ZUMBA, HIIT, SPIN, PILATES, BOXING }

data class User(
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val qrToken: String
)

data class Membership(
  val id: String,
  val userId: String,
  val planName: String,
  val startMillis: Long,
  val expiryMillis: Long,
  val isActive: Boolean = true
) {
  fun status(now: Long = System.currentTimeMillis()): MembershipStatus {
    if (!isActive || now > expiryMillis) return MembershipStatus.EXPIRED
    val daysLeft = (expiryMillis - now) / 86_400_000L
    return if (daysLeft <= 7) MembershipStatus.EXPIRING_SOON else MembershipStatus.ACTIVE
  }
  fun daysLeft(now: Long = System.currentTimeMillis()): Long =
    ((expiryMillis - now) / 86_400_000L).coerceAtLeast(0)
}

data class GymClass(
  val id: String,
  val title: String,
  val category: SlotCategory,
  val trainerId: String,
  val trainerName: String,
  val dayIndex: Int, // 0=Mon..6=Sun
  val startHour: Int,
  val startMinute: Int,
  val durationMin: Int,
  val capacity: Int,
  val bookedCount: Int,
  val station: String,
  val isCancelled: Boolean = false
) {
  val spotsLeft: Int get() = (capacity - bookedCount).coerceAtLeast(0)
  val isFull: Boolean get() = bookedCount >= capacity
  val timeLabel: String get() = "%02d:%02d".format(startHour, startMinute)
}

data class Booking(
  val id: String,
  val classId: String,
  val userId: String,
  val status: BookingStatus,
  val qrToken: String,
  val createdAt: Long
)

enum class BookingStatus { CONFIRMED, WAITLIST, CANCELLED, ATTENDED }
