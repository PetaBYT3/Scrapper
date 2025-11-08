package com.xliiicxiv.scrapper.page

import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.valentinilk.shimmer.shimmer
import com.xliiicxiv.scrapper.action.LasikAction
import com.xliiicxiv.scrapper.extension.waitWebViewToLoad
import com.xliiicxiv.scrapper.state.LasikState
import com.xliiicxiv.scrapper.string.lasikInputUrl
import com.xliiicxiv.scrapper.string.xlsxMimeType
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.viewmodel.LasikViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LasikPage(
    navController: NavController,
    viewModel: LasikViewModel = koinViewModel()
) {
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Scaffold(
        navController = navController,
        state = state,
        onAction = onAction
    )

    DisposableEffect(state.isStarted) {
        view.keepScreenOn = state.isStarted

        onDispose {
            view.keepScreenOn = false
        }
    }
}

@Composable
private fun Scaffold(
    navController: NavController,
    state: LasikState,
    onAction: (LasikAction) -> Unit
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
    state: LasikState,
    onAction: (LasikAction) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            CustomIconButton(
                imageVector = Icons.Filled.ArrowBack,
                onClick = {
                    if (state.isStarted) {
                        onAction(LasikAction.ShowSnackbar("Please Stop Process First"))
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        title = { Text(text = "SIIP BPJS") },
        actions = {
            Row() {
                CustomIconButton(
                    imageVector = Icons.Filled.RestartAlt,
                    onClick = { webViewNavigator.loadUrl(lasikInputUrl) }
                )
                CustomIconButton(
                    imageVector = Icons.Filled.QuestionMark,
                    onClick = { onAction(LasikAction.QuestionBottomSheet) }
                )
            }
        }
    )
}

@Composable
private fun Content(
    webViewNavigator: WebViewNavigator,
    state: LasikState,
    onAction: (LasikAction) -> Unit
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
                            onAction(LasikAction.SheetUri(uri = uri))
                            onAction(LasikAction.SheetName(name = cursor.getString(nameIndex)))
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
        val webState = rememberWebViewState(url = lasikInputUrl)
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
                    onAction = onAction
                )
//                LaunchedEffect(state.isStarted) {
//                    if (!state.isStarted && state.siipResult.isNotEmpty()) {
//                        exportToExcelSiip(
//                            context = context,
//                            path = siipPath,
//                            fileName = "SIIP Result ${getCurrentTime()}.xlsx",
//                            siipResult = state.siipResult
//                        )
//                        onAction(SiipBpjsAction.ShowSnackbar("File Saved !"))
//                    }
//                }
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
                        onClick = { onAction(LasikAction.ExtendedMenu) }
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
                                                onAction(LasikAction.DeleteXlsxBottomSheet)
                                            } else {
                                                onAction(LasikAction.DeleteXlsx)
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
                                onClick = { onAction(LasikAction.IsStarted) },
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
    state: LasikState,
    onAction: (LasikAction) -> Unit
) {
    var isDataValid by remember { mutableStateOf(false) }

    LaunchedEffect(state.isStarted) {
        if (!state.isStarted) return@LaunchedEffect

        delay(12_500)

        for (rawString in state.rawList) {

            waitWebViewToLoad(webViewState)

            val nikElement = """
            (function() {
                var nikInput = document.querySelector('input[placeholder="Isi Nomor E-KTP"]');
                if (nikInput) {
                    nikInput.value = '${rawString.nikNumber}';
                    nikInput.dispatchEvent(new Event('input', { bubbles: true }));
                    nikInput.dispatchEvent(new Event('change', { bubbles: true }));
                    return true;
                }
                return false;
            })();
            """.trimIndent()
            webViewNavigator.evaluateJavaScript(nikElement)

            val kpjElement = """
            (function() {
                var kpjInput = document.querySelector('input[placeholder="Isi Nomor KPJ"]');
                if (kpjInput) {
                    kpjInput.value = '${rawString.kpjNumber}';
                    kpjInput.dispatchEvent(new Event('input', { bubbles: true }));
                    kpjInput.dispatchEvent(new Event('change', { bubbles: true }));
                    return true;
                }
                return false;
            })();
            """.trimIndent()
            webViewNavigator.evaluateJavaScript(kpjElement)

            val nameElement = """
            (function() {
                var namaInput = document.querySelector('input[placeholder="Isi Nama sesuai KTP"]');
                if (namaInput) {
                    namaInput.value = '${rawString.fullName}';
                    namaInput.dispatchEvent(new Event('input', { bubbles: true }));
                    namaInput.dispatchEvent(new Event('change', { bubbles: true }));
                    return true;
                }
                return false;
            })();
            """.trimIndent()
            webViewNavigator.evaluateJavaScript(nameElement)

            delay(1_000)
            onAction(LasikAction.Process)
            webViewNavigator.loadUrl(lasikInputUrl)
        }
        onAction(LasikAction.IsStarted)
    }
}