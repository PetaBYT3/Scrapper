package com.xliiicxiv.scrapper.action

import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface SiipBpjsAction {

    data class IsLoggedIn(val isLoggedIn: Boolean) : SiipBpjsAction

    data object QuestionBottomSheet : SiipBpjsAction

    data object ExtendedMenu : SiipBpjsAction

    data class SheetUri(val uri: Uri?) : SiipBpjsAction

    data class SheetName(val name: String) : SiipBpjsAction

    data object DeleteXlsx : SiipBpjsAction

    data object DeleteXlsxBottomSheet : SiipBpjsAction

    data class RawList(val rawList: List<String>) : SiipBpjsAction

    data object IsStarted : SiipBpjsAction

    data object StopBottomSheet : SiipBpjsAction

    data object Success : SiipBpjsAction

    data object Failure : SiipBpjsAction

    data object Process : SiipBpjsAction

    data class AddResult(val result: SiipResult) : SiipBpjsAction

    data class ShowSnackbar(val message: String) : SiipBpjsAction

}