package com.xliiicxiv.scrapper.page

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.multiplatform.webview.web.AccompanistWebChromeClient
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.PlatformWebViewParams
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.valentinilk.shimmer.shimmer
import com.xliiicxiv.scrapper.action.DptAction
import com.xliiicxiv.scrapper.action.LasikAction
import com.xliiicxiv.scrapper.dataclass.DptResult
import com.xliiicxiv.scrapper.extension.waitUntilPageReady
import com.xliiicxiv.scrapper.extension.waitWebViewToLoad
import com.xliiicxiv.scrapper.state.DptState
import com.xliiicxiv.scrapper.string.dptUrlInput
import com.xliiicxiv.scrapper.string.lasikLoginUrl
import com.xliiicxiv.scrapper.string.webUserAgent
import com.xliiicxiv.scrapper.string.xlsxMimeType
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.viewmodel.DptViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
fun DptPage(
    navController: NavHostController,
    viewModel: DptViewModel = koinViewModel()
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
    state: DptState,
    onAction: (DptAction) -> Unit
) {
    val webViewNavigator = rememberWebViewNavigator()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                webViewNavigator = webViewNavigator,
                state = state,
                onAction = onAction
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Content(
                    webViewNavigator = webViewNavigator,
                    state = state,
                    onAction = onAction
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    webViewNavigator: WebViewNavigator,
    state: DptState,
    onAction: (DptAction) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            CustomIconButton(
                imageVector = Icons.Filled.ArrowBack,
                onClick = {
                    if (state.isStarted) {
                        onAction(DptAction.ShowSnackbar("Please Stop Process First"))
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        title = { Text(text = "DPT") },
        actions = {
            Row() {
                CustomIconButton(
                    imageVector = Icons.Filled.RestartAlt,
                    onClick = { webViewNavigator.loadUrl(dptUrlInput) }
                )
                CustomIconButton(
                    imageVector = Icons.Filled.QuestionMark,
                    onClick = { onAction(DptAction.QuestionBottomSheet) }
                )
            }
        }
    )
}

@Composable
private fun Content(
    webViewNavigator: WebViewNavigator,
    state: DptState,
    onAction: (DptAction) -> Unit
) {
    val context = LocalContext.current
    val xlsxPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                context.contentResolver.query(uri, null, null, null, null)?. use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            onAction(DptAction.SheetUri(uri = uri))
                            onAction(DptAction.SheetName(name = cursor.getString(nameIndex)))
                        }
                    }
                }
            }
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
    ) {
        var pageReady = remember { mutableStateOf(false) }
        val webState = rememberWebViewState(url = dptUrlInput)
        val chromeClient = remember {
            object : AccompanistWebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                        pageReady.value = true
                    }
                }
            }
        }
        val platformParams = remember { PlatformWebViewParams(chromeClient = chromeClient) }

        Card(
            modifier = Modifier
                .weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                WebView(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = webState,
                    navigator = webViewNavigator,
                    platformWebViewParams = platformParams,
                    onCreated = { webView ->
                        webView.settings.javaScriptEnabled = true
                        webView.settings.domStorageEnabled = true
                        webView.settings.userAgentString = webUserAgent
                        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        webView.settings.setSupportZoom(false)
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    }
                )
                when (webState.loadingState) {
                    is LoadingState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmer()
                        ) { Column(Modifier.fillMaxSize().background(Color.LightGray)) {} }
                    }
                    else -> {}
                }
                AutoCheck(
                    webViewNavigator = webViewNavigator,
                    webViewState = webState,
                    state = state,
                    onAction = onAction,
                    pageReady = pageReady
                )
            }
        }
        VerticalSpacer(10)
        Card() {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextTitle(text = "Auto Checker")
                    Spacer(Modifier.weight(1f))
                    val animateRotation by animateFloatAsState(
                        targetValue = if (state.extendedMenu) 0f else 180f,
                        animationSpec = tween(500)
                    )
                    IconButton(
                        onClick = { onAction(DptAction.ExtendedMenu) }
                    ) {
                        Icon(
                            modifier = Modifier
                                .rotate(animateRotation),
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null
                        )
                    }
                }
                AnimatedVisibility(
                    visible = state.extendedMenu,
                    content = {
                        Column() {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.InsertDriveFile,
                                    contentDescription = null
                                )
                                HorizontalSpacer(10)
                                CustomTextContent(state.sheetName ?: "No .xlsx File Selected !")
                                Spacer(Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        if (state.sheetUri != null) {
                                            if (state.isStarted) {
                                                onAction(DptAction.DeleteXlsxBottomSheet)
                                            } else {
                                                onAction(DptAction.DeleteXlsx)
                                            }
                                        } else {
                                            xlsxPicker.launch(xlsxMimeType)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (state.sheetUri != null) Icons.Filled.Delete else Icons.Filled.AttachFile,
                                        contentDescription = null
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = state.sheetUri != null,
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.List,
                                                    contentDescription = null
                                                )
                                                HorizontalSpacer(10)
                                                CustomTextContent(text = "${state.rawList.size} Data Detected")
                                                Spacer(Modifier.weight(1f))
                                            }
                                            VerticalSpacer(5)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Timelapse,
                                                    contentDescription = null
                                                )
                                                HorizontalSpacer(10)
                                                CustomTextContent(text = "${state.process} / ${state.rawList.size} Process")
                                            }
                                            VerticalSpacer(5)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null
                                                )
                                                HorizontalSpacer(10)
                                                CustomTextContent(text = "${state.success} Success")
                                            }
                                            VerticalSpacer(5)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = null
                                                )
                                                HorizontalSpacer(10)
                                                CustomTextContent(text = "${state.failure} Failed")
                                            }
                                        }
                                    }
                                }
                            )
                            VerticalSpacer(10)
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    if (state.isStarted) {
                                        onAction(DptAction.StopBottomSheet)
                                    } else {
                                        onAction(DptAction.IsStarted)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isStarted) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primaryContainer
                                    },
                                    contentColor = if (state.isStarted) {
                                        MaterialTheme.colorScheme.onError
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                ),
                                enabled = state.rawList.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = if (state.isStarted) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = null
                                )
                                Text(text = if (state.isStarted) "Stop" else "Start")
                            }
                        }
                    }
                )
            }
        }
    }
    VerticalSpacer(10)
}

