package com.example.fitpulsegym.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymClassDao {
  @Query("SELECT * FROM gym_classes ORDER BY dayIndex, startHour, startMinute")
  fun observeAll(): Flow<List<GymClassEntity>>

  @Query("SELECT * FROM gym_classes WHERE dayIndex = :day ORDER BY startHour, startMinute")
  fun observeByDay(day: Int): Flow<List<GymClassEntity>>

  @Query("SELECT * FROM gym_classes WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): GymClassEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<GymClassEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: GymClassEntity)

  @Query("DELETE FROM gym_classes WHERE id = :id")
  suspend fun delete(id: String)

  @Query("DELETE FROM gym_classes")
  suspend fun clear()
}

@Dao
interface BookingDao {
  @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
  fun observeAll(): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE classId = :classId AND status IN ('CONFIRMED','WAITLIST') LIMIT 1")
  suspend fun activeForClass(classId: String): BookingEntity?

  @Query("SELECT COUNT(*) FROM bookings WHERE classId = :classId AND status = 'CONFIRMED'")
  suspend fun confirmedCount(classId: String): Int

  @Query("SELECT COUNT(*) FROM bookings WHERE classId = :classId AND status = 'WAITLIST'")
  suspend fun waitlistCount(classId: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: BookingEntity)

  @Query("UPDATE bookings SET status = :status WHERE id = :id")
  suspend fun updateStatus(id: String, status: String)

  @Query("DELETE FROM bookings WHERE id = :id")
  suspend fun delete(id: String)

  @Query("SELECT * FROM bookings WHERE qrToken = :token LIMIT 1")
  suspend fun findByToken(token: String): BookingEntity?

  @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): BookingEntity?

  @Query("SELECT * FROM bookings WHERE classId = :classId ORDER BY createdAt DESC")
  fun observeForClass(classId: String): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE classId = :classId ORDER BY createdAt DESC")
  suspend fun getForClass(classId: String): List<BookingEntity>
}

@Dao
interface WorkoutLogDao {
  @Query("SELECT * FROM workout_logs ORDER BY dateMillis DESC")
  fun observeAll(): Flow<List<WorkoutLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: WorkoutLogEntity)

  @Query("DELETE FROM workout_logs WHERE id = :id")
  suspend fun delete(id: String)

  @Query("DELETE FROM workout_logs")
  suspend fun clear()
}

@Dao
interface AlertDao {
  @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
  fun observeAll(): Flow<List<AlertEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: AlertEntity)

  @Query("UPDATE alerts SET read = 1")
  suspend fun markAllRead()

  @Query("DELETE FROM alerts")
  suspend fun clear()
}

@Dao
interface MemberDao {
  @Query("SELECT * FROM member_profile LIMIT 1")
  fun observe(): Flow<MemberProfileEntity?>

  @Query("SELECT * FROM member_profile LIMIT 1")
  suspend fun get(): MemberProfileEntity?

  @Query("SELECT * FROM member_profile")
  fun observeAll(): Flow<List<MemberProfileEntity>>

  @Query("SELECT * FROM member_profile")
  suspend fun getAll(): List<MemberProfileEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: MemberProfileEntity)

  @Query("UPDATE member_profile SET waterIntakeMl = :waterMl WHERE memberId = :memberId")
  suspend fun updateWater(memberId: String, waterMl: Int)

  @Query("DELETE FROM member_profile WHERE memberId = :memberId")
  suspend fun deleteMember(memberId: String)
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
  fun observeAll(): Flow<List<PaymentTransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: PaymentTransactionEntity)

  @Query("DELETE FROM payment_transactions")
  suspend fun clear()
}

@Dao
interface PlanDao {
  @Query("SELECT * FROM membership_plans ORDER BY priceInr ASC")
  fun observeAll(): Flow<List<MembershipPlanEntity>>

  @Query("SELECT * FROM membership_plans WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): MembershipPlanEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: MembershipPlanEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<MembershipPlanEntity>)

  @Query("DELETE FROM membership_plans WHERE id = :id")
  suspend fun delete(id: String)
}

@Dao
interface GymConfigDao {
  @Query("SELECT * FROM gym_config WHERE id = 'default_gym' LIMIT 1")
  fun observe(): Flow<GymConfigEntity?>

  @Query("SELECT * FROM gym_config WHERE id = 'default_gym' LIMIT 1")
  suspend fun get(): GymConfigEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: GymConfigEntity)
}

@Dao
interface TrainerDao {
  @Query("SELECT * FROM trainers ORDER BY name ASC")
  fun observeAll(): Flow<List<TrainerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: TrainerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<TrainerEntity>)

  @Query("DELETE FROM trainers WHERE id = :id")
  suspend fun delete(id: String)
}

@Dao
interface EquipmentDao {
  @Query("SELECT * FROM gym_equipment ORDER BY name ASC")
  fun observeAll(): Flow<List<EquipmentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: EquipmentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<EquipmentEntity>)

  @Query("UPDATE gym_equipment SET status = :status, notes = :notes WHERE id = :id")
  suspend fun updateStatus(id: String, status: String, notes: String)

  @Query("DELETE FROM gym_equipment WHERE id = :id")
  suspend fun delete(id: String)
}

@Dao
interface ExpenseDao {
  @Query("SELECT * FROM gym_expenses ORDER BY timestamp DESC")
  fun observeAll(): Flow<List<GymExpenseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: GymExpenseEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<GymExpenseEntity>)

  @Query("DELETE FROM gym_expenses WHERE id = :id")
  suspend fun delete(id: String)
}

@Dao
interface LockerDao {
  @Query("SELECT * FROM gym_lockers ORDER BY lockerNumber ASC")
  fun observeAll(): Flow<List<GymLockerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: GymLockerEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<GymLockerEntity>)

  @Query("UPDATE gym_lockers SET status = 'OCCUPIED', assignedMemberName = :name, assignedMemberPhone = :phone WHERE lockerNumber = :locker")
  suspend fun assignLocker(locker: String, name: String, phone: String)

  @Query("UPDATE gym_lockers SET status = 'AVAILABLE', assignedMemberName = NULL, assignedMemberPhone = NULL WHERE lockerNumber = :locker")
  suspend fun releaseLocker(locker: String)

  @Query("UPDATE gym_lockers SET status = :status WHERE lockerNumber = :locker")
  suspend fun updateStatus(locker: String, status: String)
}

@Dao
interface LeadDao {
  @Query("SELECT * FROM lead_inquiries ORDER BY createdAt DESC")
  fun observeAll(): Flow<List<LeadInquiryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: LeadInquiryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<LeadInquiryEntity>)

  @Query("UPDATE lead_inquiries SET status = :status WHERE id = :id")
  suspend fun updateStatus(id: String, status: String)

  @Query("DELETE FROM lead_inquiries WHERE id = :id")
  suspend fun delete(id: String)
}


