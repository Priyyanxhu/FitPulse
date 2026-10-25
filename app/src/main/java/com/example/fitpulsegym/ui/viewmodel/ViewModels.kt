package com.example.fitpulsegym.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitpulsegym.data.local.BookingEntity
import com.example.fitpulsegym.data.model.BookingStatus
import com.example.fitpulsegym.data.model.ClassCategory
import com.example.fitpulsegym.data.model.GymClass
import com.example.fitpulsegym.data.repository.FitPulseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: FitPulseRepository) : ViewModel() {
  private val _loggedIn = MutableStateFlow(false)
  val loggedIn: StateFlow<Boolean> = _loggedIn.asStateFlow()
  private val _role = MutableStateFlow("member") // member | admin
  val role: StateFlow<String> = _role.asStateFlow()

  init { viewModelScope.launch { repo.ensureSeeded() } }

  /** Mock phone/email login. Swap with FirebaseAuth.signInWith... in production. */
  fun login(role: String = "member") {
    _role.value = role
    _loggedIn.value = true
  }
  fun logout() { _loggedIn.value = false }
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(repo: FitPulseRepository) : ViewModel() {
  private val _day = MutableStateFlow(0)
  val day: StateFlow<Int> = _day.asStateFlow()
  private val _query = MutableStateFlow("")
  val query: StateFlow<String> = _query.asStateFlow()
  private val _cat = MutableStateFlow<ClassCategory?>(null)
  val category: StateFlow<ClassCategory?> = _cat.asStateFlow()

  val classes = repo.classesFlow
  val member = repo.memberFlow
  val alerts = repo.alertsFlow

  val filtered: StateFlow<List<GymClass>> = combine(classes, _day, _query, _cat) { all, d, q, c ->
    all.filter { it.dayIndex == d }
      .filter { c == null || it.category == c }
      .filter { q.isBlank() || it.title.contains(q, true) || it.trainerName.contains(q, true) }
      .sortedBy { it.startHour * 60 + it.startMinute }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setDay(d: Int) { _day.value = d.coerceIn(0, 6) }
  fun setQuery(q: String) { _query.value = q }
  fun setCategory(c: ClassCategory?) { _cat.value = c }
}

@HiltViewModel
class BookingViewModel @Inject constructor(private val repo: FitPulseRepository) : ViewModel() {
  val bookings: StateFlow<List<BookingEntity>> = repo.bookingsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  private val _msg = MutableStateFlow<String?>(null)
  val message: StateFlow<String?> = _msg.asStateFlow()

  fun book(classId: String, onNotify: (String, String) -> Unit = { _, _ -> }) {
    viewModelScope.launch {
      val cls = repo.getClass(classId)
      repo.bookClass(classId).onSuccess { (status, pos) ->
        val text = if (status == BookingStatus.CONFIRMED) "Booked: ${cls?.title}" else "Waitlist #$pos: ${cls?.title}"
        _msg.value = text
        onNotify("FitPulse booking", text)
      }.onFailure { _msg.value = it.message }
    }
  }

  fun cancel(bookingId: String, classId: String) {
    viewModelScope.launch {
      repo.cancelBooking(bookingId, classId)
      _msg.value = "Booking cancelled"
    }
  }
  fun clearMsg() { _msg.value = null }
  fun markRead() { viewModelScope.launch { repo.markAlertsRead() } }
}

@HiltViewModel
class AdminViewModel @Inject constructor(private val repo: FitPulseRepository) : ViewModel() {
  val classes = repo.classesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val bookings = repo.bookingsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val members = repo.membersFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val plans = repo.plansFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val gymConfig = repo.gymConfigFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
  val trainers = repo.trainersFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val equipment = repo.equipmentFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val expenses = repo.expensesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val lockers = repo.lockersFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val leads = repo.leadsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val transactions = repo.transactionsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _scanResult = MutableStateFlow<String?>(null)
  val scanResult: StateFlow<String?> = _scanResult.asStateFlow()

  fun setCapacity(id: String, cap: Int) { viewModelScope.launch { repo.updateCapacity(id, cap) } }
  fun setCancelled(id: String, cancelled: Boolean) { viewModelScope.launch { repo.setCancelled(id, cancelled) } }
  fun deleteClass(id: String) { viewModelScope.launch { repo.deleteClass(id) } }
  fun addClass(title: String, cat: ClassCategory, trainer: String, day: Int, h: Int, m: Int, dur: Int, cap: Int, station: String) {
    viewModelScope.launch { repo.addClass(title, cat, trainer, day, h, m, dur, cap, station) }
  }

  // Member CRM
  fun renewMember(memberId: String, days: Int) { viewModelScope.launch { repo.renewMemberFromAdmin(memberId, days) } }
  fun addWalkInMember(name: String, phone: String, email: String, planName: String, days: Int, locker: String) {
    viewModelScope.launch { repo.addWalkInMember(name, phone, email, planName, days, locker) }
  }
  fun deleteMember(memberId: String) { viewModelScope.launch { repo.deleteMember(memberId) } }

  // Plan management
  fun addOrUpdatePlan(id: String?, name: String, priceInr: Int, days: Int, perks: String, isPopular: Boolean) {
    viewModelScope.launch { repo.addOrUpdatePlan(id, name, priceInr, days, perks, isPopular) }
  }
  fun deletePlan(planId: String) { viewModelScope.launch { repo.deletePlan(planId) } }

  // Studio config & broadcast
  fun updateGymConfig(name: String, address: String, phone: String, announcement: String, wifiSsid: String, wifiPass: String, openHours: String) {
    viewModelScope.launch { repo.updateGymConfig(name, address, phone, announcement, wifiSsid, wifiPass, openHours) }
  }
  fun broadcastAnnouncement(message: String) {
    viewModelScope.launch { repo.broadcastAnnouncement(message) }
  }

  // Trainer roster
  fun addTrainer(name: String, specialty: String, bio: String, phone: String) {
    viewModelScope.launch { repo.addTrainer(name, specialty, bio, phone) }
  }
  fun deleteTrainer(id: String) { viewModelScope.launch { repo.deleteTrainer(id) } }

  // Equipment & Maintenance
  fun addEquipment(name: String, category: String, status: String, zone: String, lastService: String, notes: String) {
    viewModelScope.launch { repo.addEquipment(name, category, status, zone, lastService, notes) }
  }
  fun updateEquipmentStatus(id: String, status: String, notes: String) {
    viewModelScope.launch { repo.updateEquipmentStatus(id, status, notes) }
  }
  fun deleteEquipment(id: String) { viewModelScope.launch { repo.deleteEquipment(id) } }

  // Expenses & Financials
  fun addExpense(title: String, category: String, amountInr: Int, paidTo: String) {
    viewModelScope.launch { repo.addExpense(title, category, amountInr, paidTo) }
  }
  fun deleteExpense(id: String) { viewModelScope.launch { repo.deleteExpense(id) } }

  // Lockers
  fun assignLocker(locker: String, name: String, phone: String) {
    viewModelScope.launch { repo.assignLocker(locker, name, phone) }
  }
  fun releaseLocker(locker: String) {
    viewModelScope.launch { repo.releaseLocker(locker) }
  }
  fun setLockerStatus(locker: String, status: String) {
    viewModelScope.launch { repo.setLockerStatus(locker, status) }
  }

  // Leads & Pipeline
  fun addLead(name: String, phone: String, goal: String, trialDate: String, notes: String) {
    viewModelScope.launch { repo.addLead(name, phone, goal, trialDate, notes) }
  }
  fun updateLeadStatus(id: String, status: String) {
    viewModelScope.launch { repo.updateLeadStatus(id, status) }
  }
  fun deleteLead(id: String) { viewModelScope.launch { repo.deleteLead(id) } }

  suspend fun getAttendeesForClass(classId: String) = repo.getAttendeesForClass(classId)

  // Check-in
  fun scan(token: String) {
    viewModelScope.launch {
      repo.markAttendedByToken(token).onSuccess { _scanResult.value = "✅ $it" }
        .onFailure { _scanResult.value = "❌ ${it.message}" }
    }
  }
  fun clearScan() { _scanResult.value = null }
}

@HiltViewModel
class PassViewModel @Inject constructor(repo: FitPulseRepository) : ViewModel() {
  val member = repo.memberFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
  val bookings = repo.bookingsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val gymConfig = repo.gymConfigFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

@HiltViewModel
class ProgressViewModel @Inject constructor(private val repo: FitPulseRepository) : ViewModel() {
  val logs = repo.logsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val bookings = repo.bookingsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val member = repo.memberFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  fun logWorkout(title: String, calories: Int, notes: String, isPb: Boolean) {
    viewModelScope.launch { repo.logWorkout(title, calories, notes, isPb) }
  }

  fun deleteWorkout(id: String) {
    viewModelScope.launch { repo.deleteWorkout(id) }
  }

  fun addWater(ml: Int) {
    viewModelScope.launch { repo.addWaterIntake(ml) }
  }

  fun resetWater() {
    viewModelScope.launch { repo.resetWaterIntake() }
  }

  fun updateProfile(name: String, email: String, phone: String, weight: Float, height: Float, goal: String, locker: String) {
    viewModelScope.launch { repo.updateProfile(name, email, phone, weight, height, goal, locker) }
  }
}

@HiltViewModel
class MembershipViewModel @Inject constructor(private val repo: FitPulseRepository) : ViewModel() {
  val member = repo.memberFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
  val transactions = repo.transactionsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val livePlans = repo.plansFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun buy(planName: String, days: Int) { viewModelScope.launch { repo.extendMembership(planName, days) } }
  fun buyWithPayment(planName: String, days: Int, priceInr: Int, paymentMethod: String) {
    viewModelScope.launch { repo.extendMembershipWithPayment(planName, days, priceInr, paymentMethod) }
  }
}

