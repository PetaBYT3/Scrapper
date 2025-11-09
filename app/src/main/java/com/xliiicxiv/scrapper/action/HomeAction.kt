package com.xliiicxiv.scrapper.action

sealed interface HomeAction {

    data object ProfileBottomSheet : HomeAction

    data object LogoutBottomSheet : HomeAction

    data object Logout : HomeAction

}