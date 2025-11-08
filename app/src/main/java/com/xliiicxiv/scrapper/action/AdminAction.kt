package com.xliiicxiv.scrapper.action

import com.xliiicxiv.scrapper.dataclass.UserDataClass

sealed interface AdminAction {

    data object AddBottomSheet : AdminAction

    data object AddUser : AdminAction

    data class DeleteBottomSheet(val userData: UserDataClass?) : AdminAction

    data object DeleteUser : AdminAction

    data class UserName(val name: String) : AdminAction

    data class UserPassword(val password: String) : AdminAction

    data class UserRole(val role: String) : AdminAction

}