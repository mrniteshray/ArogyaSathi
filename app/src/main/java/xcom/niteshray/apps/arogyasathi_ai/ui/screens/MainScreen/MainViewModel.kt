package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
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

class MainViewModel(context: Context) : ViewModel() {
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
            when (LanguagePreference(context).getSelectedLanguageDisplayName()) {
                "Hindi" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("नमस्ते! मैं आपकी क्या मदद कर सकता हूँ?", false)
                }
                "English" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("Hello, How may I help you!", false)
                }
                "Marathi" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("नमस्कार! मी तुम्हाला कशी मदत करू शकेन?", false)
                }
                "Tamil" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("வணக்கம்! நான் உங்களுக்கு எப்படி உதவ முடியும்?", false)
                }
                "Telugu" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("హాయ్! నేను మీకు ఎలా సహాయపడగలను?", false)
                }
                "Malayalam" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("ഹായ്! ഞാൻ എങ്ങനെ നിനക്ക് സഹായിക്കാം?", false)
                }
                "Kannada" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("ನಮಸ್ಕಾರ! ನಾನು ನಿನಗೆ ಹೇಗೆ ಸಹಾಯ ಮಾಡಬಹುದು?", false)
                }
                "Bengali" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("নমস্কার! আমি কীভাবে আপনাকে সাহায্য করতে পারি?", false)
                }
                "Gujarati" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("નમસ્તે! હું તમને કેવી રીતે મદદ કરી શકું?", false)
                }
                "Punjabi" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("ਸਤ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਂ ਤੁਹਾਡੀ ਕਿਵੇਂ ਮਦਦ ਕਰ ਸਕਦਾ ਹਾਂ?", false)
                }
                "Urdu" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("سلام! میں آپ کی کیسے مدد کر سکتا ہوں؟", false)
                }
                "English (Indian)" -> {
                    _messages.value = emptyList<Message>()
                    _messages.value = _messages.value + Message("Namaste, How may I help you!", false)
                }
            }
            textToSpeechManager = TextToSpeechManager(context)
            _user.value = userRepository.fetchUser(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    fun startListening(context: Context) {
        speechRecognizerManager = SpeechRecognizerManager(context).apply {
            startListening(
                onFinalResult = { finalSegment ->
                    viewModelScope.launch {
                        if(finalSegment.isNotEmpty()){
                            textToSpeechManager = TextToSpeechManager(context)
                            _isListening.value = false
                            _messages.value = _messages.value + Message(finalSegment,true)

                            if(_currentChatId.value == null){
                                val chatId = userRepository.SaveChat(user.value?.uid ?: "Null",finalSegment , messages.value)
                                _currentChatId.value = chatId
                            }else{
                                userRepository.UpdateChat(user.value!!.uid,currentChatId.value!! , messages.value)
                            }
                            try {
                                val response = GeminiService().getResponseFromGemini(_messages.value, finalSegment, context)
                                _messages.value = _messages.value + Message(response, false)
                                userRepository.UpdateChat(_user.value!!.uid, _currentChatId.value ?: "", _messages.value)
                                textToSpeechManager.speak(response)
                            } catch (e: Exception) {
                                val errorMessage = when (LanguagePreference(context).getSelectedLanguageDisplayName()) {
                                    "Hindi" -> "माफ करें, सर्वर में त्रुटि हुई। कृपया बाद में पुनः प्रयास करें।"
                                    "Marathi" -> "माफ करा, सर्व्हरमध्ये त्रुटी आली. कृपया नंतर पुन्हा प्रयत्न करा."
                                    else -> "Sorry, there was a server error. Please try again later."
                                }
                                textToSpeechManager.speak(errorMessage)
                            }
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
                onError = { errorMessage ->
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
                startListening(context)
            }
        }
    }

    fun CreateNewChat(context: Context){
        when (LanguagePreference(context).getSelectedLanguageDisplayName()) {
            "Hindi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्ते! मैं आपकी क्या मदद कर सकता हूँ?", false)
            }
            "English" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("Hello, How may I help you!", false)
            }
            "Marathi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्कार! मी तुम्हाला कशी मदत करू शकेन?", false)
            }
            "Tamil" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("வணக்கம்! நான் உங்களுக்கு எப்படி உதவ முடியும்?", false)
            }
            "Telugu" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("హాయ్! నేను మీకు ఎలా సహాయపడగలను?", false)
            }
            "Malayalam" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ഹായ്! ഞാൻ എങ്ങനെ നിനക്ക് സഹായിക്കാം?", false)
            }
            "Kannada" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ನಮಸ್ಕಾರ! ನಾನು ನಿನಗೆ ಹೇಗೆ ಸಹಾಯ ಮಾಡಬಹುದು?", false)
            }
            "Bengali" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("নমস্কার! আমি কীভাবে আপনাকে সাহায্য করতে পারি?", false)
            }
            "Gujarati" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("નમસ્તે! હું તમને કેવી રીતે મદદ કરી શકું?", false)
            }
            "Punjabi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ਸਤ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਂ ਤੁਹਾਡੀ ਕਿਵੇਂ ਮਦਦ ਕਰ ਸਕਦਾ ਹਾਂ?", false)
            }
            "Urdu" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("سلام! میں آپ کی کیسے مدد کر سکتا ہوں؟", false)
            }
            "English (Indian)" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("Namaste, How may I help you!", false)
            }
        }
        textToSpeechManager.destroy()
        _currentChatId.value = null
    }

    fun setMessage(context: Context){
        when (LanguagePreference(context).getSelectedLanguageDisplayName()) {
            "Hindi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्ते! मैं आपकी क्या मदद कर सकता हूँ?", false)
            }
            "English" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("Hello, How may I help you!", false)
            }
            "Marathi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("नमस्कार! मी तुम्हाला कशी मदत करू शकेन?", false)
            }
            "Tamil" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("வணக்கம்! நான் உங்களுக்கு எப்படி உதவ முடியும்?", false)
            }
            "Telugu" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("హాయ్! నేను మీకు ఎలా సహాయపడగలను?", false)
            }
            "Malayalam" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ഹായ്! ഞാൻ എങ്ങനെ നിനക്ക് സഹായിക്കാം?", false)
            }
            "Kannada" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ನಮಸ್ಕಾರ! ನಾನು ನಿನಗೆ ಹೇಗೆ ಸಹಾಯ ಮಾಡಬಹುದು?", false)
            }
            "Bengali" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("নমস্কার! আমি কীভাবে আপনাকে সাহায্য করতে পারি?", false)
            }
            "Gujarati" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("નમસ્તે! હું તમને કેવી રીતે મદદ કરી શકું?", false)
            }
            "Punjabi" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("ਸਤ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਂ ਤੁਹਾਡੀ ਕਿਵੇਂ ਮਦਦ ਕਰ ਸਕਦਾ ਹਾਂ?", false)
            }
            "Urdu" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("سلام! میں آپ کی کیسے مدد کر سکتا ہوں؟", false)
            }
            "English (Indian)" -> {
                _messages.value = emptyList<Message>()
                _messages.value = _messages.value + Message("Namaste, How may I help you!", false)
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _messages.value = emptyList()
    }


    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    context
                ) as T
            }
        }
    }
}
