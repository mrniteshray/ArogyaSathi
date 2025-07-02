package xcom.niteshray.apps.arogyasathi_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.apps.arogyasathi_ai.ui.screens.History.HistoryScreen
import xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen.MainScreen
import xcom.niteshray.apps.arogyasathi_ai.ui.screens.SignInScreen.SignInScreen
import xcom.niteshray.apps.arogyasathi_ai.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App(){
    var navController = rememberNavController()

    NavHost(navController = navController , startDestination = "Splash"){
        composable("Splash"){
            SplashScreen(navController)
        }
        composable("SignIn"){
            SignInScreen(navController)
        }

        composable(route = "MainScreen"){
            MainScreen(navController)
        }

        composable(route = "History"){
            HistoryScreen()
        }
    }
}






