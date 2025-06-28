package xcom.niteshray.apps.arogyasathi_ai.data.model

data class User(
    val uid : String = "",
    val name : String = "",
    val email : String ="",
    val photoUrl : String ="",
    val signInTime: Long = System.currentTimeMillis()
)
