package com.xliiicxiv.scrapper.action

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.dataclass.DptResult
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface DptAction {

    data object QuestionBottomSheet : DptAction

    data object ExtendedMenu : DptAction

    data class SheetUri(val uri: Uri?) : DptAction

    data class SheetName(val name: String) : DptAction

    data object DeleteXlsx : DptAction

    data object Success : DptAction

    data object Failure : DptAction

    data object Process : DptAction

    data class AddResult(val result: DptResult) : DptAction

    data object IsStarted : DptAction

    data class MessageDialog(val color: Color ,val icon: ImageVector, val message: String) : DptAction

}