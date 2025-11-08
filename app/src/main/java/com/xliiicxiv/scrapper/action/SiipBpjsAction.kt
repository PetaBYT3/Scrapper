package com.xliiicxiv.scrapper.action

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.dataclass.SiipResult

sealed interface SiipBpjsAction {

    data class IsLoggedIn(val isLoggedIn: Boolean) : SiipBpjsAction

    data object QuestionBottomSheet : SiipBpjsAction

    data object ExtendedMenu : SiipBpjsAction

    data class SheetUri(val uri: Uri?) : SiipBpjsAction

    data class SheetName(val name: String) : SiipBpjsAction

    data object DeleteXlsx : SiipBpjsAction

    data object Success : SiipBpjsAction

    data object Failure : SiipBpjsAction

    data object Process : SiipBpjsAction

    data class AddResult(val result: SiipResult) : SiipBpjsAction

    data object IsStarted : SiipBpjsAction

    data class MessageDialog(val color: Color ,val icon: ImageVector, val message: String) : SiipBpjsAction

}