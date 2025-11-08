package com.xliiicxiv.scrapper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.route.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val dataStore: DataStore,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _effect = MutableSharedFlow<Route>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            dataStore.getUserId.collect { userId ->
                checkUserExistence(userId)
            }
        }
    }

    private fun checkUserExistence(userId: String) {
        viewModelScope.launch {
            firebaseRepository.checkUserExistence(userId).collect { isExist ->
                if (!isExist) {
                    dataStore.setUserId("")
                    _effect.emit(Route.LoginPage)
                }
            }
        }
    }

}