package com.example.fitpulsegym.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitpulsegym.domain.usecase.BookSlotUseCase
import com.example.fitpulsegym.domain.usecase.GetHomeDashboardUseCase
import com.example.fitpulsegym.domain.usecase.GetScheduleUseCase
import com.example.fitpulsegym.domain.usecase.HomeDashboardData
import com.example.fitpulsegym.domain.model.GymClass
import com.example.fitpulsegym.ui.state.GymPulseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/** Home Dashboard VM — membership + upcoming + suggestions via single use-case. */
@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
  getDashboard: GetHomeDashboardUseCase,
  repo: com.example.fitpulsegym.data.repository.FitPulseRepository
) : ViewModel() {
  val gymConfig = repo.gymConfigFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Demo user; AuthRepository.currentUser() wires the real id in production.
  val state: StateFlow<GymPulseUiState<HomeDashboardData>> =
    getDashboard("member-001")
      .map { GymPulseUiState.Success(it) as GymPulseUiState<HomeDashboardData> }
      .catch { emit(GymPulseUiState.Error(it.message ?: "Failed to load dashboard")) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GymPulseUiState.Loading)
}

/** Schedule VM v2 — explicit Loading/Success/Empty/Error + refresh + book. */
@HiltViewModel
class GymPulseScheduleViewModel @Inject constructor(
  private val getSchedule: GetScheduleUseCase,
  private val bookSlot: BookSlotUseCase
) : ViewModel() {
  private val _day = MutableStateFlow(0)
  val day: StateFlow<Int> = _day.asStateFlow()
  private val _bookingMsg = MutableStateFlow<String?>(null)
  val bookingMsg: StateFlow<String?> = _bookingMsg.asStateFlow()

  fun setDay(d: Int) { _day.value = d.coerceIn(0, 6) }

  fun scheduleForDay(dayIndex: Int): StateFlow<GymPulseUiState<List<GymClass>>> =
    getSchedule(dayIndex).map { list ->
      if (list.isEmpty()) GymPulseUiState.Empty
      else GymPulseUiState.Success(list.sortedBy { it.startHour * 60 + it.startMinute })
    }.catch { emit(GymPulseUiState.Error(it.message ?: "Schedule failed to load")) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GymPulseUiState.Loading) as StateFlow<GymPulseUiState<List<GymClass>>>

  fun refresh() {
    viewModelScope.launch {
      getSchedule.refresh().onFailure { _bookingMsg.value = "Offline — showing cached schedule" }
    }
  }

  fun book(classId: String) {
    viewModelScope.launch {
      bookSlot(classId, "member-001")
        .onSuccess { _bookingMsg.value = "Booked (${it.status})" }
        .onFailure { _bookingMsg.value = it.message }
    }
  }

  fun clearMsg() { _bookingMsg.value = null }
}
