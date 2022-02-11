package gst.trainingcourse.trelloapp.model

import java.io.Serializable

data class Card(
    val id: Int,
    val listId: Int,
    val content: String,
    var startTime: String,
    var endTime: String,
    val documentId: String?
) : Serializable