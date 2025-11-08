package com.xliiicxiv.scrapper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.datastore.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val dataStore: DataStore
): ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.getUserId.collect {
                _userId.update { it }
            }
        }
    }

}