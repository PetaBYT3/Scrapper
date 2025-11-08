package com.xliiicxiv.scrapper.action

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.dataclass.LasikResult
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface LasikAction {

    data object QuestionBottomSheet : LasikAction

    data object ExtendedMenu : LasikAction

    data class SheetUri(val uri: Uri?) : LasikAction

    data class SheetName(val name: String) : LasikAction

    data object DeleteXlsx : LasikAction

    data object Success : LasikAction

    data object Failure : LasikAction

    data object Process : LasikAction

    data class AddResult(val result: LasikResult) : LasikAction

    data object IsStarted : LasikAction

    data class MessageDialog(val color: Color ,val icon: ImageVector, val message: String) : LasikAction

}