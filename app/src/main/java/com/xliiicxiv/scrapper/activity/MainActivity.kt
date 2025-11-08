package com.xliiicxiv.scrapper.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.xliiicxiv.scrapper.datastore.DataStore
import com.xliiicxiv.scrapper.page.AdminPage
import com.xliiicxiv.scrapper.page.DptPage
import com.xliiicxiv.scrapper.page.HomePage
import com.xliiicxiv.scrapper.page.LasikPage
import com.xliiicxiv.scrapper.page.LoginPage
import com.xliiicxiv.scrapper.page.SiipBpjsPage
import com.xliiicxiv.scrapper.page.SplashPage
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.template.slideComposable
import com.xliiicxiv.scrapper.ui.theme.ScrapperTheme
import com.xliiicxiv.scrapper.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrapperTheme {
                val navController = rememberNavController()

                val viewModel: MainViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { route ->
                        navController.navigate(route)
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Route.SplashPage
                ) {
                    slideComposable<Route.SplashPage> {
                        SplashPage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.LoginPage> {
                        LoginPage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.HomePage> {
                        HomePage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.AdminPage> {
                        AdminPage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.SiipBpjsPage> {
                        SiipBpjsPage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.LasikPage> {
                        LasikPage(
                            navController = navController
                        )
                    }
                    slideComposable<Route.DptPage> {
                        DptPage(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}