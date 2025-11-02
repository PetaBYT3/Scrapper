package com.xliiicxiv.scrapper.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.xliiicxiv.scrapper.template.CustomIconButton
import org.koin.core.component.getScopeName

@Composable
fun SiipBpjsPage(
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
        topBar = { TopBar(navController = navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { Content() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController
) {
    TopAppBar(
        navigationIcon = { CustomIconButton(
            imageVector = Icons.Filled.ArrowBack,
            onClick = { navController.popBackStack() }
        ) },
        title = { Text(text = "SIIP BPJS") }
    )
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val url = "https://sipp.bpjsketenagakerjaan.go.id/"
        val webState = rememberWebViewState(url = url)
        val webNavigator = rememberWebViewNavigator()

        Card() {
            WebView(
                modifier = Modifier
                    .fillMaxSize(),
                state = webState,
                navigator = webNavigator
            )
        }
    }
}