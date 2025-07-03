package xcom.niteshray.apps.arogyasathi_ai.ui.screens.ChatDetailScreen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xcom.niteshray.apps.arogyasathi_ai.data.model.Chat
import xcom.niteshray.apps.arogyasathi_ai.data.model.User
import xcom.niteshray.apps.arogyasathi_ai.data.repository.UserRepo

class ChatDetailViewModel(private val chatId: String) : ViewModel() {
    private val userRepository = UserRepo()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _chat = MutableStateFlow<Chat?>(null)
    val chat: StateFlow<Chat?> = _chat.asStateFlow()

    init {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                userRepository.getChats(uid).collect { chats ->
                    _chat.value = chats.find { it.chatId == chatId }
                }
                Log.d("ChatDetailView" , chat.value?.messages.toString())
                Log.d("ChatDetailView" , chatId)
            }
        }
    }

    companion object {
        fun Factory(chatId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatDetailViewModel(chatId) as T
            }
        }
    }
}