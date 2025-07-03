package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import xcom.niteshray.apps.arogyasathi_ai.R
import xcom.niteshray.apps.arogyasathi_ai.utils.LanguagePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController,mainViewModel: MainViewModel = viewModel<MainViewModel>()) {
    var context = LocalContext.current
    val isListening by mainViewModel.isListening.collectAsState()
    val messages by mainViewModel.messages.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.mic_ani))
    var isSettingDialog by remember { mutableStateOf(false) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 2f
    )
    val langpref = LanguagePreference(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp).clip(CircleShape).border(2.dp, color = Color.White,CircleShape)

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(R.drawable.history),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp).clickable{
                                navController.navigate("History")
                            },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                                .clickable{
                                    isSettingDialog = true
                                }
                            ,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = painterResource(R.drawable.newchat),
                            contentDescription = "",
                            modifier = Modifier.size(30.dp).clickable{
                                mainViewModel.CreateNewChat(context)
                            },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
            ){
                Divider(color = Color.White, thickness = 1.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable{
                            mainViewModel.toggleListening(context)
                        }
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    if(isSettingDialog){
                        SettingDialog(langpref.getSelectedLanguageDisplayName(),{isSettingDialog = false}){ name , code ->
                            langpref.saveSelectedLanguage(code,name)
                            isSettingDialog = false
                            Toast.makeText(context , "Selected Language : $name" , Toast.LENGTH_LONG).show()
                            mainViewModel.setMessage(context)
                        }
                    }
                    if(isListening){
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.fillMaxSize()
                        )
                        ListeningIndicator(partialText = mainViewModel.partialText.collectAsState().value, isListening = isListening)
                    }else{
                        Image(
                            painter = if(!isListening) painterResource(R.drawable.mic) else painterResource(R.drawable.pause),
                            contentDescription = "",
                            modifier = Modifier.size(150.dp)
                        )

                    }

                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ) {
            items(messages.size) { index ->
                val (message, isUser) = messages[index]
                ChatBubble(message = message, isUser = isUser)
            }
        }
    }
}

