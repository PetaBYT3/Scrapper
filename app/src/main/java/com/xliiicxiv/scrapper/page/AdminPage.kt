package com.xliiicxiv.scrapper.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.xliiicxiv.scrapper.action.AdminAction
import com.xliiicxiv.scrapper.state.AdminState
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomRadioButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextField
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
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
                    visible = state.warningAdd,
                    content = {
                        Column() {
                            VerticalSpacer(10)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(10.dp),
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = state.warningAddMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                )
            },
            btnYes = { onAction(AdminAction.AddUser) },
            onCancel = { onAction(AdminAction.AddBottomSheet) },
        )
    }

    if (state.deleteBottomSheet) {
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
                        }
                    }
                }
            },
            onConfirm = { onAction(AdminAction.DeleteUser) },
            onCancel = { onAction(AdminAction.DeleteBottomSheet(null)) }
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
        topBar = { TopBar() },
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
private fun TopBar() {
    TopAppBar(
        navigationIcon = {
            CustomIconButton(
                imageVector = Icons.Rounded.ArrowBack,
                onClick = {}
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
    Column(
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
                        }
                        Spacer(Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            CustomIconButton(
                                imageVector = Icons.Filled.Delete,
                                onClick = { onAction(AdminAction.DeleteBottomSheet(userData)) }
                            )
                        }
                    }
                }
                VerticalSpacer(10)
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