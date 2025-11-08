package com.xliiicxiv.scrapper.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.xliiicxiv.scrapper.action.LoginAction
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.state.LoginState
import com.xliiicxiv.scrapper.template.CustomFilledButton
import com.xliiicxiv.scrapper.template.CustomTextField
import com.xliiicxiv.scrapper.template.CustomTextLarge
import com.xliiicxiv.scrapper.template.CustomTextMedium
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Scaffold(
        navController = navController,
        state = state,
        onAction = onAction
    )
}

@Composable
private fun Scaffold(
    navController: NavController,
    state: LoginState,
    onAction: (LoginAction) -> Unit
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
        floatingActionButton = { FloatingActionButton() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        navigationIcon = {},
        title = { Text(text = "Scrap App") }
    )
}

@Composable
private fun Content(
    navController: NavController,
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        var isPasswordVisible by rememberSaveable { mutableStateOf(true) }

        CustomTextField(
            value = state.username,
            onValueChange = { onAction(LoginAction.Username(it)) },
            placeholder = "Username",
            leadingIcon = Icons.Filled.Person
        )
        VerticalSpacer(10)
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.password,
            onValueChange = { onAction(LoginAction.Password(it)) },
            placeholder = { Text(text = "Password") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = if (isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton (
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )
        VerticalSpacer(10)
        CustomFilledButton(
            title = "Login",
            onClick = { navController.navigate(Route.HomePage) },
        )
        VerticalSpacer(10)
    }
}

@Composable
private fun FloatingActionButton() {
    ExtendedFloatingActionButton(
        onClick = {},
        icon = {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = null
            )
        },
        text = { Text(text = "Contact Admin") }
    )
}