package xcom.niteshray.apps.arogyasathi_ai.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.*
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import xcom.niteshray.apps.arogyasathi_ai.R
import xcom.niteshray.apps.arogyasathi_ai.data.model.User
import xcom.niteshray.apps.arogyasathi_ai.data.repository.UserRepo

class GoogleSignInHelper {
    companion object {
        fun doSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context,
                    )

                    val credential = result.credential
                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleCredential.idToken

                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val user = Firebase.auth.signInWithCredential(firebaseCredential).await().user

                        if (user != null && !user.isAnonymous) {

                            val user = User(
                                uid= user.uid,
                                name = (user.displayName ?: "Unknown"),
                                email =  (user.email ?: "Unknown"),
                                photoUrl = (user.photoUrl?.toString() ?: ""),
                            )
                            UserRepo().saveUserData(user)
                            login()
                        }
                    }
                } catch (e: NoCredentialException) {
                    launcher?.launch(getIntent())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }
    }
}
