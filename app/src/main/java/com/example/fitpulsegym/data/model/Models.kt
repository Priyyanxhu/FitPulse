package com.example.fitpulsegym.data.model

enum class BookingStatus { CONFIRMED, WAITLIST, CANCELLED, ATTENDED }

enum class ClassCategory(val label: String, val emoji: String) {
  HIIT("HIIT", "\uD83D\uDD25"),
  YOGA("Yoga", "\uD83E\uDDD8"),
  SPIN("Spin", "\uD83D\uDEB4"),
  CROSSFIT("CrossFit", "\uD83C\uDFCB\uFE0F"),
  STRENGTH("Strength", "\uD83D\uDCAA"),
  PILATES("Pilates", "\uD83E\uDD38"),
  BOXING("Boxing", "\uD83E\uDD4A"),
  DANCE("Zumba", "\uD83D\uDC83")
}

data class GymClass(
  val id: String,
  val title: String,
  val category: ClassCategory,
  val trainerName: String,
  val trainerBio: String,
  val dayIndex: Int, // 0=Mon .. 6=Sun
  val startHour: Int,
  val startMinute: Int,
  val durationMin: Int,
  val capacity: Int,
  val bookedCount: Int,
  val station: String,
  val isCancelled: Boolean = false
) {
  val spotsLeft: Int get() = (capacity - bookedCount).coerceAtLeast(0)
  val isFull: Boolean get() = bookedCount >= capacity || isCancelled.not().not()
  val timeLabel: String get() = "%02d:%02d".format(startHour, startMinute)
  val endLabel: String get() {
    val total = startHour * 60 + startMinute + durationMin
    return "%02d:%02d".format((total / 60) % 24, total % 60)
  }
}

data class Booking(
  val id: String,
  val classId: String,
  val classTitle: String,
  val category: ClassCategory,
  val trainerName: String,
  val dayIndex: Int,
  val timeLabel: String,
  val status: BookingStatus,
  val qrToken: String,
  val createdAt: Long
)

data class MemberProfile(
  val memberId: String = "member-001",
  val name: String = "Aarav Sharma",
  val email: String = "aarav@example.com",
  val phone: String = "+91 98765 43210",
  val planName: String = "Pro Monthly",
  val expiryEpochMillis: Long = System.currentTimeMillis() + 18L * 24 * 60 * 60 * 1000,
  val qrToken: String = "FITPULSE-member-001-PRO"
)

data class WorkoutLog(
  val id: String,
  val classTitle: String,
  val dateMillis: Long,
  val calories: Int,
  val notes: String,
  val personalBest: Boolean
)

data class MembershipPlan(
  val id: String,
  val name: String,
  val priceInr: Int,
  val durationDays: Int,
  val perks: List<String>
)

data class AppAlert(
  val id: String,
  val message: String,
  val timestamp: Long,
  val read: Boolean = false
)

fun dayName(index: Int): String = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").getOrElse(index) { "Day" }
fun dayFullName(index: Int): String = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").getOrElse(index) { "" }
