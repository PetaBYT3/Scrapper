package com.xliiicxiv.scrapper.action

sealed interface LoginAction {

    data class Username(val username: String) : LoginAction

    data class Password(val password: String) : LoginAction
    data object Login : LoginAction

}