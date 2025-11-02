package com.xliiicxiv.scrapper.action

import android.net.Uri

interface SiipBpjsAction {

    data class IsLoggedIn(val isLoggedIn: Boolean) : SiipBpjsAction

    data object ExtendedMenu : SiipBpjsAction

    data class SheetUri(val uri: Uri?) : SiipBpjsAction

    data class SheetName(val name: String) : SiipBpjsAction

    data object DeleteSheet : SiipBpjsAction

    data class RawList(val rawList: List<String>) : SiipBpjsAction

    data object IsStarted : SiipBpjsAction

}