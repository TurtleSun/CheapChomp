package com.example.cheapchomp.ui.state

sealed interface RegistrationUiState {
    data object Initial : RegistrationUiState
    data object Loading : RegistrationUiState
    data class Success(val message: String) : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState
}