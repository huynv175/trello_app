package gst.trainingcourse.trelloapp.model

data class ActivityHistory(
    val id: Int,
    val boardId: Int,
    val time: String,
    val content: String
)