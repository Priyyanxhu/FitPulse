package com.example.fitpulsegym.domain.usecase

import com.example.fitpulsegym.domain.model.Booking
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.domain.model.Membership
import com.example.fitpulsegym.domain.repository.BookingRepository
import com.example.fitpulsegym.domain.repository.MembershipRepository
import com.example.fitpulsegym.domain.repository.ScheduleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Presentation calls use-cases (not repositories directly) — Clean Architecture rule. */
class GetScheduleUseCase @Inject constructor(private val repo: ScheduleRepository) {
  operator fun invoke(dayIndex: Int? = null): Flow<List<GymClass>> = repo.observeClasses(dayIndex)
  suspend fun refresh(): Result<Unit> = repo.refreshSchedule()
}

class BookSlotUseCase @Inject constructor(private val bookings: BookingRepository) {
  suspend operator fun invoke(classId: String, userId: String): Result<Booking> =
    bookings.bookSlot(classId, userId)
}

class GetMembershipUseCase @Inject constructor(private val repo: MembershipRepository) {
  operator fun invoke(userId: String): Flow<Membership?> = repo.observeMembership(userId)
}

data class HomeDashboardData(
  val membership: Membership?,
  val upcoming: List<Booking>,
  val suggested: List<GymClass>
)

class GetHomeDashboardUseCase @Inject constructor(
  private val membershipRepo: MembershipRepository,
  private val bookingRepo: BookingRepository,
  private val scheduleRepo: ScheduleRepository
) {
  operator fun invoke(userId: String, dayIndex: Int? = null): Flow<HomeDashboardData> =
    combine(
      membershipRepo.observeMembership(userId),
      bookingRepo.observeMyBookings(userId),
      scheduleRepo.observeClasses(dayIndex)
    ) { membership, bookings, classes ->
      val upcoming = bookings.filter { it.status.name == "CONFIRMED" || it.status.name == "WAITLIST" }.take(3)
      HomeDashboardData(membership, upcoming, classes.filter { !it.isCancelled }.take(5))
    }
}
