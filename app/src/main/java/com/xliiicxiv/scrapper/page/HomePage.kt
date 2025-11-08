package com.xliiicxiv.scrapper.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextHint
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer

@Composable
fun HomePage(
    navController: NavController
) {
    Scaffold(
        navController = navController
    )
}

@Composable
private fun Scaffold(
    navController: NavController
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
                    navController = navController
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController
) {
    TopAppBar(
        navigationIcon = {},
        title = { Text(text = "Home") },
        actions = {
            CustomIconButton(
                imageVector = Icons.Filled.AdminPanelSettings,
                onClick = { navController.navigate(Route.AdminPage) }
            )
        }
    )
}

@Composable
private fun Content(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .verticalScroll(
                state = scrollState,
                enabled = true
            )
    ) {
        data class MenuItem(
            val icon: ImageVector,
            val title: String,
            val content: String,
            val onClick: () -> Unit,
        )
        val menuList = listOf(
            MenuItem(
                icon = Icons.Rounded.Start,
                title = "SIIP BPJS",
                content = "Cek bpjs siip",
                onClick = { navController.navigate(Route.SiipBpjsPage) }
            ),
            MenuItem(
                icon = Icons.Rounded.Start,
                title = "DPT",
                content = "Cek DPT",
                onClick = { navController.navigate(Route.DptPage) }
            ),
            MenuItem(
                icon = Icons.Rounded.Start,
                title = "Lasik",
                content = "Cek Lasik",
                onClick = { navController.navigate(Route.LasikPage) }
            ),
        )
        menuList.forEach { menuItem ->
            Card(
                onClick = { menuItem.onClick.invoke() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxHeight(),
                            imageVector = menuItem.icon,
                            contentDescription = null
                        )
                        HorizontalSpacer(15)
                        Column {
                            CustomTextTitle(text = menuItem.title)
                            VerticalSpacer(10)
                            CustomTextHint(text = menuItem.content)
                        }
                    }
                }
            }
            VerticalSpacer(10)
        }
    }
}