package gst.trainingcourse.trelloapp.model

import java.io.Serializable

data class User(
    val id: Int?,
    val accountName: String,
    val password: String,
    val name: String,
    val avatar: String,
    val email: String
) : Serializable