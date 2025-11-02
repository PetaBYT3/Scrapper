package com.xliiicxiv.scrapper.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xliiicxiv.scrapper.action.SiipBpjsAction
import com.xliiicxiv.scrapper.extension.parseSingleColumnExcel
import com.xliiicxiv.scrapper.extension.parseXlsxFile
import com.xliiicxiv.scrapper.state.SiipBpjsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

class SiipBpjsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _state = MutableStateFlow(SiipBpjsState())
    val state = _state.asStateFlow()

    fun onAction(action: SiipBpjsAction) {
        when (action) {
            is SiipBpjsAction.IsLoggedIn -> {
                isLoggedIn(action.isLoggedIn)
            }
            SiipBpjsAction.ExtendedMenu -> {
                extendedMenu()
            }
            is SiipBpjsAction.SheetUri -> {
                sheetUri(action.uri)
            }
            is SiipBpjsAction.SheetName -> {
                sheetName(action.name)
            }
            SiipBpjsAction.DeleteSheet -> {
                deleteSheet()
            }
            is SiipBpjsAction.RawList -> {
                rawList(action.rawList)
            }
            SiipBpjsAction.IsStarted -> {
                isStarted()
            }
        }
    }

    private fun isLoggedIn(isLoggedIn: Boolean) {
        _state.update { it.copy(isLoggedIn = isLoggedIn) }
    }

    private fun extendedMenu() {
        _state.update { it.copy(extendedMenu = !it.extendedMenu) }
    }

    private fun sheetUri(uri: Uri?) {
        _state.update { it.copy(sheetUri = uri) }
        viewModelScope.launch {
            if (uri != null) {
                parseSingleColumnExcel(context, uri).collect { rawString ->
                    _state.update { it.copy(rawList = it.rawList + rawString) }
                }
            }
        }
    }

    private fun sheetName(name: String) {
        _state.update { it.copy(sheetName = name) }
    }

    private fun deleteSheet() {
        _state.update { it.copy(
            sheetName = null,
            sheetUri = null,
            rawList = emptyList()
        ) }
    }

    private fun rawList(rawList: List<String>) {
        _state.update { it.copy(rawList = rawList) }
    }

    private fun isStarted() {
        _state.update { it.copy(isStarted = !it.isStarted) }
    }
}