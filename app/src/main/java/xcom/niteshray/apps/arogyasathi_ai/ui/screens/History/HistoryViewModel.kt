package xcom.niteshray.apps.arogyasathi_ai.ui.screens.History

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xcom.niteshray.apps.arogyasathi_ai.data.model.Chat
import xcom.niteshray.apps.arogyasathi_ai.data.repository.UserRepo

class HistoryViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>?>(null)
    val chat : StateFlow<List<Chat>?> get() = _chats
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val userRepo = UserRepo()

    init {
        viewModelScope.launch {
             userRepo.getChats(uid).collect {
            _chats.value = it
            }
        }
    }
}