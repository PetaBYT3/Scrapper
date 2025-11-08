package com.xliiicxiv.scrapper.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.dataclass.UserDataClass
import com.xliiicxiv.scrapper.ui.theme.Warning

data class AdminState(

    val userList: List<UserDataClass> = emptyList(),

    val addBottomSheet: Boolean = false,

    val deleteBottomSheet: Boolean = false,
    val userToDelete: UserDataClass? = null,

    val userName: String = "",
    val userPassword: String = "",
    val userRole: String = "",

    val dialogVisibility: Boolean = false,
    val dialogColor: Color = Warning,
    val iconDialog: ImageVector = Icons.Filled.Warning,
    val messageDialog: String = ""
)