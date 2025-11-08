package com.xliiicxiv.scrapper.state

import com.xliiicxiv.scrapper.dataclass.UserDataClass

data class HomeState(

    val userId: String = "",

    val userData: UserDataClass? = null,

    val logoutBottomSheet: Boolean = false

)
