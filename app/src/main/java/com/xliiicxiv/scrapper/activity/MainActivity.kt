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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.xliiicxiv.scrapper.page.DptPage
import com.xliiicxiv.scrapper.page.ExamplePage
import com.xliiicxiv.scrapper.page.HomePage
import com.xliiicxiv.scrapper.page.LasikPage
import com.xliiicxiv.scrapper.page.LoginPage
import com.xliiicxiv.scrapper.page.SiipBpjsPage
import com.xliiicxiv.scrapper.route.Route
import com.xliiicxiv.scrapper.template.slideComposable
import com.xliiicxiv.scrapper.ui.theme.ScrapperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrapperTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Route.LoginPage
                ) {
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