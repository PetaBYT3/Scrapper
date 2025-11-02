package com.xliiicxiv.scrapper.viewmodel

import androidx.lifecycle.ViewModel
import com.xliiicxiv.scrapper.action.LoginAction
import com.xliiicxiv.scrapper.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.Username -> {
                username(action.username)
            }
            is LoginAction.Password -> {
                password(action.password)
            }
            is LoginAction.Login -> {
                login()
            }
        }
    }

    private fun username(username: String) {
        _state.update { it.copy(username = username) }
    }

    private fun password(password: String) {
        _state.update { it.copy(password = password) }
    }

    private fun login() {

    }
}