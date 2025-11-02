package com.xliiicxiv.scrapper.page

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ExamplePage (

) {
    Scaffold()
}

@Composable
private fun Scaffold(

) {
    Scaffold(
        topBar = { TopBar() },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Content()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        navigationIcon = {},
        title = { Text(text = "Example JS Inject") }
    )
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val urlToLoad = "https://sipp.bpjsketenagakerjaan.go.id/"
        val context = LocalContext.current
        val webView = remember {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(urlToLoad)
            }
        }
        AndroidView(
            modifier = Modifier
                .weight(1f),
            factory = { webView }
        )
        Column(
            modifier = Modifier
                .padding(15.dp)
        ) {
            val rememberScrollState = rememberScrollState()
            var collectedText by remember { mutableStateOf("No Text Collected") }
            Row(
                modifier = Modifier
                    .horizontalScroll(state = rememberScrollState, enabled = true)
            ) {
                Button(
                    onClick = {
                        val text = "Ferrari"
                        webView.evaluateJavascript(
                            "(function() { document.querySelector('#searchInput').value = '$text'; })()",
                            null
                        )
                    }
                ) {
                    Text(text = "Input Text")
                }
                Button(
                    onClick = {
                        webView.evaluateJavascript(
                            "(function() { document.querySelector('.pure-button.pure-button-primary-progressive').click(); })()",
                            null
                        )
                    }
                ) {
                    Text(text = "Search Button Click")
                }
                Button(
                    onClick = {
                        webView.evaluateJavascript(
                            "document.querySelector('#js-link-box-en > strong').innerText;",
                            { collectedText = it.toString() }
                        )
                    }
                ) {
                    Text(text = "Extract Text")
                }
                Button(
                    onClick = {
                        webView.reload()
                    }
                ) {
                    Text(text = "Reload")
                }
            }
            Text(text = collectedText)
        }
    }
}