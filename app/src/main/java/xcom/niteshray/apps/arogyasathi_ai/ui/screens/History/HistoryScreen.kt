package xcom.niteshray.apps.arogyasathi_ai.ui.screens.History

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import xcom.niteshray.apps.arogyasathi_ai.R
import xcom.niteshray.apps.arogyasathi_ai.data.model.Chat
import xcom.niteshray.apps.arogyasathi_ai.ui.theme.graycolor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen( viewModel: HistoryViewModel = viewModel()){
    val chats by viewModel.chat.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(R.drawable.history),
                            contentDescription = "",
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "History" ,
                            fontSize = 22.sp ,
                            style = MaterialTheme.typography.titleMedium , color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ){ innerpad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerpad),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(chats.isNullOrEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "no Chats Available",
                        color = Color.White,
                        fontSize = 18.sp, // Make it a bit more prominent
                        textAlign = TextAlign.Center
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chats!!.size){ index->
                        val chat = chats!![index]
                        HistoryIems(chat)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryIems(chat : Chat) {
    Box(
        Modifier.fillMaxWidth().padding(12.dp).background(
            color = graycolor,
            shape = RoundedCornerShape(12.dp)
        ).padding(12.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = chat.title,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 16.sp
            )
        }
    }
}