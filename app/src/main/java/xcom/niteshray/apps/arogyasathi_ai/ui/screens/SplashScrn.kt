package xcom.niteshray.apps.arogyasathi_ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import xcom.niteshray.apps.arogyasathi_ai.R

@Composable
fun SplashScreen(navController: NavController){

    LaunchedEffect(Unit){
        delay(3000)
        if(FirebaseAuth.getInstance().currentUser!=null){
            navController.popBackStack()
            navController.navigate("MainScreen")

        }else{
            navController.popBackStack()

            navController.navigate("SignIn")
        }
    }
    Scaffold(
        containerColor = Color.Black
    ){ innerpad->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerpad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ){
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = " ",
                modifier = Modifier.size(240.dp)
            )
        }
    }
}