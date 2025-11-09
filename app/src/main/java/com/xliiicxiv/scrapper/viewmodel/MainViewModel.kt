package com.xliiicxiv.scrapper.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.extension.getAndroidId
import com.xliiicxiv.scrapper.repository.FirebaseRepository
import com.xliiicxiv.scrapper.route.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val dataStore: DataStore,
    private val firebaseRepository: FirebaseRepository
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _effect = MutableSharedFlow<Route>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            dataStore.getUserId.collect { userId ->

                checkUserExistence(userId)

                firebaseRepository.getUserById(userId).collect { userData ->
                    if (userData != null) {
                        checkAndroidId(userData.androidId)
                    }
                }
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

    private fun checkAndroidId(androidId: String) {
        viewModelScope.launch {
            val getAndroidId = getAndroidId(context)
            if (androidId == "" || androidId != getAndroidId) {
                dataStore.setUserId("")
                _effect.emit(Route.LoginPage)
            }
        }
    }
}