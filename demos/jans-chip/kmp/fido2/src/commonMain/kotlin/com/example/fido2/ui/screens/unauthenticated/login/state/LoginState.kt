package com.example.fido2.ui.screens.unauthenticated.login.state

import com.example.fido2.ui.common.state.ErrorState

/**
 * Login State holding ui input values
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val errorState: LoginErrorState = LoginErrorState(),
    val isLoginSuccessful: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Error state in login holding respective
 * text field validation errors
 */
data class LoginErrorState(
    val emailOrMobileErrorState: ErrorState = ErrorState(),
    val passwordErrorState: ErrorState = ErrorState()
)

