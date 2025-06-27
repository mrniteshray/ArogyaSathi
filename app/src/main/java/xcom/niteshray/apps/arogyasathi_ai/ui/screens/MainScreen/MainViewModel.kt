package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xcom.niteshray.apps.arogyasathi_ai.utils.SpeechRecognizerManager

class MainViewModel : ViewModel() {
    private val _fullText = MutableStateFlow("")
    val fullText: StateFlow<String> = _fullText.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private lateinit var speechRecognizerManager: SpeechRecognizerManager

    fun startListening(context: Context) {
        speechRecognizerManager = SpeechRecognizerManager(context).apply {
            startListening(
                onFinalResult = { finalSegment ->
                    viewModelScope.launch {
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
                startListening(context)
            }
        }
    }
    override fun onCleared() {
        speechRecognizerManager.destroy()
        super.onCleared()
    }
}