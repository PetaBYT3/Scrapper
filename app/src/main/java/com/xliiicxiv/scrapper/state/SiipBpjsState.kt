package com.xliiicxiv.scrapper.state

import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.SiipResult

data class SiipBpjsState(

    val isLoggedIn: Boolean = true,

    val extendedMenu: Boolean = false,

    val sheetUri: Uri? = null,
    val sheetName: String? = null,

    val rawList: List<String> = emptyList(),

    val isStarted: Boolean = false,
    val stopBottomSheet: Boolean = false,

    val success : Int = 0,
    val failure : Int = 0,

    val process: Int = 0,

    val siipResult: List<SiipResult> = emptyList()

)
