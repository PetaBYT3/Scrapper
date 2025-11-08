package com.xliiicxiv.scrapper.page

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
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
import androidx.navigation.NavHostController
import com.valentinilk.shimmer.shimmer
import com.xliiicxiv.scrapper.action.DptAction
import com.xliiicxiv.scrapper.dataclass.DptResult
import com.xliiicxiv.scrapper.extension.AdvanceWebViewComposable
import com.xliiicxiv.scrapper.extension.AdvanceWebViewControl
import com.xliiicxiv.scrapper.extension.exportToExcelDpt
import com.xliiicxiv.scrapper.extension.getCurrentTime
import com.xliiicxiv.scrapper.extension.getRegencyName
import com.xliiicxiv.scrapper.extension.getSubdistrictName
import com.xliiicxiv.scrapper.extension.getWardName
import com.xliiicxiv.scrapper.extension.rememberAdvanceViewControl
import com.xliiicxiv.scrapper.extension.removeDoubleQuote
import com.xliiicxiv.scrapper.state.DptState
import com.xliiicxiv.scrapper.string.dptPath
import com.xliiicxiv.scrapper.string.dptUrlInput
import com.xliiicxiv.scrapper.string.xlsxMimeType
import com.xliiicxiv.scrapper.template.CustomIconButton
import com.xliiicxiv.scrapper.template.CustomTextContent
import com.xliiicxiv.scrapper.template.CustomTextTitle
import com.xliiicxiv.scrapper.template.HorizontalSpacer
import com.xliiicxiv.scrapper.template.VerticalSpacer
import com.xliiicxiv.scrapper.util.CustomBottomSheetConfirmation
import com.xliiicxiv.scrapper.util.CustomBottomSheetMessage
import com.xliiicxiv.scrapper.util.CustomBottomSheetMessageComposable
import com.xliiicxiv.scrapper.viewmodel.DptViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun DptPage(
    navController: NavHostController,
    viewModel: DptViewModel = koinViewModel()
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

    if (state.stopBottomSheet) {
        CustomBottomSheetConfirmation(
            title = "Stop Process",
            message = "Are you sure you want to stop the process?, Some data may not be collected",
            onConfirm = { onAction(DptAction.IsStarted) },
            onCancel = { onAction(DptAction.StopBottomSheet) }
        )
    }

    if (state.questionBottomSheet) {
        CustomBottomSheetMessageComposable(
            title = "How To Use ?",
            content = {
                CustomTextTitle(text = "How to start the DPT auto check ?")
                VerticalSpacer(5)
                CustomTextContent(text = "1. Wait until web page loaded\n2. Select .xlsx File\n3. Click Start Button")
                VerticalSpacer(15)
                CustomTextTitle(text = "Where the result saved ?")
                VerticalSpacer(5)
                CustomTextContent(text = "Documents / Scrapper / DPT")
            },
            onDismiss = { onAction(DptAction.QuestionBottomSheet) }
        )
    }

    if (state.deleteXlsxBottomSheet) {
        CustomBottomSheetMessage(
            title = "Delete .XLSX File",
            message = "Auto check is running, Stop it first to delete .xlsx file",
            onDismiss = { onAction(DptAction.DeleteXlsxBottomSheet) }
        )
    }
}

