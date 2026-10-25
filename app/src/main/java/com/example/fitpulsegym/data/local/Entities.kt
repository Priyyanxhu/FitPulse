package com.example.fitpulsegym.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_classes")
data class GymClassEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String,
  val trainerName: String,
  val trainerBio: String,
  val dayIndex: Int,
  val startHour: Int,
  val startMinute: Int,
  val durationMin: Int,
  val capacity: Int,
  val bookedCount: Int,
  val station: String,
  val isCancelled: Boolean
)

@Entity(tableName = "bookings")
data class BookingEntity(
  @PrimaryKey val id: String,
  val classId: String,
  val classTitle: String,
  val category: String,
  val trainerName: String,
  val dayIndex: Int,
  val timeLabel: String,
  val status: String,
  val qrToken: String,
  val createdAt: Long
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
  @PrimaryKey val id: String,
  val classTitle: String,
  val dateMillis: Long,
  val calories: Int,
  val notes: String,
  val personalBest: Boolean
)

@Entity(tableName = "alerts")
data class AlertEntity(
  @PrimaryKey val id: String,
  val message: String,
  val timestamp: Long,
  val read: Boolean
)

@Entity(tableName = "member_profile")
data class MemberProfileEntity(
  @PrimaryKey val memberId: String,
  val name: String,
  val email: String,
  val phone: String,
  val planName: String,
  val expiryEpochMillis: Long,
  val qrToken: String,
  val weightKg: Float = 74f,
  val heightCm: Float = 178f,
  val fitnessGoal: String = "Strength & Endurance",
  val waterIntakeMl: Int = 1750,
  val waterGoalMl: Int = 3000,
  val lockerNumber: String = "L-42"
)

@Entity(tableName = "payment_transactions")
data class PaymentTransactionEntity(
  @PrimaryKey val id: String,
  val planName: String,
  val amountInr: Int,
  val paymentMethod: String,
  val timestamp: Long,
  val invoiceNumber: String
)

@Entity(tableName = "membership_plans")
data class MembershipPlanEntity(
  @PrimaryKey val id: String,
  val name: String,
  val priceInr: Int,
  val durationDays: Int,
  val perks: String, // comma separated perks
  val isPopular: Boolean = false
)

@Entity(tableName = "gym_config")
data class GymConfigEntity(
  @PrimaryKey val id: String = "default_gym",
  val gymName: String = "FitPulse Gym & Boutique Studio",
  val address: String = "42 Silicon Avenue, 2nd Floor, Indiranagar",
  val phone: String = "+91 80 4123 4567",
  val announcement: String = "Welcome athletes! Hydration bar & new kettlebells are now live.",
  val wifiSsid: String = "FitPulse-HighSpeed",
  val wifiPass: String = "PulseFit2026",
  val openHours: String = "05:30 AM - 10:30 PM"
)

@Entity(tableName = "trainers")
data class TrainerEntity(
  @PrimaryKey val id: String,
  val name: String,
  val specialty: String,
  val bio: String,
  val phone: String = "+91 98765 00000"
)

@Entity(tableName = "gym_equipment")
data class EquipmentEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String, // Cardio, Strength, Free Weights, Machines
  val status: String,   // OPERATIONAL, SERVICE_DUE, UNDER_REPAIR
  val locationZone: String, // Main Floor, Cardio Deck, Studio A, Free Weights Area
  val lastServiceDate: String,
  val notes: String = ""
)

@Entity(tableName = "gym_expenses")
data class GymExpenseEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String, // Rent, Utilities, Salaries, Maintenance, Supplies
  val amountInr: Int,
  val timestamp: Long,
  val paidTo: String
)

@Entity(tableName = "gym_lockers")
data class GymLockerEntity(
  @PrimaryKey val lockerNumber: String, // L-01, L-02...
  val status: String, // AVAILABLE, OCCUPIED, MAINTENANCE
  val assignedMemberName: String? = null,
  val assignedMemberPhone: String? = null,
  val zone: String = "Main Locker Bay"
)

@Entity(tableName = "lead_inquiries")
data class LeadInquiryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val phone: String,
  val goal: String,
  val status: String, // NEW_INQUIRY, TRIAL_SCHEDULED, TRIAL_COMPLETED, CONVERTED, LOST
  val trialDate: String,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)


