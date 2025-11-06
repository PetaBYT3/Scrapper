package com.xliiicxiv.scrapper.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.DptAction
import com.xliiicxiv.scrapper.extension.getKpjFromXlsx
import com.xliiicxiv.scrapper.extension.getNikFromXlsx
import com.xliiicxiv.scrapper.state.DptState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DptViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _state = MutableStateFlow(DptState())
    val state = _state.asStateFlow()

    fun onAction(action: DptAction) {
        when (action) {
            DptAction.QuestionBottomSheet -> {
                _state.update { it.copy(questionBottomSheet = !it.questionBottomSheet) }
            }
            DptAction.ExtendedMenu -> {
                _state.update { it.copy(extendedMenu = !it.extendedMenu) }
            }
            is DptAction.SheetUri -> {
                _state.update { it.copy(sheetUri = action.uri) }
                viewModelScope.launch {
                    if (action.uri != null) {
                        getNikFromXlsx(context, action.uri).collect { rawList ->
                            _state.update { it.copy(rawList = it.rawList + rawList) }
                        }
                    }
                }
            }
            is DptAction.SheetName -> {
                _state.update { it.copy(sheetName = action.name) }
            }
            DptAction.DeleteXlsx -> {
                _state.update { it.copy(
                    sheetName = null,
                    sheetUri = null,
                    process = 0,
                    success = 0,
                    failure = 0,
                    rawList = emptyList(),
                ) }
            }
            DptAction.DeleteXlsxBottomSheet -> {
                _state.update { it.copy(deleteXlsxBottomSheet = !it.deleteXlsxBottomSheet) }
            }
            is DptAction.RawList -> {
            }
            DptAction.IsStarted -> {
                _state.update { it.copy(isStarted = !it.isStarted) }
            }
            DptAction.StopBottomSheet -> {
                _state.update { it.copy(stopBottomSheet = !it.stopBottomSheet) }
            }
            DptAction.Process -> {
                _state.update { it.copy(process = it.process + 1) }
            }
            DptAction.Success -> {
                _state.update { it.copy(success = it.success + 1) }
            }
            DptAction.Failure -> {
                _state.update { it.copy(failure = it.failure + 1) }
            }
            is DptAction.AddResult -> {
                _state.update { it.copy(dptResult = it.dptResult + action.result) }
            }
            is DptAction.ShowSnackbar -> {

            }
        }
    }
}