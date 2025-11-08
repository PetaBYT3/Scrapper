package com.xliiicxiv.scrapper.state

import com.xliiicxiv.scrapper.dataclass.UserDataClass

data class AdminState(

    val userList: List<UserDataClass> = emptyList(),

    val addBottomSheet: Boolean = false,
    val warningAdd: Boolean = false,
    val warningAddMessage: String = "",
    val isAddSuccess: Boolean = false,

    val deleteBottomSheet: Boolean = false,
    val userToDelete: UserDataClass? = null,

    val userName: String = "",
    val userPassword: String = "",
    val userRole: String = "",
)