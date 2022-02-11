package gst.trainingcourse.trelloapp.model

import java.io.Serializable

data class List(
    val id: Int,
    val boardId: Int,
    val name: String,
    val createdTime: String,
)