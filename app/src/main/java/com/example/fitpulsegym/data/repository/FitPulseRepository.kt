package com.example.fitpulsegym.data.repository

import com.example.fitpulsegym.data.local.AlertEntity
import com.example.fitpulsegym.data.local.BookingEntity
import com.example.fitpulsegym.data.local.FitPulseDatabase
import com.example.fitpulsegym.data.local.GymClassEntity
import com.example.fitpulsegym.data.local.MemberProfileEntity
import com.example.fitpulsegym.data.local.WorkoutLogEntity
import com.example.fitpulsegym.data.model.BookingStatus
import com.example.fitpulsegym.data.model.ClassCategory
import com.example.fitpulsegym.data.model.GymClass
import com.example.fitpulsegym.data.seed.SeedData
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun GymClassEntity.toDomain() = GymClass(
  id = id,
  title = title,
  category = runCatching { ClassCategory.valueOf(category) }.getOrDefault(ClassCategory.HIIT),
  trainerName = trainerName,
  trainerBio = trainerBio,
  dayIndex = dayIndex,
  startHour = startHour,
  startMinute = startMinute,
  durationMin = durationMin,
  capacity = capacity,
  bookedCount = bookedCount,
  station = station,
  isCancelled = isCancelled
)

fun GymClass.toEntity() = GymClassEntity(id, title, category.name, trainerName, trainerBio, dayIndex, startHour, startMinute, durationMin, capacity, bookedCount, station, isCancelled)

/**
 * Single repository facade (Clean Architecture: data layer).
 * Room is the source of truth for offline-first UX.
 * Remote sync hooks (Firestore / REST) can be added here without touching ViewModels:
 *   fun syncFromFirestore(...) / pushBooking(...) — currently mocked with seed + local writes.
 */
@Singleton
class FitPulseRepository @Inject constructor(private val db: FitPulseDatabase) {

  val classesFlow: Flow<List<GymClass>> = db.gymClassDao().observeAll().map { list -> list.map { it.toDomain() } }

  fun classesForDay(day: Int): Flow<List<GymClass>> =
    db.gymClassDao().observeByDay(day).map { list -> list.map { it.toDomain() } }

  val bookingsFlow: Flow<List<BookingEntity>> = db.bookingDao().observeAll()
  val logsFlow: Flow<List<WorkoutLogEntity>> = db.workoutLogDao().observeAll()
  val alertsFlow: Flow<List<AlertEntity>> = db.alertDao().observeAll()
  val memberFlow: Flow<MemberProfileEntity?> = db.memberDao().observe()
  val membersFlow: Flow<List<MemberProfileEntity>> = db.memberDao().observeAll()
  val transactionsFlow: Flow<List<com.example.fitpulsegym.data.local.PaymentTransactionEntity>> = db.transactionDao().observeAll()
  val plansFlow: Flow<List<com.example.fitpulsegym.data.local.MembershipPlanEntity>> = db.planDao().observeAll()
  val gymConfigFlow: Flow<com.example.fitpulsegym.data.local.GymConfigEntity?> = db.gymConfigDao().observe()
  val trainersFlow: Flow<List<com.example.fitpulsegym.data.local.TrainerEntity>> = db.trainerDao().observeAll()
  val equipmentFlow: Flow<List<com.example.fitpulsegym.data.local.EquipmentEntity>> = db.equipmentDao().observeAll()
  val expensesFlow: Flow<List<com.example.fitpulsegym.data.local.GymExpenseEntity>> = db.expenseDao().observeAll()
  val lockersFlow: Flow<List<com.example.fitpulsegym.data.local.GymLockerEntity>> = db.lockerDao().observeAll()
  val leadsFlow: Flow<List<com.example.fitpulsegym.data.local.LeadInquiryEntity>> = db.leadDao().observeAll()

  suspend fun ensureSeeded() {
    if (db.gymConfigDao().get() == null) {
      db.gymClassDao().upsertAll(SeedData.classes())
      db.memberDao().upsert(SeedData.member())
      SeedData.logs().forEach { db.workoutLogDao().upsert(it) }
      SeedData.transactions().forEach { db.transactionDao().upsert(it) }
      db.planDao().upsertAll(SeedData.planEntities())
      db.gymConfigDao().upsert(SeedData.gymConfig())
      db.trainerDao().upsertAll(SeedData.trainers())
      db.equipmentDao().upsertAll(SeedData.equipments())
      db.expenseDao().upsertAll(SeedData.expenses())
      db.lockerDao().upsertAll(SeedData.lockers())
      db.leadDao().upsertAll(SeedData.leads())
      db.alertDao().insert(
        AlertEntity(UUID.randomUUID().toString(), "Welcome to FitPulse! Your studio management hub is active.", System.currentTimeMillis(), false)
      )
    }
  }

