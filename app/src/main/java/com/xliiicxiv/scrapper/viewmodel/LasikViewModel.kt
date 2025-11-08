package com.xliiicxiv.scrapper.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.LasikAction
import com.xliiicxiv.scrapper.extension.getDataForLasik
import com.xliiicxiv.scrapper.state.LasikState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LasikViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _state = MutableStateFlow(LasikState())
    val state = _state.asStateFlow()

    fun onAction(action: LasikAction) {
        when (action) {
            LasikAction.QuestionBottomSheet -> {
                _state.update { it.copy(questionBottomSheet = !it.questionBottomSheet) }
            }
            LasikAction.ExtendedMenu -> {
                _state.update { it.copy(extendedMenu = !it.extendedMenu) }
            }
            is LasikAction.SheetUri -> {
                _state.update { it.copy(sheetUri = action.uri) }
                viewModelScope.launch {
                    if (action.uri != null) {
                        getDataForLasik(context, action.uri).collect { rawString ->
                            _state.update { it.copy(rawList = it.rawList + rawString) }
                        }
                    }
                }
            }
            is LasikAction.SheetName -> {
                _state.update { it.copy(sheetName = action.name) }
            }
            LasikAction.DeleteXlsx -> {
                _state.update { it.copy(
                    sheetName = null,
                    sheetUri = null,
                    process = 0,
                    success = 0,
                    failure = 0,
                    rawList = emptyList(),
                ) }
            }
            LasikAction.Process -> {
                _state.update { it.copy(process = it.process + 1) }
            }
            LasikAction.Success -> {
                _state.update { it.copy(success = it.success + 1) }
            }
            LasikAction.Failure -> {
                _state.update { it.copy(failure = it.failure + 1) }
            }
            is LasikAction.AddResult -> {
                _state.update { it.copy(lasikResult = it.lasikResult + action.result) }
            }
            LasikAction.IsStarted -> {
                _state.update { it.copy(isStarted = !it.isStarted) }

                val isStarted = _state.value.isStarted
                if (isStarted) {
                    _state.update { it.copy(
                        process = 0,
                        success = 0,
                        failure = 0,
                        lasikResult = emptyList()
                    ) }
                }
            }
            is LasikAction.MessageDialog -> {
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
}