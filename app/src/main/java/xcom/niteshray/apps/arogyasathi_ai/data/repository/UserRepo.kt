package xcom.niteshray.apps.arogyasathi_ai.data.repository

import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import xcom.niteshray.apps.arogyasathi_ai.data.model.User

class UserRepo {
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun fetchUser(userId: String): User? {
        return try {
            val snapshot = firebaseFirestore.collection("Users")
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
}