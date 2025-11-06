package com.xliiicxiv.scrapper.state

import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.DptResult

data class DptState(

    val questionBottomSheet: Boolean = false,

    val extendedMenu: Boolean = false,

    val sheetUri: Uri? = null,
    val sheetName: String? = null,

    val rawList: List<DptResult> = emptyList(),

    val deleteXlsxBottomSheet: Boolean = false,

    val isStarted: Boolean = false,
    val stopBottomSheet: Boolean = false,

    val process: Int = 0,
    val success: Int = 0,
    val failure: Int = 0,

    val dptResult: List<DptResult> = emptyList(),
)
