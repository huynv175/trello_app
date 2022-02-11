package gst.trainingcourse.trelloapp.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val EMPTY_STRING = ""
    const val ADD_LIST = "Thêm danh sách"
    const val ACTION_CARD = "action_card"
    const val ACTION_SCROLL = "action_scroll"
    const val POSITION_SCROLL = "position_scroll"
    const val OBJECT_CARD = "card"
    const val CONTENT_CARD = "content_card"
    const val IMAGE_PICK_CODE = 1000
    const val PERMISSION_CODE = 1001
    const val MY_PREFS_NAME = "my_pref"
    const val DATA_USER = "data_user"
    const val BOARD_ID = "board_id"

    /**
     * getCurrentDate.
     */
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}