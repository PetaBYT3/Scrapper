package com.xliiicxiv.scrapper.action

import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.DptResult
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface DptAction {

    data object QuestionBottomSheet : DptAction

    data object ExtendedMenu : DptAction

    data class SheetUri(val uri: Uri?) : DptAction

    data class SheetName(val name: String) : DptAction

    data object DeleteXlsx : DptAction

    data object DeleteXlsxBottomSheet : DptAction

    data class RawList(val rawList: List<String>) : DptAction

    data object IsStarted : DptAction

    data object StopBottomSheet : DptAction

    data object Success : DptAction

    data object Failure : DptAction

    data object Process : DptAction

    data class AddResult(val result: DptResult) : DptAction

    data class ShowSnackbar(val message: String) : DptAction

}