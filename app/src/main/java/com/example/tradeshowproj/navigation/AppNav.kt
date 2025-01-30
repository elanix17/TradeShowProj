package com.example.tradeshowproj.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tradeshowproj.gettingstarted.GettingStartedScreen
import com.example.tradeshowproj.home.HomeScreen
import com.example.tradeshowproj.zCatalystSDK.ZAuthSDK
import com.example.tradeshowproj.appswitchers.AppSwitcherScreen
import com.example.tradeshowproj.auth.SignUpScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavSpec {

    @Serializable
    data object AppSwitcher : AppNavSpec

    @Serializable
    data object SignUpScreen : AppNavSpec

    @Serializable
    data object GettingStarted : AppNavSpec

    @Serializable
    data class Home(val userName: String) : AppNavSpec
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (ZAuthSDK.isUserSignedIn()) AppNavSpec.AppSwitcher else AppNavSpec.GettingStarted,
    ) {
        composable<AppNavSpec.AppSwitcher> {
            AppSwitcherScreen(navController = navController)
        }

        composable<AppNavSpec.GettingStarted> {
            GettingStartedScreen(navController)
        }

        composable<AppNavSpec.SignUpScreen> {
            SignUpScreen(navController = navController)
        }

        composable<AppNavSpec.Home> {
            val homeParams = it.toRoute<AppNavSpec.Home>()
            HomeScreen(navController = navController, name = homeParams.userName)
        }
    }
}
