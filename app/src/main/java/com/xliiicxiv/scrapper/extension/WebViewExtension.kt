package com.xliiicxiv.scrapper.extension

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.xliiicxiv.scrapper.string.dptUrlInput
import com.xliiicxiv.scrapper.string.webUserAgent
import im.delight.android.webview.AdvancedWebView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdvanceWebViewControl {
    var webView: AdvancedWebView? = null

    fun evaluateJavascript(script: String, onResult: (String) -> Unit) {
        webView?.evaluateJavascript(script) {
            onResult(it)
        }
    }

    fun loadUrl(url: String) {
        webView?.loadUrl(url)
    }
}

@Composable
fun rememberAdvanceViewControl(): AdvanceWebViewControl {
    return remember { AdvanceWebViewControl() }
}

@Composable
fun AdvanceWebViewComposable(
    modifier: Modifier = Modifier,
    url: String,
    advanceWebViewControl: AdvanceWebViewControl,
    onPageStarted: () -> Unit,
    onPageFinished: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .background(Color.Transparent),
        factory = {
            AdvancedWebView(context).apply {
                advanceWebViewControl.webView = this

                setListener(
                    context as ComponentActivity,
                    object : AdvancedWebView.Listener {
                        override fun onPageStarted(
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            onPageStarted()
                        }

                        override fun onPageFinished(url: String?) {
                            onPageFinished()
                        }

                        override fun onPageError(
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            onError(description.toString())
                        }

                        override fun onDownloadRequested(
                            url: String?,
                            suggestedFilename: String?,
                            mimeType: String?,
                            contentLength: Long,
                            contentDisposition: String?,
                            userAgent: String?
                        ) {

                        }

                        override fun onExternalPageRequest(url: String?) {

                        }
                    }
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = webUserAgent
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                settings.setSupportZoom(false)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                loadUrl(url)
            }
        },
        update = {
        }
    )
}