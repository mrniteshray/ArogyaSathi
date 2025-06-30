package xcom.niteshray.apps.arogyasathi_ai.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    init {
        val hindi = Locale.forLanguageTag("en-US")

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = hindi
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.setPitch(1.0f)
                isInitialized = true
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun destroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        isInitialized = false
    }
}