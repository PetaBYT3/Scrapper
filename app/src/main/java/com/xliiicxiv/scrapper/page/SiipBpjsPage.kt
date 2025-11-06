package com.xliiicxiv.scrapper.page

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import com.xliiicxiv.scrapper.action.SiipBpjsAction
import com.xliiicxiv.scrapper.dataclass.SiipResult
import com.xliiicxiv.scrapper.effect.SiipBpjsEffect
import com.xliiicxiv.scrapper.extension.exportToExcelSiip
import com.xliiicxiv.scrapper.extension.getCurrentTime
import com.xliiicxiv.scrapper.extension.removeDoubleQuote
import com.xliiicxiv.scrapper.extension.waitWebViewToLoad
import com.xliiicxiv.scrapper.state.SiipBpjsState
import com.xliiicxiv.scrapper.string.SiipBPJSInput
import com.xliiicxiv.scrapper.string.SiipBPJSLoginUrl
import com.xliiicxiv.scrapper.string.siipPath
import com.xliiicxiv.scrapper.string.xlsxMimeType
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.util.CustomBottomSheetConfirmation
import com.xliiicxiv.scrapper.util.CustomBottomSheetMessage
import com.xliiicxiv.scrapper.util.CustomBottomSheetMessageComposable
import com.xliiicxiv.scrapper.viewmodel.SiipBpjsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SiipBpjsPage(
    navController: NavController,
    viewModel: SiipBpjsViewModel = koinViewModel()
) {
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Scaffold(
        navController = navController,
        state = state,
        onAction = onAction,
        snackbarHostState = snackbarHostState
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is SiipBpjsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = it.message,
                        withDismissAction = true
                    )
                }
            }
        }
    }

    DisposableEffect(state.isStarted) {
        view.keepScreenOn = state.isStarted

        onDispose {
            view.keepScreenOn = false
        }
    }

    if (state.stopBottomSheet) {
        CustomBottomSheetConfirmation(
            title = "Stop Process",
            message = "Are you sure you want to stop the process?, Some data may not be collected",
            onConfirm = { onAction(SiipBpjsAction.IsStarted) },
            onCancel = { onAction(SiipBpjsAction.StopBottomSheet) }
        )
    }

    if (state.questionBottomSheet) {
        CustomBottomSheetMessageComposable(
            title = "How To Use ?",
            content = {
                CustomTextTitle(text = "How to start the Auto Check ?")
                CustomTextContent(text = "1. Login Into SIIP BPJS Web\n2. Select .xlsx File\n3. Click Start Button")
                VerticalSpacer(10)
                CustomTextTitle(text = "Where the result saved ?")
                CustomTextContent(text = "Documents / Scrapper / Siip BPJS")
            },
            onDismiss = { onAction(SiipBpjsAction.QuestionBottomSheet) }
        )
    }

    if (state.deleteXlsxBottomSheet) {
        CustomBottomSheetMessage(
            title = "Delete .XLSX File",
            message = "Auto check is running, Stop it first to delete .xlsx file",
            onDismiss = { onAction(SiipBpjsAction.DeleteXlsxBottomSheet) }
        )
    }
}

@Composable
private fun Scaffold(
    navController: NavController,
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val webViewNavigator = rememberWebViewNavigator()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
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
                    onAction = onAction,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    webViewNavigator: WebViewNavigator,
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            CustomIconButton(
                imageVector = Icons.Filled.ArrowBack,
                onClick = {
                    if (state.isStarted) {
                        onAction(SiipBpjsAction.ShowSnackbar("Please Stop Process First"))
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
                    onClick = { webViewNavigator.loadUrl(SiipBPJSLoginUrl) }
                )
                CustomIconButton(
                    imageVector = Icons.Filled.QuestionMark,
                    onClick = { onAction(SiipBpjsAction.QuestionBottomSheet) }
                )
            }
        }
    )
}

