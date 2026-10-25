package com.example.fitpulsegym.domain.repository

import com.example.fitpulsegym.domain.model.Booking
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.domain.model.Membership
import com.example.fitpulsegym.domain.model.User
import kotlinx.coroutines.flow.Flow

/** Auth — phone/email today, Firebase Auth / OAuth tomorrow. */
interface AuthRepository {
  fun currentUser(): Flow<User?>
  suspend fun loginWithPhoneOrEmail(identifier: String): Result<User>
  suspend fun logout()
}

/** Schedule — offline-first: Room cache, Retrofit refresh. */
interface ScheduleRepository {
  fun observeClasses(dayIndex: Int? = null): Flow<List<GymClass>>
  suspend fun refreshSchedule(): Result<Unit>
  suspend fun addSlot(slot: GymClass): Result<Unit>
  suspend fun updateCapacity(classId: String, capacity: Int): Result<Unit>
  suspend fun setCancelled(classId: String, cancelled: Boolean): Result<Unit>
}

/** Booking — one-tap book + waitlist promotion. */
interface BookingRepository {
  fun observeMyBookings(userId: String): Flow<List<Booking>>
  suspend fun bookSlot(classId: String, userId: String): Result<Booking>
  suspend fun cancelBooking(bookingId: String): Result<Unit>
  suspend fun markAttended(qrToken: String): Result<String>
}

/** Membership — pass + subscription. */
interface MembershipRepository {
  fun observeMembership(userId: String): Flow<Membership?>
  suspend fun purchasePlan(userId: String, planName: String, durationDays: Int): Result<Membership>
}
