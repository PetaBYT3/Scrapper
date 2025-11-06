package com.xliiicxiv.scrapper.action

import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface LasikAction {

    data object QuestionBottomSheet : LasikAction

    data object ExtendedMenu : LasikAction

    data class SheetUri(val uri: Uri?) : LasikAction

    data class SheetName(val name: String) : LasikAction

    data object DeleteXlsx : LasikAction

    data object DeleteXlsxBottomSheet : LasikAction

    data class RawList(val rawList: List<String>) : LasikAction

    data object IsStarted : LasikAction

    data object StopBottomSheet : LasikAction

    data object Success : LasikAction

    data object Failure : LasikAction

    data object Process : LasikAction

    data class AddResult(val result: SiipResult) : LasikAction

    data class ShowSnackbar(val message: String) : LasikAction

}