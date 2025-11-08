package com.xliiicxiv.scrapper.state

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.dataclass.SiipResult
import com.xliiicxiv.scrapper.ui.theme.Warning

data class SiipBpjsState(

    val isLoggedIn: Boolean = true,

    val questionBottomSheet: Boolean = false,

    val extendedMenu: Boolean = false,

    val sheetUri: Uri? = null,
    val sheetName: String? = null,

    val rawList: List<String> = emptyList(),

    val deleteXlsxBottomSheet: Boolean = false,

    val isStarted: Boolean = false,
    val stopBottomSheet: Boolean = false,

    val process: Int = 0,
    val success: Int = 0,
    val failure: Int = 0,

    val siipResult: List<SiipResult> = emptyList(),

    val dialogVisibility: Boolean = false,
    val dialogColor: Color = Warning,
    val iconDialog: ImageVector = Icons.Filled.Warning,
    val messageDialog: String = ""

)