@Composable
private fun Content(
    webViewNavigator: WebViewNavigator,
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit
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
                            onAction(SiipBpjsAction.SheetUri(uri = uri))
                            onAction(SiipBpjsAction.SheetName(name = cursor.getString(nameIndex)))
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
        val webState = rememberWebViewState(url = SiipBPJSLoginUrl)
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
                LaunchedEffect(state.isStarted) {
                    if (!state.isStarted && state.siipResult.isNotEmpty()) {
                        exportToExcelSiip(
                            context = context,
                            path = siipPath,
                            fileName = "SIIP Result ${getCurrentTime()}.xlsx",
                            siipResult = state.siipResult
                        )
                        onAction(SiipBpjsAction.ShowSnackbar("File Saved !"))
                    }
                }
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
                        onClick = { onAction(SiipBpjsAction.ExtendedMenu) }
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
                        if (state.isLoggedIn) {
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
                                                    onAction(SiipBpjsAction.DeleteXlsxBottomSheet)
                                                } else {
                                                    onAction(SiipBpjsAction.DeleteXlsx)
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
                                            onAction(SiipBpjsAction.StopBottomSheet)
                                        } else {
                                            onAction(SiipBpjsAction.IsStarted)
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
                        } else {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null
                                )
                                HorizontalSpacer(10)
                                CustomTextContent(text = "Please Login Into SIIP BPJS First")
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
private fun LoginDetection(
    webViewNavigator: WebViewNavigator,
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit
) {
    val loggedDetection = "document.getElementById('form-login') != null"
    webViewNavigator.evaluateJavaScript(script = loggedDetection) {
        onAction(SiipBpjsAction.IsLoggedIn(it.toBoolean()))
    }
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            webViewNavigator.loadUrl("https://sipp.bpjsketenagakerjaan.go.id/tenaga-kerja/baru/form-tambah-tk-individu")
        }
        delay(1000)
    }
}

@Composable
private fun AutoCheck(
    webViewNavigator: WebViewNavigator,
    webViewState: WebViewState,
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit
) {
    var kpjNumber by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var nikNumber by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isKpjDetected by remember { mutableStateOf(false) }

    LaunchedEffect(state.isStarted) {
        if (!state.isStarted) return@LaunchedEffect

        for (rawString in state.rawList) {

            kpjNumber = ""
            fullName = ""
            nikNumber = ""
            birthDate = ""
            email = ""

            webViewNavigator.loadUrl(SiipBPJSInput)

            waitWebViewToLoad(webViewState = webViewState)

            val doneButton = "Array.from(document.querySelectorAll('button')).find(el => el.textContent.includes('Sudah'))?.click();"
            webViewNavigator.evaluateJavaScript(doneButton)

            val kpjTextField = "document.querySelector('input[placeholder=\"Input No KPJ\"]').value = '$rawString';"
            webViewNavigator.evaluateJavaScript(kpjTextField)

            val btnNext = "Array.from(document.querySelectorAll('button')).find(el => el.textContent.includes('Lanjut'))?.click();"
            webViewNavigator.evaluateJavaScript(btnNext)

            waitWebViewToLoad(webViewState = webViewState)

            val successDetection = "document.querySelector('.swal2-title').textContent;"
            webViewNavigator.evaluateJavaScript(successDetection) {
                if (it.contains("Berhasil!")) {
                    isKpjDetected = true
                } else {
                    isKpjDetected = false
                }
            }

            delay(7500)

            if (!isKpjDetected) {
                Log.d("SIIP", "KPJ Not Detected !")
                onAction(SiipBpjsAction.Failure)
                onAction(SiipBpjsAction.Process)
                continue
            }

            Log.d("SIIP", "KPJ Detected !")

            kpjNumber = rawString

            val nameElement = "document.querySelector('.swal2-content').textContent;"
            webViewNavigator.evaluateJavaScript(nameElement) {
                val pattern = "atas nama (.*?) terdaftar".toRegex()
                val filtering = pattern.find(it)

                val nameResult = filtering?.groups?.get(1)?.value.toString()

                fullName = nameResult
            }

            val continueButton = "document.querySelector('.swal2-confirm').click();"
            webViewNavigator.evaluateJavaScript(continueButton)

            waitWebViewToLoad(webViewState = webViewState)

            val nikElement = "document.getElementById('no_identitas').value;"
            webViewNavigator.evaluateJavaScript(nikElement) {
                val removedQuote = removeDoubleQuote(it)
                nikNumber = removedQuote
            }

            val birthDateElement = "document.getElementById('tgl_lahir').value;"
            webViewNavigator.evaluateJavaScript(birthDateElement) {
                val removedQuote = removeDoubleQuote(it)
                birthDate = removedQuote
            }

            val emailElement = "document.getElementById('email').value;"
            webViewNavigator.evaluateJavaScript(emailElement) {
                val removedQuote = removeDoubleQuote(it)
                email = removedQuote
            }

            delay(12_500)

            val result = SiipResult(
                kpjNumber = kpjNumber,
                fullName = fullName,
                nikNumber = nikNumber,
                birthDate = birthDate,
                email = email
            )
            onAction(SiipBpjsAction.AddResult(result = result))
            onAction(SiipBpjsAction.Success)
            onAction(SiipBpjsAction.Process)

            delay(1000)
        }
        onAction(SiipBpjsAction.IsStarted)
    }
}

