package com.xliiicxiv.scrapper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.AdminAction
import com.xliiicxiv.scrapper.dataclass.UserDataClass
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.state.AdminState
import com.xliiicxiv.scrapper.string.isExist
import com.xliiicxiv.scrapper.string.isFail
import com.xliiicxiv.scrapper.string.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseRepository.getUser().collect { userList ->
                _state.update { it.copy(userList = userList) }
            }
        }
    }

    fun onAction(action: AdminAction) {
        when (action) {
            AdminAction.AddBottomSheet -> {
                _state.update { it.copy(addBottomSheet = !it.addBottomSheet) }
            }
            AdminAction.AddUser -> {
                viewModelScope.launch {
                    val userData = UserDataClass(
                        userId = "",
                        userName = _state.value.userName,
                        userPassword = _state.value.userPassword,
                        userRole = _state.value.userRole
                    )
                    val firebaseResult = firebaseRepository.addUser(userData)

                    if (_state.value.userName.isBlank()) {
                        _state.update { it.copy(warningAddMessage = "Username cannot be empty", warningAdd = true) }
                        delay(5_000)
                        _state.update { it.copy(warningAddMessage = "", warningAdd = false) }
                    } else if (_state.value.userPassword.isBlank()) {
                        _state.update { it.copy(warningAddMessage = "Password cannot be empty", warningAdd = true) }
                        delay(5_000)
                        _state.update { it.copy(warningAddMessage = "", warningAdd = false) }
                    } else if (_state.value.userRole.isBlank()) {
                        _state.update { it.copy(warningAddMessage = "Please choose a role", warningAdd = true) }
                        delay(5_000)
                        _state.update { it.copy(warningAddMessage = "", warningAdd = false) }
                    } else {
                        val message = when (firebaseResult) {
                            isFail -> "Failed to add user"
                            isExist -> "Username already exist"
                            else -> "Something went wrong"
                        }
                        _state.update { it.copy(warningAddMessage = message, warningAdd = true) }
                        delay(5_000)
                        _state.update { it.copy(warningAddMessage = "", warningAdd = false) }

                        if (firebaseResult == isSuccess) {
                            _state.update { it.copy(isAddSuccess = true) }
                            delay(500)
                            _state.update { it.copy(isAddSuccess = false) }
                        }
                    }
                }
            }
            is AdminAction.DeleteBottomSheet -> {
                _state.update { it.copy(
                    deleteBottomSheet = !it.deleteBottomSheet,
                    userToDelete = action.userData
                ) }

                if (!_state.value.deleteBottomSheet) {
                    _state.update { it.copy(userToDelete = null) }
                }
            }
            AdminAction.DeleteUser -> {
                val userToDelete = _state.value.userToDelete
                if (userToDelete != null) {
                    firebaseRepository.deleteUser(userToDelete)
                }
            }
            is AdminAction.UserName -> {
                _state.update { it.copy(userName = action.name) }
            }
            is AdminAction.UserPassword -> {
                _state.update { it.copy(userPassword = action.password) }
            }
            is AdminAction.UserRole -> {
                _state.update { it.copy(userRole = action.role) }
            }
        }
    }
}