@Composable
private fun Scaffold(
    navController: NavController,
    state: DptState,
    onAction: (DptAction) -> Unit
) {
    val advanceWebViewControl = rememberAdvanceViewControl()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                advanceWebViewControl = advanceWebViewControl,
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
                    advanceWebViewControl = advanceWebViewControl,
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
    advanceWebViewControl: AdvanceWebViewControl,
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
                    onClick = { advanceWebViewControl.loadUrl(dptUrlInput) }
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
    advanceWebViewControl: AdvanceWebViewControl,
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
        Card(
            modifier = Modifier
                .weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                var isLoading by remember { mutableStateOf(true) }
                AdvanceWebViewComposable(
                    modifier = Modifier
                        .fillMaxSize(),
                    url = dptUrlInput,
                    advanceWebViewControl = advanceWebViewControl,
                    onPageStarted = {
                        isLoading = true
                    },
                    onPageFinished = {
                        isLoading = false
                    },
                    onError = {
                        advanceWebViewControl.loadUrl(dptUrlInput)
                    }
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmer()
                    ) { Column(Modifier.fillMaxSize().background(Color.LightGray)) {} }
                }
                AutoCheck(
                    advanceWebViewControl = advanceWebViewControl,
                    state = state,
                    onAction = onAction
                )
                LaunchedEffect(state.isStarted) {
                    if (!state.isStarted && state.dptResult.isNotEmpty()) {
                        exportToExcelDpt(
                            context = context,
                            path = dptPath,
                            fileName = "DPT Result ${getCurrentTime()}.xlsx",
                            dptResult = state.dptResult
                        )
                        onAction(DptAction.StopBottomSheet)
                        Toast.makeText(context, "Result Saved", Toast.LENGTH_SHORT).show()
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
    advanceWebViewControl: AdvanceWebViewControl,
    state: DptState,
    onAction: (DptAction) -> Unit
) {
    var regencyName by remember { mutableStateOf("") }
    var subdistrictName by remember { mutableStateOf("") }
    var wardName by remember { mutableStateOf("") }

    var isDataFound by remember { mutableStateOf(false) }

    LaunchedEffect(state.isStarted) {
        if (!state.isStarted) return@LaunchedEffect

        for (rawList in state.rawList) {
            regencyName = ""
            subdistrictName = ""
            wardName = ""

            advanceWebViewControl.loadUrl(dptUrlInput)

            delay(5_000)

            val inputNikElement = """
                (function() {
                    const input = document.querySelector('form input[type="text"]');
                    if (input) {
                        input.value = '${rawList.nikNumber}';
                        input.dispatchEvent(new Event('input', {bubbles: true}));
                        return 'OK';
                    }
                    return 'NO_INPUT';
                })();
            """.trimIndent()
            advanceWebViewControl.evaluateJavascript(inputNikElement) {}

            delay(1_000)

            val bypassCaptcha = """
                window.grecaptcha = { execute: () => Promise.resolve('token') };
                if (typeof findDptb === 'function') findDptb('${rawList.nikNumber}');
            """.trimIndent()
            advanceWebViewControl.evaluateJavascript(bypassCaptcha) {}

            delay(1_000)

            val elementFind = """
                Array.from(document.querySelectorAll('div.wizard-buttons button'))
                .find(b => b.textContent.trim().includes('Pencarian'))?.click();
            """.trimIndent()
            advanceWebViewControl.evaluateJavascript(elementFind) {}

            delay(5_000)

            val jsCheck = "document.querySelector('.watermarked') ? 'YES' : 'NO';"
            advanceWebViewControl.evaluateJavascript(jsCheck) { result ->
                isDataFound = result.contains("YES")
            }

            delay(5_000)

            if (!isDataFound) {
                onAction(DptAction.Process)
                onAction(DptAction.Failure)
                continue
            }

            val regencyElement = "document.querySelector('.row--left')?.textContent?.trim()"
            advanceWebViewControl.evaluateJavascript(regencyElement) {
                val removedQuote = removeDoubleQuote(it)
                val result = getRegencyName(removedQuote)
                regencyName = result
            }

            val subdistrictElement = "document.querySelector('.row--center')?.textContent?.trim()"
            advanceWebViewControl.evaluateJavascript(subdistrictElement) {
                val removedQuote = removeDoubleQuote(it)
                val result = getSubdistrictName(removedQuote)
                subdistrictName = result
            }

            val wardElement = "document.querySelectorAll('.row--right')[2]?.textContent?.trim()"
            advanceWebViewControl.evaluateJavascript(wardElement) {
                val removedQuote = removeDoubleQuote(it)
                val result = getWardName(removedQuote)
                wardName = result
            }

            delay(5_000)

            val result = DptResult(
                kpjNumber = rawList.kpjNumber,
                nikNumber = rawList.nikNumber,
                fullName = rawList.fullName,
                birthDate = rawList.birthDate,
                email = rawList.email,
                regencyName = regencyName,
                subdistrictName = subdistrictName,
                wardName = wardName
            )

            onAction(DptAction.AddResult(result))
            onAction(DptAction.Process)
            onAction(DptAction.Success)

            delay(1000)
        }
        onAction(DptAction.IsStarted)
    }
}