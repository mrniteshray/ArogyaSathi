package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xcom.niteshray.apps.arogyasathi_ai.data.model.Message
import xcom.niteshray.apps.arogyasathi_ai.data.model.User
import xcom.niteshray.apps.arogyasathi_ai.data.repository.UserRepo
import xcom.niteshray.apps.arogyasathi_ai.utils.SpeechRecognizerManager

class MainViewModel : ViewModel() {
    private val userRepository = UserRepo()
    private val _fullText = MutableStateFlow("")
    val fullText: StateFlow<String> = _fullText.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private lateinit var speechRecognizerManager: SpeechRecognizerManager

    init {
        viewModelScope.launch {
            _messages.value = _messages.value + Message("Hello Nitesh ,How may i help you!",false)
            _user.value = userRepository.fetchUser(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    fun startListening(context: Context) {
        speechRecognizerManager = SpeechRecognizerManager(context).apply {
            startListening(
                onFinalResult = { finalSegment ->
                    viewModelScope.launch {
                        Log.d("Speechkadekh", "Final: $finalSegment")
                        _fullText.value += if (_fullText.value.endsWith(" ") || _fullText.value.isEmpty()) {
                            finalSegment
                        } else {
                            " $finalSegment"
                        }
                        _partialText.value = ""
                    }
                },
                onPartialResult = { partial ->
                    viewModelScope.launch {
                        _partialText.value = " $partial"
                    }
                },
                onError = {

                }
            )
        }
    }
    fun toggleListening(context: Context) {
        viewModelScope.launch {
            if (_isListening.value) {
                _isListening.value = false
                Log.d("Message", "Message added: ${_fullText.value}")
                if (_fullText.value.isNotBlank()) {
                    _messages.value = _messages.value + Message(_fullText.value.trim(), true)
                }
                speechRecognizerManager.stopListening()
            } else {
                _isListening.value = true
                _fullText.value = ""
                _partialText.value = ""
                startListening(context)
            }
        }
    }
    override fun onCleared() {
        speechRecognizerManager.destroy()
        super.onCleared()
    }
}