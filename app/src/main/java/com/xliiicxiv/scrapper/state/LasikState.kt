package com.xliiicxiv.scrapper.state

import android.net.Uri

data class LasikState(

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
)
