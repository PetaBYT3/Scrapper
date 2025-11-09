package com.xliiicxiv.scrapper.page

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.xliiicxiv.scrapper.action.AdminAction
import com.xliiicxiv.scrapper.state.AdminState
import com.xliiicxiv.scrapper.template.CustomDropDownMenu
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomRadioButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextField
import com.xliiicxiv.scrapper.template.DropDownItem
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.ui.theme.Warning
import com.xliiicxiv.scrapper.util.CustomBottomSheetConfirmationComposable
import com.xliiicxiv.scrapper.util.CustomBottomSheetInputComposable
import com.xliiicxiv.scrapper.viewmodel.AdminViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPage(
    navController: NavController,
    viewModel: AdminViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Scaffold(
        navController = navController,
        state = state,
        onAction = onAction
    )

    LaunchedEffect(state.userData) {
        if (state.userData?.userRole == null) return@LaunchedEffect

        if (state.userData?.userRole != "Admin") {
            navController.popBackStack()
        }
    }

    if (state.addBottomSheet) {
        val addSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        CustomBottomSheetInputComposable(
            coroutineScope = scope,
            sheetState = addSheetState,
            title = "Add User",
            content = {
                CustomTextField(
                    value = state.userName,
                    onValueChange = { onAction(AdminAction.UserName(it)) },
                    placeholder = "Username",
                    leadingIcon = Icons.Filled.Person
                )
                VerticalSpacer(10)
                CustomTextField(
                    value = state.userPassword,
                    onValueChange = { onAction(AdminAction.UserPassword(it)) },
                    placeholder = "Password",
                    leadingIcon = Icons.Filled.Password
                )
                VerticalSpacer(10)
                val userRole = listOf(
                    "Admin",
                    "User"
                )
                CustomRadioButton(
                    itemList = userRole,
                    selectedValue = state.userRole,
                    onItemSelected = { onAction(AdminAction.UserRole(it)) }
                )
                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = state.dialogVisibility,
                    content = {
                        Column() {
                            VerticalSpacer(10)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(state.dialogColor),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    imageVector = state.iconDialog,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Text(
                                    text = state.messageDialog,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                )
            },
            btnYes = {
                if (state.userName.isBlank() || state.userPassword.isBlank() || state.userRole.isBlank()) {
                    onAction(AdminAction.MessageDialog(
                        color = Warning,
                        icon = Icons.Filled.Warning,
                        message = "Please fill all fields & select the role !"
                    ))
                } else {
                    onAction(AdminAction.AddUser)
                }
            },
            onCancel = { onAction(AdminAction.AddBottomSheet) },
        )
    }

    if (state.deleteUserBottomSheet) {
        CustomBottomSheetConfirmationComposable(
            title = "Delete User",
            content = {
                Card() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column() {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.userToDelete?.userName ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Password,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.userToDelete?.userPassword ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.userToDelete?.userRole ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Android,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.userToDelete?.androidId?.ifBlank { "No Android ID !" } ?: "No Android ID !")
                            }
                        }
                    }
                }
            },
            onConfirm = { onAction(AdminAction.DeleteUser) },
            onCancel = { onAction(AdminAction.DeleteBottomSheet(null)) }
        )
    }

    if (state.deleteAndroidIdBottomSheet) {
        CustomBottomSheetConfirmationComposable(
            title = "Reset Android ID",
            content = {
                Card() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column() {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.androidIdToDelete?.userName ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Password,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.androidIdToDelete?.userPassword ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.androidIdToDelete?.userRole ?: "")
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Android,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = state.androidIdToDelete?.androidId?.ifBlank { "No Android ID !" } ?: "No Android ID !")
                            }
                        }
                    }
                }
            },
            onConfirm = { onAction(AdminAction.DeleteAndroidId) },
            onCancel = { onAction(AdminAction.DeleteAndroidIdBottomSheet(null)) }
        )
    }
}

@Composable
private fun Scaffold(
    navController: NavController,
    state: AdminState,
    onAction: (AdminAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                navController = navController
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Content(
                    navController = navController,
                    state = state,
                    onAction = onAction
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                state = state,
                onAction = onAction
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController
) {
    TopAppBar(
        navigationIcon = {
            CustomIconButton(
                imageVector = Icons.Rounded.ArrowBack,
                onClick = { navController.popBackStack() }
            )
        },
        title = { Text(text = "Admin Panel") }
    )
}

@Composable
private fun Content(
    navController: NavController,
    state: AdminState,
    onAction: (AdminAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = state.userList,
                key = { userData -> userData.userId }
            ) { userData ->
                Card() {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = userData.userName)
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Password,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = userData.userPassword)
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = userData.userRole)
                            }
                            VerticalSpacer(5)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Android,
                                    contentDescription = null
                                )
                                HorizontalSpacer(15)
                                CustomTextContent(text = userData.androidId.ifBlank { "No Android ID !" })
                            }
                        }
                        val dropDownList = buildList {
                            add(
                                DropDownItem(
                                    imageVector = Icons.Filled.Delete,
                                    title = "Delete User",
                                    onClick = { onAction(AdminAction.DeleteBottomSheet(userData)) }
                                )
                            )
                            if (userData.androidId.isNotBlank()) {
                                add(
                                    DropDownItem(
                                        imageVector = Icons.Filled.RemoveCircle,
                                        title = "Reset Android ID",
                                        onClick = { onAction(AdminAction.DeleteAndroidIdBottomSheet(userData)) }
                                    )
                                )
                            }
                        }
                        CustomDropDownMenu(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 5.dp),
                            dropDownList = dropDownList
                        )
                    }
                }
                VerticalSpacer(10)
            }
        }

        if (state.userList.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                   imageVector = Icons.Filled.Warning,
                   contentDescription = null,
                )
                HorizontalSpacer(10)
                CustomTextContent(text = "No User Found !")
            }
        }
    }
}

@Composable
private fun FloatingActionButton(
    state: AdminState,
    onAction: (AdminAction) -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = { onAction(AdminAction.AddBottomSheet) },
        icon = {
            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null
            )
        },
        text = { Text(text = "Add User") }
    )
}