  suspend fun getClass(id: String): GymClass? = db.gymClassDao().getById(id)?.toDomain()

  /** One-tap booking: CONFIRMED if capacity left else WAITLIST. Returns status + position. */
  suspend fun bookClass(classId: String): Result<Pair<BookingStatus, Int>> {
    val existing = db.bookingDao().activeForClass(classId)
    if (existing != null) return Result.failure(IllegalStateException("Already booked (${existing.status})"))
    val entity = db.gymClassDao().getById(classId) ?: return Result.failure(IllegalStateException("Class not found"))
    if (entity.isCancelled) return Result.failure(IllegalStateException("Class cancelled by gym"))
    val confirmed = db.bookingDao().confirmedCount(classId)
    val status = if (confirmed < entity.capacity) BookingStatus.CONFIRMED else BookingStatus.WAITLIST
    val waitPos = if (status == BookingStatus.WAITLIST) db.bookingDao().waitlistCount(classId) + 1 else 0
    val booking = BookingEntity(
      id = UUID.randomUUID().toString(),
      classId = classId,
      classTitle = entity.title,
      category = entity.category,
      trainerName = entity.trainerName,
      dayIndex = entity.dayIndex,
      timeLabel = "%02d:%02d".format(entity.startHour, entity.startMinute),
      status = status.name,
      qrToken = "FITPULSE-${classId}-${UUID.randomUUID().toString().take(6).uppercase()}",
      createdAt = System.currentTimeMillis()
    )
    db.bookingDao().upsert(booking)
    if (status == BookingStatus.CONFIRMED) {
      db.gymClassDao().upsert(entity.copy(bookedCount = (entity.bookedCount + 1).coerceAtMost(entity.capacity + 50)))
    }
    val msg = if (status == BookingStatus.CONFIRMED) "Booked: ${entity.title} — spot confirmed!"
    else "Class full — you're #${waitPos} on the waitlist for ${entity.title}."
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), msg, System.currentTimeMillis(), false))
    return Result.success(status to waitPos)
  }

  suspend fun cancelBooking(bookingId: String, classId: String) {
    db.bookingDao().updateStatus(bookingId, BookingStatus.CANCELLED.name)
    val entity = db.gymClassDao().getById(classId)
    if (entity != null && entity.bookedCount > 0) {
      db.gymClassDao().upsert(entity.copy(bookedCount = entity.bookedCount - 1))
    }
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "Booking cancelled. A waitlist spot may have opened.", System.currentTimeMillis(), false))
    promoteWaitlist(classId)
  }

  private suspend fun promoteWaitlist(classId: String) {
    val waitCount = db.bookingDao().waitlistCount(classId)
    if (waitCount > 0) {
      val cls = db.gymClassDao().getById(classId)
      db.alertDao().insert(
        AlertEntity(UUID.randomUUID().toString(), "Good news! A spot opened in ${cls?.title ?: "your class"} — check in fast.", System.currentTimeMillis(), false)
      )
    }
  }

  suspend fun markAttendedByToken(token: String): Result<String> {
    val clean = token.trim()
    val member = db.memberDao().get()
    if (member != null && member.qrToken.equals(clean, ignoreCase = true)) {
      db.workoutLogDao().upsert(
        WorkoutLogEntity(UUID.randomUUID().toString(), "Gym check-in", System.currentTimeMillis(), 150, "Front-desk turnstile check-in", false)
      )
      return Result.success("Member ${member.name} checked in")
    }
    val booking = db.bookingDao().findByToken(clean)
      ?: return Result.failure(IllegalStateException("No booking found for this QR"))
    db.bookingDao().updateStatus(booking.id, BookingStatus.ATTENDED.name)
    db.workoutLogDao().upsert(
      WorkoutLogEntity(UUID.randomUUID().toString(), booking.classTitle, System.currentTimeMillis(), 420, "Attended via QR scan", false)
    )
    return Result.success("Marked ATTENDED: ${booking.classTitle}")
  }

  // ---- Workouts & Activity Logger ----
  suspend fun logWorkout(title: String, calories: Int, notes: String, isPersonalBest: Boolean) {
    val log = WorkoutLogEntity(
      id = "wl-${UUID.randomUUID().toString().take(8)}",
      classTitle = title,
      dateMillis = System.currentTimeMillis(),
      calories = calories,
      notes = notes,
      personalBest = isPersonalBest
    )
    db.workoutLogDao().upsert(log)
    val msg = if (isPersonalBest) "🔥 NEW PERSONAL BEST recorded for $title!" else "Logged workout: $title ($calories kcal)"
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), msg, System.currentTimeMillis(), false))
  }

  suspend fun deleteWorkout(id: String) {
    db.workoutLogDao().delete(id)
  }

  // ---- Wellness & Habits ----
  suspend fun addWaterIntake(amountMl: Int) {
    val m = db.memberDao().get() ?: return
    val updated = (m.waterIntakeMl + amountMl).coerceIn(0, 10000)
    db.memberDao().upsert(m.copy(waterIntakeMl = updated))
  }

  suspend fun resetWaterIntake() {
    val m = db.memberDao().get() ?: return
    db.memberDao().upsert(m.copy(waterIntakeMl = 0))
  }

  suspend fun updateProfile(
    name: String, email: String, phone: String,
    weightKg: Float, heightCm: Float, goal: String, locker: String
  ) {
    val m = db.memberDao().get() ?: return
    db.memberDao().upsert(
      m.copy(
        name = name,
        email = email,
        phone = phone,
        weightKg = weightKg,
        heightCm = heightCm,
        fitnessGoal = goal,
        lockerNumber = locker
      )
    )
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "Profile & body metrics updated.", System.currentTimeMillis(), false))
  }

  // ---- Admin: slot & capacity manager ----
  suspend fun updateCapacity(classId: String, newCapacity: Int) {
    val e = db.gymClassDao().getById(classId) ?: return
    db.gymClassDao().upsert(e.copy(capacity = newCapacity.coerceAtLeast(1)))
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "${e.title}: capacity updated to $newCapacity.", System.currentTimeMillis(), false))
  }

  suspend fun setCancelled(classId: String, cancelled: Boolean) {
    val e = db.gymClassDao().getById(classId) ?: return
    db.gymClassDao().upsert(e.copy(isCancelled = cancelled))
    db.alertDao().insert(
      AlertEntity(UUID.randomUUID().toString(), if (cancelled) "${e.title} cancelled by gym." else "${e.title} is back on schedule!", System.currentTimeMillis(), false)
    )
  }

  suspend fun deleteClass(classId: String) {
    db.gymClassDao().delete(classId)
  }

  suspend fun addClass(
    title: String, category: ClassCategory, trainer: String, day: Int,
    hour: Int, minute: Int, duration: Int, capacity: Int, station: String
  ) {
    val id = "c-${UUID.randomUUID().toString().take(8)}"
    db.gymClassDao().upsert(
      GymClassEntity(id, title, category.name, trainer, "Studio coach", day, hour, minute, duration, capacity, 0, station, false)
    )
  }

  suspend fun extendMembership(planName: String, days: Int) {
    extendMembershipWithPayment(planName, days, 2499, "UPI (Instant)")
  }

  suspend fun extendMembershipWithPayment(planName: String, days: Int, priceInr: Int, paymentMethod: String) {
    val m = db.memberDao().get() ?: return
    val base = maxOf(m.expiryEpochMillis, System.currentTimeMillis())
    val next = base + days * 24L * 60 * 60 * 1000
    db.memberDao().upsert(m.copy(planName = planName, expiryEpochMillis = next))
    val inv = "INV-2026-${(1000..9999).random()}"
    val tx = com.example.fitpulsegym.data.local.PaymentTransactionEntity(
      id = "tx-${UUID.randomUUID().toString().take(8)}",
      planName = planName,
      amountInr = priceInr,
      paymentMethod = paymentMethod,
      timestamp = System.currentTimeMillis(),
      invoiceNumber = inv
    )
    db.transactionDao().upsert(tx)
    db.alertDao().insert(
      AlertEntity(UUID.randomUUID().toString(), "$planName activated! $days days added via $paymentMethod. Invoice #$inv generated.", System.currentTimeMillis(), false)
    )
  }

  suspend fun renewMemberFromAdmin(memberId: String, days: Int) {
    val m = db.memberDao().get() ?: return
    val base = maxOf(m.expiryEpochMillis, System.currentTimeMillis())
    val next = base + days * 24L * 60 * 60 * 1000
    db.memberDao().upsert(m.copy(expiryEpochMillis = next))
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "Admin extended membership by $days days.", System.currentTimeMillis(), false))
  }

  suspend fun markAlertsRead() = db.alertDao().markAllRead()
  suspend fun clearAlerts() = db.alertDao().clear()

  // ---- Gym Owner: Membership Plans Management ----
  suspend fun addOrUpdatePlan(id: String?, name: String, priceInr: Int, days: Int, perks: String, isPopular: Boolean) {
    val planId = id ?: "plan-${UUID.randomUUID().toString().take(6)}"
    db.planDao().upsert(
      com.example.fitpulsegym.data.local.MembershipPlanEntity(planId, name, priceInr, days, perks, isPopular)
    )
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "Owner updated membership plan: $name (₹$priceInr).", System.currentTimeMillis(), false))
  }

  suspend fun deletePlan(planId: String) {
    db.planDao().delete(planId)
  }

  // ---- Gym Owner: Studio Config & Announcements ----
  suspend fun updateGymConfig(gymName: String, address: String, phone: String, announcement: String, wifiSsid: String, wifiPass: String, openHours: String) {
    db.gymConfigDao().upsert(
      com.example.fitpulsegym.data.local.GymConfigEntity("default_gym", gymName, address, phone, announcement, wifiSsid, wifiPass, openHours)
    )
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "Gym details & Wi-Fi settings updated.", System.currentTimeMillis(), false))
  }

  suspend fun broadcastAnnouncement(message: String) {
    val current = db.gymConfigDao().get() ?: SeedData.gymConfig()
    db.gymConfigDao().upsert(current.copy(announcement = message))
    db.alertDao().insert(
      AlertEntity(UUID.randomUUID().toString(), "📢 GYM BROADCAST: $message", System.currentTimeMillis(), false)
    )
  }

  // ---- Gym Owner: Trainer Roster Management ----
  suspend fun addTrainer(name: String, specialty: String, bio: String, phone: String) {
    val id = "t-${UUID.randomUUID().toString().take(6)}"
    db.trainerDao().upsert(
      com.example.fitpulsegym.data.local.TrainerEntity(id, name, specialty, bio, phone)
    )
    db.alertDao().insert(AlertEntity(UUID.randomUUID().toString(), "New trainer onboarded: $name ($specialty)", System.currentTimeMillis(), false))
  }

  suspend fun deleteTrainer(trainerId: String) {
    db.trainerDao().delete(trainerId)
  }

  // ---- Gym Owner: Walk-In Member Registration & Management ----
  suspend fun addWalkInMember(name: String, phone: String, email: String, planName: String, days: Int, locker: String) {
    val id = "member-${(100..999).random()}"
    val token = "FITPULSE-$id-${planName.take(3).uppercase()}"
    val newMember = MemberProfileEntity(
      memberId = id,
      name = name,
      email = email,
      phone = phone,
      planName = planName,
      expiryEpochMillis = System.currentTimeMillis() + days * 24L * 60 * 60 * 1000,
      qrToken = token,
      lockerNumber = locker
    )
    db.memberDao().upsert(newMember)
    db.alertDao().insert(
      AlertEntity(UUID.randomUUID().toString(), "New member registered: $name on $planName (Locker $locker)", System.currentTimeMillis(), false)
    )
  }

  suspend fun deleteMember(memberId: String) {
    db.memberDao().deleteMember(memberId)
  }

  suspend fun getAttendeesForClass(classId: String): List<BookingEntity> =
    db.bookingDao().getForClass(classId)

  // ---- Equipment & Maintenance Operations ----
  suspend fun addEquipment(name: String, category: String, status: String, zone: String, lastService: String, notes: String) {
    val id = "eq-${UUID.randomUUID().toString().take(6)}"
    db.equipmentDao().upsert(
      com.example.fitpulsegym.data.local.EquipmentEntity(id, name, category, status, zone, lastService, notes)
    )
  }

  suspend fun updateEquipmentStatus(id: String, status: String, notes: String) {
    db.equipmentDao().updateStatus(id, status, notes)
  }

  suspend fun deleteEquipment(id: String) {
    db.equipmentDao().delete(id)
  }

  // ---- Financial Expenses & Operations ----
  suspend fun addExpense(title: String, category: String, amountInr: Int, paidTo: String) {
    val id = "exp-${UUID.randomUUID().toString().take(6)}"
    db.expenseDao().upsert(
      com.example.fitpulsegym.data.local.GymExpenseEntity(id, title, category, amountInr, System.currentTimeMillis(), paidTo)
    )
  }

  suspend fun deleteExpense(id: String) {
    db.expenseDao().delete(id)
  }

  // ---- Locker Management Operations ----
  suspend fun assignLocker(locker: String, memberName: String, memberPhone: String) {
    db.lockerDao().assignLocker(locker, memberName, memberPhone)
  }

  suspend fun releaseLocker(locker: String) {
    db.lockerDao().releaseLocker(locker)
  }

  suspend fun setLockerStatus(locker: String, status: String) {
    db.lockerDao().updateStatus(locker, status)
  }

  // ---- Leads & Inquiry Pipeline Operations ----
  suspend fun addLead(name: String, phone: String, goal: String, trialDate: String, notes: String) {
    val id = "lead-${UUID.randomUUID().toString().take(6)}"
    db.leadDao().upsert(
      com.example.fitpulsegym.data.local.LeadInquiryEntity(id, name, phone, goal, "NEW_INQUIRY", trialDate, notes, System.currentTimeMillis())
    )
  }

  suspend fun updateLeadStatus(id: String, status: String) {
    db.leadDao().updateStatus(id, status)
  }

  suspend fun deleteLead(id: String) {
    db.leadDao().delete(id)
  }
}
