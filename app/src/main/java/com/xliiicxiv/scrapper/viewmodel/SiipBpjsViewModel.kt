package com.xliiicxiv.scrapper.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.SiipBpjsAction
import com.xliiicxiv.scrapper.effect.SiipBpjsEffect
import com.xliiicxiv.scrapper.effect.SiipBpjsEffect.*
import com.xliiicxiv.scrapper.extension.getDataForSiip
import com.xliiicxiv.scrapper.state.SiipBpjsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SiipBpjsViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _state = MutableStateFlow(SiipBpjsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SiipBpjsEffect>()
    val effect = _effect.asSharedFlow()

    fun onAction(action: SiipBpjsAction) {
        when (action) {
            is SiipBpjsAction.IsLoggedIn -> {
                _state.update { it.copy(isLoggedIn = action.isLoggedIn) }
            }
            SiipBpjsAction.QuestionBottomSheet -> {
                _state.update { it.copy(questionBottomSheet = !it.questionBottomSheet) }
            }
            SiipBpjsAction.ExtendedMenu -> {
                _state.update { it.copy(extendedMenu = !it.extendedMenu) }
            }
            is SiipBpjsAction.SheetUri -> {
                _state.update { it.copy(sheetUri = action.uri) }
                viewModelScope.launch {
                    if (action.uri != null) {
                        getDataForSiip(context, action.uri).collect { rawString ->
                            _state.update { it.copy(rawList = it.rawList + rawString) }
                        }
                    }
                }
            }
            is SiipBpjsAction.SheetName -> {
                _state.update { it.copy(sheetName = action.name) }
            }
            SiipBpjsAction.DeleteXlsx -> {
                _state.update { it.copy(
                    sheetName = null,
                    sheetUri = null,
                    process = 0,
                    success = 0,
                    failure = 0,
                    rawList = emptyList(),
                ) }
            }
            is SiipBpjsAction.RawList -> {
                _state.update { it.copy(rawList = action.rawList) }
            }
            SiipBpjsAction.IsStarted -> {
                _state.update { it.copy(isStarted = !it.isStarted) }
                if (_state.value.isStarted) {
                    _state.update {
                        it.copy(
                            process = 0,
                            success = 0,
                            failure = 0,
                            siipResult = emptyList(),
                        )
                    }
                }
            }
            SiipBpjsAction.DeleteXlsxBottomSheet -> {
                _state.update { it.copy(deleteXlsxBottomSheet = !it.deleteXlsxBottomSheet) }
            }
            SiipBpjsAction.StopBottomSheet -> {
                _state.update { it.copy(stopBottomSheet = !it.stopBottomSheet) }
            }
            SiipBpjsAction.Success -> {
                _state.update { it.copy(success = it.success + 1) }
            }
            SiipBpjsAction.Failure -> {
                _state.update { it.copy(failure = it.failure + 1) }
            }
            is SiipBpjsAction.AddResult -> {
                _state.update { it.copy(siipResult = it.siipResult + action.result) }
            }
            SiipBpjsAction.Process -> {
                _state.update { it.copy(process = it.process + 1) }
            }
            is SiipBpjsAction.ShowSnackbar -> {
                viewModelScope.launch {
                    _effect.emit(ShowSnackbar(action.message))
                }
            }
        }
    }
}