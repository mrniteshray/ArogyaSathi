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
import xcom.niteshray.apps.arogyasathi_ai.data.Api.GeminiService
import xcom.niteshray.apps.arogyasathi_ai.data.model.Message
import xcom.niteshray.apps.arogyasathi_ai.data.model.User
import xcom.niteshray.apps.arogyasathi_ai.data.repository.UserRepo
import xcom.niteshray.apps.arogyasathi_ai.utils.LanguagePreference
import xcom.niteshray.apps.arogyasathi_ai.utils.SpeechRecognizerManager
import xcom.niteshray.apps.arogyasathi_ai.utils.TextToSpeechManager

class MainViewModel : ViewModel() {
    private val userRepository = UserRepo()
    private val _fullText = MutableStateFlow("")

    private lateinit var textToSpeechManager: TextToSpeechManager

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId.asStateFlow()


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
                        if(finalSegment.isNotEmpty()){
                            _messages.value = _messages.value + Message(finalSegment,true)



                            if(_currentChatId.value == null){
                                val chatId = userRepository.SaveChat(user.value?.uid ?: "Null",finalSegment , messages.value)
                                _currentChatId.value = chatId
                            }else{
                                userRepository.UpdateChat(user.value!!.uid,currentChatId.value!! , messages.value)
                            }

                            val response = GeminiService().getResponseFromGemini(_messages.value,finalSegment , context)
                            _messages.value = _messages.value + Message(response,false)

                            userRepository.UpdateChat(user.value!!.uid, _currentChatId.value ?: "", _messages.value)
                            textToSpeechManager.speak(response)
                        }

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
                speechRecognizerManager.stopListening()
            } else {
                _isListening.value = true
                _fullText.value = ""
                _partialText.value = ""
                textToSpeechManager = TextToSpeechManager(context)
                startListening(context)
            }
        }
    }

    fun setMessage(context: Context){
        when(LanguagePreference(context).getSelectedLanguageDisplayName()){
            "Hindi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्त! मैं आपकी क्या मदद कर सकता हूँ?",false)
            }
            "English" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("Hello ,How may i help you!",false)
            }
            "Marathi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्कार! मी तुम्हाला कशी मदत करू शकेन?",false)
            }
        }
    }
    override fun onCleared() {
        speechRecognizerManager.destroy()
        super.onCleared()
    }
}