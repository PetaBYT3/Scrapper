package com.xliiicxiv.scrapper.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xliiicxiv.scrapper.ui.theme.Warning

data class LoginState(

    val username : String = "",
    val password : String = "",

    val isLoading: Boolean = false,

    val dialogVisibility: Boolean = false,
    val dialogColor: Color = Warning,
    val iconDialog: ImageVector = Icons.Filled.Warning,
    val messageDialog: String = ""

)
