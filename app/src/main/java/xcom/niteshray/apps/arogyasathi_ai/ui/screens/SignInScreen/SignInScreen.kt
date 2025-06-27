package xcom.niteshray.apps.arogyasathi_ai.ui.screens.SignInScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import xcom.niteshray.apps.arogyasathi_ai.R
import xcom.niteshray.apps.arogyasathi_ai.ui.theme.graycolor
import xcom.niteshray.apps.arogyasathi_ai.utils.GoogleSignInHelper

@Composable
fun SignInScreen(navController : NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentuser = FirebaseAuth.getInstance().currentUser

    if(currentuser!= null){
        navController.navigate("MainScreen")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        GoogleSignInHelper.doSignIn(
            context = context,
            scope = scope,
            launcher = null,
            login = {
                navController.navigate("MainScreen")
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

            }
        )
    }

    Scaffold { innerPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPad)
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(280.dp).align(Alignment.Center)
            )


            Button(
                onClick = {
                    GoogleSignInHelper.doSignIn(
                        context = context,
                        scope = scope,
                        launcher = launcher,
                        login = {
                            navController.navigate("MainScreen")
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                ,
                colors = ButtonDefaults.buttonColors(containerColor = graycolor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Icon(
                        painter = painterResource(R.drawable.google),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "Sign In With Google",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
