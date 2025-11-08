package com.xliiicxiv.scrapper.string

import com.xliiicxiv.scrapper.dataclass.UserDataClass

const val isSuccess = "success"
const val isFail = "fail"
const val isExist = "exist"

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data object Fail : LoginResult()
}

sealed class UserDataResult {
    data class Success(val userData: UserDataClass?) : UserDataResult()
    data object Fail : UserDataResult()
}
