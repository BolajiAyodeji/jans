package com.example.fido2.ui.screens.unauthenticated.registration.state

import com.example.fido2.ui.common.state.ErrorState

/**
 * Registration State holding ui input values
 */
data class RegistrationState(
    val username: String = "",
    val password: String = "",
    val errorState: RegistrationErrorState = RegistrationErrorState(),
    val isRegistrationSuccessful: Boolean = false,
    val isValidationSuccessful: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Error state in registration holding respective
 * text field validation errors
 */
data class RegistrationErrorState(
    val emailIdErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState(),
)