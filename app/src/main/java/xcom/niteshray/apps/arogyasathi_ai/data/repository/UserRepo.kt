package xcom.niteshray.apps.arogyasathi_ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import xcom.niteshray.apps.arogyasathi_ai.data.model.Chat
import xcom.niteshray.apps.arogyasathi_ai.data.model.Message
import xcom.niteshray.apps.arogyasathi_ai.data.model.User

class UserRepo {
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun fetchUser(userId: String): User? {
        return try {
            val snapshot = firebaseFirestore.collection("users")
                .document(userId)
                .get()
                .await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveUserData(user: User) = withContext(Dispatchers.IO) {
        try {
            firebaseFirestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
        } catch (e: Exception) {

        }
    }

    suspend fun SaveChat(userId : String , title : String , messages : List<Message>) : String {
        val chatId = firebaseFirestore.collection("users")
            .document(userId)
            .collection("chats")
            .document()
            .id
        val chat = Chat(
            chatId = chatId,
            title = title.take(50),
            messages = messages.map { Message(it.message, it.user) }
        )
        firebaseFirestore.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .set(chat)
            .await()
        return chatId
    }

    suspend fun UpdateChat(userId: String , chatId : String , messages :List<Message>){
        val chat = firebaseFirestore.collection("users")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .get()
            .await()
            .toObject(Chat::class.java)
        if (chat != null) {
            firebaseFirestore.collection("users")
                .document(userId)
                .collection("chats")
                .document(chatId)
                .update("messages", messages.map { Message( it.message, it.user) })
                .await()
        }
    }

    fun getChats(userId: String?): Flow<List<Chat>> = flow {
        try {
            val snapshot = firebaseFirestore.collection("users")
                .document(userId!!)
                .collection("chats")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val chats = snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
            emit(chats)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}