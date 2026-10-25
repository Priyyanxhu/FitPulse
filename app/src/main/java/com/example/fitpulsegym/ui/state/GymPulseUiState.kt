package com.example.fitpulsegym.ui.state

/** Standard MVI-style screen state — every primary screen exposes one of these. */
sealed interface GymPulseUiState<out T> {
  data object Loading : GymPulseUiState<Nothing>
  data class Success<T>(val data: T) : GymPulseUiState<T>
  data object Empty : GymPulseUiState<Nothing>
  data class Error(val message: String) : GymPulseUiState<Nothing>
}

fun <T> List<T>.toUiState(emptyMessage: String = ""): GymPulseUiState<List<T>> =
  if (isEmpty()) GymPulseUiState.Empty else GymPulseUiState.Success(this)
