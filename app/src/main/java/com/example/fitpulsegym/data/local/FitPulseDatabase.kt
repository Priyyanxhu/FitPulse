package com.example.fitpulsegym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [
    GymClassEntity::class,
    BookingEntity::class,
    WorkoutLogEntity::class,
    AlertEntity::class,
    MemberProfileEntity::class,
    PaymentTransactionEntity::class,
    MembershipPlanEntity::class,
    GymConfigEntity::class,
    TrainerEntity::class,
    EquipmentEntity::class,
    GymExpenseEntity::class,
    GymLockerEntity::class,
    LeadInquiryEntity::class
  ],
  version = 4,
  exportSchema = false
)
abstract class FitPulseDatabase : RoomDatabase() {
  abstract fun gymClassDao(): GymClassDao
  abstract fun bookingDao(): BookingDao
  abstract fun workoutLogDao(): WorkoutLogDao
  abstract fun alertDao(): AlertDao
  abstract fun memberDao(): MemberDao
  abstract fun transactionDao(): TransactionDao
  abstract fun planDao(): PlanDao
  abstract fun gymConfigDao(): GymConfigDao
  abstract fun trainerDao(): TrainerDao
  abstract fun equipmentDao(): EquipmentDao
  abstract fun expenseDao(): ExpenseDao
  abstract fun lockerDao(): LockerDao
  abstract fun leadDao(): LeadDao
}
