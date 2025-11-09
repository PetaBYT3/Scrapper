package com.xliiicxiv.scrapper.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.xliiicxiv.scrapper.R
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.route.Route
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashPage(
    navController: NavController,
    dataStore: DataStore = koinInject()
) {
    val userId by dataStore.getUserId.collectAsStateWithLifecycle("")

    LaunchedEffect(Unit) {
        delay(2000)
        if (userId.isBlank()) {
            navController.navigate(Route.LoginPage)
        } else {
            navController.navigate(Route.HomePage)
        }
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(50)),
                    painter = painterResource(R.drawable.speedrunner),
                    contentDescription = null
                )
            }
        }
    )
}