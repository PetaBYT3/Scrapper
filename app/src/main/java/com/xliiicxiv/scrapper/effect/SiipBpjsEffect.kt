package com.xliiicxiv.scrapper.effect

sealed interface SiipBpjsEffect {

    data class ShowSnackbar(val message: String) : SiipBpjsEffect

}