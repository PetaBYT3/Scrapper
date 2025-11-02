package com.xliiicxiv.scrapper.page

import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.valentinilk.shimmer.shimmer
import com.xliiicxiv.scrapper.action.SiipBpjsAction
import com.xliiicxiv.scrapper.extension.parseXlsxFile
import com.xliiicxiv.scrapper.state.SiipBpjsState
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.viewmodel.SiipBpjsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SiipBpjsPage(
    navController: NavController,
    viewModel: SiipBpjsViewModel = koinViewModel()
) {
    val context = LocalContext.current
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
    state: (SiipBpjsState),
    onAction: (SiipBpjsAction) -> Unit
) {
    val webViewNavigator = rememberWebViewNavigator()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                navController = navController,
                webViewNavigator = webViewNavigator
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
    webViewNavigator: WebViewNavigator
) {

    TopAppBar(
        navigationIcon = { CustomIconButton(
            imageVector = Icons.Filled.ArrowBack,
            onClick = { navController.popBackStack() }
        ) },
        title = { Text(text = "SIIP BPJS") },
        actions = {
            Row() {
                CustomIconButton(
                    imageVector = Icons.Filled.RestartAlt,
                    onClick = { webViewNavigator.reload() }
                )
                HorizontalSpacer(5)
                CustomIconButton(
                    imageVector = Icons.Filled.ArrowBack,
                    onClick = { webViewNavigator.navigateBack() }
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
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val xlsxMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    val xlsxPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                context.contentResolver.query(uri, null, null, null, null)?. use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
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
            .padding(horizontal = 15.dp)
            .verticalScroll(state = scrollState, enabled = true),
    ) {
        val url = "https://sipp.bpjsketenagakerjaan.go.id/tenaga-kerja/baru/form-tambah-tk-individu"
        val webState = rememberWebViewState(url = url)
        Card(
            modifier = Modifier
                .weight(1f)
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

                val loggedDetection = "document.getElementById('form-login') == null"
                webViewNavigator.evaluateJavaScript(script = loggedDetection) {
                    onAction(SiipBpjsAction.IsLoggedIn(it.toBoolean()))
                }
            }
        }
        VerticalSpacer(10)
        Card() {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextTitle(text = "Menu")
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
                                                onAction(SiipBpjsAction.DeleteSheet)
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
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column() {
                                                CustomTextContent(text = "${state.rawList.size} Rows Detected")
                                                VerticalSpacer(10)
                                                CustomTextContent(text = "Success : ")
                                                CustomTextContent(text = "Failed : ")
                                            }
                                            Spacer(Modifier.weight(1f))
                                        }
                                    }
                                )
                                VerticalSpacer(10)
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = { onAction(SiipBpjsAction.IsStarted) },
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
                                    )
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

private fun jsinjection() {
//    val script2 = "document.querySelector('button[href=\"#collapseTwo\"]').click();"
//    webViewNavigator.evaluateJavaScript(script = script2)
//
//    val script3 = "(function() { document.querySelector('#kpj').value = '12039136994'; })()"
//    webViewNavigator.evaluateJavaScript(script = script3)
//
//    val script4 = "Array.from(document.querySelectorAll('button')).find(el => el.textContent.includes('Lanjut'))?.click();"
//    webViewNavigator.evaluateJavaScript(script = script4)
}

