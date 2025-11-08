package com.xliiicxiv.scrapper.extension

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun waitWebViewToLoad(
    webViewState: WebViewState
) {
    delay(1000)
    snapshotFlow { webViewState.loadingState }
        .filterIsInstance<LoadingState.Finished>()
        .first()
    delay(1000)
}

suspend fun waitUntilPageReady(pageReady: MutableState<Boolean>) {
    while (!pageReady.value) {
        delay(100)
    }
    delay(800)
}

suspend fun WebViewNavigator.awaitJavaScript(script: String): String {
    return suspendCancellableCoroutine { continuation ->
        evaluateJavaScript(script) { result ->
            if (continuation.isActive) {
                continuation.resume(result, null)
            }
        }
    }
}

suspend fun AdvanceWebViewControl.awaitJavaScript(script: String): String {
    return suspendCancellableCoroutine { continuation ->
        evaluateJavascript(script) { result ->
            if (continuation.isActive) {
                continuation.resume(result, null)
            }
        }
    }
}