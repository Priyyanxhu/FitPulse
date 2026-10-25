package com.example.fitpulsegym.data.repository

import com.example.fitpulsegym.data.local.FitPulseDatabase
import com.example.fitpulsegym.data.mapper.toDomain
import com.example.fitpulsegym.data.mapper.toDomainMembership
import com.example.fitpulsegym.data.mapper.toDomainUser
import com.example.fitpulsegym.data.mapper.toEntity
import com.example.fitpulsegym.data.remote.GymPulseApi
import com.example.fitpulsegym.domain.model.Booking
import com.example.fitpulsegym.domain.model.BookingStatus
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.domain.model.Membership
import com.example.fitpulsegym.domain.model.User
import com.example.fitpulsegym.domain.repository.AuthRepository
import com.example.fitpulsegym.domain.repository.BookingRepository
import com.example.fitpulsegym.domain.repository.MembershipRepository
import com.example.fitpulsegym.domain.repository.ScheduleRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Sample offline-first Repository implementations.
 * Pattern: observe Room → refresh via Retrofit → upsert cache.
 * On network failure we keep serving cache (see refreshSchedule).
 */
@Singleton
class RoomScheduleRepository @Inject constructor(
  private val db: FitPulseDatabase,
  private val api: GymPulseApi
) : ScheduleRepository {
  override fun observeClasses(dayIndex: Int?): Flow<List<GymClass>> =
    if (dayIndex == null) db.gymClassDao().observeAll().map { l -> l.map { it.toDomain() } }
    else db.gymClassDao().observeByDay(dayIndex).map { l -> l.map { it.toDomain() } }

  override suspend fun refreshSchedule(): Result<Unit> = runCatching {
    val remote = api.getSchedule()
    if (remote.isNotEmpty()) {
      db.gymClassDao().upsertAll(remote.map { it.toDomain().toEntity() })
    }
  }.recover { Unit } // offline-first: swallow network errors, keep cache

  override suspend fun addSlot(slot: GymClass): Result<Unit> = runCatching {
    db.gymClassDao().upsert(slot.toEntity())
  }

  override suspend fun updateCapacity(classId: String, capacity: Int): Result<Unit> = runCatching {
    db.gymClassDao().getById(classId)?.let { db.gymClassDao().upsert(it.copy(capacity = capacity.coerceAtLeast(1))) }
      ?: throw IllegalArgumentException("Slot not found")
  }

  override suspend fun setCancelled(classId: String, cancelled: Boolean): Result<Unit> = runCatching {
    db.gymClassDao().getById(classId)?.let { db.gymClassDao().upsert(it.copy(isCancelled = cancelled)) }
      ?: throw IllegalArgumentException("Slot not found")
  }
}

@Singleton
class RoomBookingRepository @Inject constructor(private val db: FitPulseDatabase) : BookingRepository {
  override fun observeMyBookings(userId: String): Flow<List<Booking>> =
    db.bookingDao().observeAll().map { l -> l.map { it.toDomain(userId) } }

  override suspend fun bookSlot(classId: String, userId: String): Result<Booking> = runCatching {
    // Delegate to existing transactional helper for capacity/waitlist rules.
    val repo = FitPulseRepository(db)
    val (status, _) = repo.bookClass(classId).getOrThrow()
    val latest = db.bookingDao().observeAll().let { flow ->
      // read-back: fetch active booking we just created
      db.bookingDao().activeForClass(classId) ?: throw IllegalStateException("Booking failed")
    }
    Booking(latest.id, latest.classId, userId, BookingStatus.valueOf(latest.status), latest.qrToken, latest.createdAt)
  }

  override suspend fun cancelBooking(bookingId: String): Result<Unit> = runCatching {
    val existing = db.bookingDao().getById(bookingId) ?: throw IllegalArgumentException("Booking not found")
    FitPulseRepository(db).cancelBooking(bookingId, existing.classId)
  }

  override suspend fun markAttended(qrToken: String): Result<String> =
    FitPulseRepository(db).markAttendedByToken(qrToken)
}

@Singleton
class RoomMembershipRepository @Inject constructor(private val db: FitPulseDatabase) : MembershipRepository {
  override fun observeMembership(userId: String): Flow<Membership?> =
    db.memberDao().observe().map { it?.toDomainMembership() }

  override suspend fun purchasePlan(userId: String, planName: String, durationDays: Int): Result<Membership> = runCatching {
    FitPulseRepository(db).extendMembership(planName, durationDays)
    db.memberDao().get()?.toDomainMembership() ?: throw IllegalStateException("No member")
  }
}

@Singleton
class FakeAuthRepository @Inject constructor(private val db: FitPulseDatabase) : AuthRepository {
  override fun currentUser(): Flow<User?> = db.memberDao().observe().map { it?.toDomainUser() }
  override suspend fun loginWithPhoneOrEmail(identifier: String): Result<User> = runCatching {
    db.memberDao().get()?.toDomainUser() ?: throw IllegalStateException("No seeded member — call ensureSeeded() first")
  }
  override suspend fun logout() { }
}