@Composable
private fun AutoCheck(
    webViewNavigator: WebViewNavigator,
    webViewState: WebViewState,
    state: DptState,
    onAction: (DptAction) -> Unit,
    pageReady: MutableState<Boolean>
) {
    var kpjNumber by remember { mutableStateOf("") }
    var nikNumber by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var kabupaten by remember { mutableStateOf("") }
    var kecamatan by remember { mutableStateOf("") }
    var kelurahan by remember { mutableStateOf("") }
    var isDataFound by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(state.isStarted) {
        if (!state.isStarted) {
            pageReady.value = false
            return@LaunchedEffect
        }

        for (rawMap in state.rawList) {
            // Reset semua
            kpjNumber = ""
            nikNumber = ""
            fullName = ""
            birthDate = ""
            email = ""
            kabupaten = ""
            kecamatan = ""
            kelurahan = ""
            isDataFound = false
            pageReady.value = false // Reset progress

            val currentNik = rawMap.nikNumber ?: continue

            // 1. Load halaman
            webViewNavigator.loadUrl(dptUrlInput)
            waitUntilPageReady(pageReady)

            // 2. Inject NIK
            val jsSetNik = "document.querySelector('form input[type=\"text\"]').value = '$currentNik';"
            webViewNavigator.evaluateJavaScript(jsSetNik)

            // 3. Bypass reCAPTCHA + trigger
            val jsBypass = """
                window.grecaptcha = { execute: () => Promise.resolve('token') };
                if (typeof findDptb === 'function') findDptb('$currentNik');
            """.trimIndent()
            webViewNavigator.evaluateJavaScript(jsBypass)

            // 4. Klik Pencarian
            val jsClick = """
                Array.from(document.querySelectorAll('div.wizard-buttons button'))
                .find(b => b.textContent.trim().includes('Pencarian'))?.click();
            """.trimIndent()
            webViewNavigator.evaluateJavaScript(jsClick)

            // Tunggu hasil
            delay(10_000)

            // 5. Cek apakah ditemukan
            val jsCheck = "document.querySelector('.watermarked') ? 'YES' : 'NO';"
            webViewNavigator.evaluateJavaScript(jsCheck) { result ->
                isDataFound = result.contains("YES")
            }

            if (!isDataFound) {
                Log.d("DPT", "NIK $currentNik TIDAK ditemukan")
                onAction(DptAction.Failure)
                continue
            }

            Log.d("DPT", "NIK $currentNik DITEMUKAN!")

            // 6. Ambil data dari input
            kpjNumber = rawMap.kpjNumber
            nikNumber = currentNik
            fullName = rawMap.fullName
            birthDate = rawMap.birthDate
            email = rawMap.email

            // 7. Ambil alamat
            val jsAddr = """
                (function() {
                    try {
                        return JSON.stringify({
                            kab: document.querySelector('.row--left')?.textContent?.trim() || '',
                            kec: document.querySelector('.row--center')?.textContent?.trim() || '',
                            kel: document.querySelectorAll('.row--right')[2]?.textContent?.trim() || ''
                        });
                    } catch(e) { return '{}'; }
                })();
            """.trimIndent()

            webViewNavigator.evaluateJavaScript(jsAddr) { jsonStr ->
                scope.launch {
                    try {
                        var cleaned = jsonStr.trim()
                        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
                            cleaned = cleaned.substring(1, cleaned.length - 1).replace("\\\"", "\"")
                        }
                        val json = JSONObject(cleaned)
                        kabupaten = json.optString("kab").replace("Kabupaten", "").trim()
                        kecamatan = json.optString("kec").replace("Kecamatan", "").trim()
                        kelurahan = json.optString("kel").replace("Kelurahan", "").trim()
                    } catch (e: Exception) {
                        Log.e("DPT", "Parse alamat gagal: ${e.message}")
                    }
                }
            }

            delay(1200)

            // 8. Simpan hasil
            val result = DptResult(
                kpjNumber = kpjNumber,
                nikNumber = nikNumber,
                fullName = fullName,
                birthDate = birthDate,
                email = email,
                regencyName = kabupaten,
                subdistrictName = kecamatan,
                wardName = kelurahan
            )

            onAction(DptAction.AddResult(result))
            onAction(DptAction.Success)

            delay(800)
        }

        // Selesai
        onAction(DptAction.IsStarted)
    }
}