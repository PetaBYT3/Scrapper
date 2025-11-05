package com.xliiicxiv.scrapper.extension

import androidx.compose.runtime.snapshotFlow
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun waitWebViewToLoad(
    webViewState: WebViewState
) {
    delay(1000)
    snapshotFlow { webViewState.loadingState }
        .filterIsInstance<LoadingState.Finished>()
        .first()
    delay(1000)
}