package com.xliiicxiv.scrapper.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.LoginAction
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.effect.LoginEffect
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.state.LoginState
import com.xliiicxiv.scrapper.string.LoginResult
import com.xliiicxiv.scrapper.string.isFail
import com.xliiicxiv.scrapper.string.isSuccess
import com.xliiicxiv.scrapper.ui.theme.Success
import com.xliiicxiv.scrapper.ui.theme.Warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val dataStore: DataStore
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.Username -> {
                _state.update { it.copy(username = action.username) }
            }
            is LoginAction.Password -> {
                _state.update { it.copy(password = action.password) }
            }
            is LoginAction.Login -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }

                    val userName = _state.value.username
                    val password = _state.value.password
                    val loginResult = firebaseRepository.login(userName, password)

                    when(loginResult) {
                        is LoginResult.Success -> {
                            showDialog(
                                dialogColor = Success,
                                iconDialog = Icons.Default.Check,
                                messageDialog = "Login successfully !"
                            )
                            dataStore.setUserId(loginResult.userId)
                            delay(500)
                            _effect.emit(LoginEffect.Navigate(Route.HomePage))
                        }
                        LoginResult.Fail -> {
                            showDialog(
                                dialogColor = Warning,
                                iconDialog = Icons.Default.Warning,
                                messageDialog = "Username or password incorrect !"
                            )
                        }
                    }
                    _state.update { it.copy(isLoading = false) }
                }
            }
            is LoginAction.MessageDialog -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        dialogVisibility = true,
                        dialogColor = action.color,
                        iconDialog = action.icon,
                        messageDialog = action.message
                    ) }
                    delay(5_000)
                    _state.update { it.copy(dialogVisibility = false) }
                }
            }
        }
    }

    private fun showDialog(
        dialogColor: Color,
        iconDialog: ImageVector,
        messageDialog: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(
                dialogVisibility = true,
                dialogColor = dialogColor,
                iconDialog = iconDialog,
                messageDialog = messageDialog
            ) }
            delay(5_000)
            _state.update { it.copy(dialogVisibility = false) }
        }
    }
}