package com.xliiicxiv.scrapper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.HomeAction
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.state.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dataStore: DataStore,
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.getUserId.collect { userId ->
                _state.update { it.copy(userId = userId) }
                firebaseRepository.getUserById(userId).collect { userData ->
                    _state.update { it.copy(userData = userData) }
                    Log.d("HomeViewModel", "User data: ${userData}")
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.LogoutBottomSheet -> {
                _state.update { it.copy(logoutBottomSheet = !it.logoutBottomSheet) }
            }
            HomeAction.Logout -> {
                viewModelScope.launch {
                    dataStore.setUserId("")
                }
            }
        }
    }

}