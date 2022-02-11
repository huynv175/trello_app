package gst.trainingcourse.trelloapp.model

data class Board(
    val id: Int,
    val workspaceId: Int,
    val name: String,
    val createdTime: String
)