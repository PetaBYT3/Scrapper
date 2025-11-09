package com.xliiicxiv.scrapper.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.AdminAction
import com.xliiicxiv.scrapper.dataclass.UserDataClass
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.state.AdminState
import com.xliiicxiv.scrapper.string.isExist
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

class AdminViewModel(
    private val dataStore: DataStore,
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

        viewModelScope.launch {
            dataStore.getUserId.collect { userId ->
                firebaseRepository.getUserById(userId).collect { userData ->
                    _state.update { it.copy(userData = userData) }
                }
            }
        }
    }

    fun onAction(action: AdminAction) {
        when (action) {
            is AdminAction.UserName -> {
                _state.update { it.copy(userName = action.name) }
            }
            is AdminAction.UserPassword -> {
                _state.update { it.copy(userPassword = action.password) }
            }
            is AdminAction.UserRole -> {
                _state.update { it.copy(userRole = action.role) }
            }
            AdminAction.AddBottomSheet -> {
                _state.update { it.copy(addBottomSheet = !it.addBottomSheet) }

                val addBottomSheet = _state.value.addBottomSheet
                if (!addBottomSheet) {
                    _state.update { it.copy(
                        userName = "",
                        userPassword = "",
                        userRole = ""
                    ) }
                }
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

                    when (firebaseResult) {
                        isSuccess -> {
                            showDialog(
                                dialogColor = Success,
                                iconDialog = Icons.Filled.Check,
                                messageDialog = "User successfully added !"
                            )
                            _state.update { it.copy(
                                userName = "",
                                userPassword = "",
                                userRole = ""
                            ) }
                        }
                        isFail -> {
                            showDialog(
                                dialogColor = Warning,
                                iconDialog = Icons.Filled.Warning,
                                messageDialog = "Something went wrong !"
                            )
                        }
                        isExist -> {
                            showDialog(
                                dialogColor = Warning,
                                iconDialog = Icons.Filled.Warning,
                                messageDialog = "Username already exist !"
                            )
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
            is AdminAction.MessageDialog -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        dialogVisibility = true,
                        dialogColor = action.color,
                        iconDialog = action.icon,
                        messageDialog = action.message
                    ) }
                    delay(5_000)
                    _state.update { it.copy(
                        dialogVisibility = false,
                    ) }
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