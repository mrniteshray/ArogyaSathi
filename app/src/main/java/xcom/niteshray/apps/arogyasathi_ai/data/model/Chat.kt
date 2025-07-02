package xcom.niteshray.apps.arogyasathi_ai.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val chatId: String = "",
    val title: String = "",
    val messages: List<Message> = emptyList(),
    @ServerTimestamp val createdAt: Date? = null
){
    constructor() : this("","",emptyList() , null)
}