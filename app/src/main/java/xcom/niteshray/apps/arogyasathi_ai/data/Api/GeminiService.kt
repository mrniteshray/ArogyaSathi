package xcom.niteshray.apps.arogyasathi_ai.data.Api

import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import xcom.niteshray.apps.arogyasathi_ai.data.model.Message

class GeminiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyDQGTG3AB18NhUM7oK2W3njKUkDjtPg5AI"
    )

    suspend fun getResponseFromGemini(message : List<Message> , userPrompt : String) : String {
        val prompt = getPrompt(message,userPrompt)
        val response = generativeModel.generateContent(prompt)
        return response.text ?: "Sorry, I couldn't generate a response."
    }

    fun getPrompt(messages: List<Message> , userPrompt: String) : String{
        val chatHistory = messages.joinToString("\n") { message ->
            val sender = if (message.isUser) "User" else "Assistant"
            "$sender: ${message.message}"
        }
        return """
            You are a professional health assistant, acting like a doctor. Based on the user's latest input : $userPrompt, provide a concise (under 100 words), actionable health tip or home remedy directly addressing their health concern. 
            Use the chat history to avoid repeating advice and provide fresh, relevant suggestions:
            ---
            $chatHistory
            ---
            Avoid emotional phrases like "मुझे दुख हुआ" or "I'm sorry". 
            Do not provide medical diagnoses or specific treatments. Use a clear, supportive, and professional tone, like a doctor giving practical advice.
        """.trimIndent()
    }

}