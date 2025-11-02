package com.xliiicxiv.scrapper.state

import android.net.Uri

data class SiipBpjsState(

    val isLoggedIn: Boolean = false,

    val extendedMenu: Boolean = false,

    val sheetUri: Uri? = null,
    val sheetName: String? = null,

    val rawList: List<String> = emptyList(),

    val isStarted: Boolean = false,

)
