package xcom.niteshray.apps.arogyasathi_ai.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val message : String,
    val user : Boolean
){
    constructor() : this ("",false)
}
