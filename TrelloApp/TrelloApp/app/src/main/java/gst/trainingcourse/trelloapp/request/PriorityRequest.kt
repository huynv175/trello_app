package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import gst.trainingcourse.trelloapp.model.List
import gst.trainingcourse.trelloapp.model.Priority

class PriorityRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("priority")

        fun getPriority(callback: (priorities: MutableList<Priority>?) -> Unit): Unit {
            collection
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<Priority>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val color = document.data.getValue("color")?.toString() ?: ""

                        list.add(Priority(id, name, color))
                    }

                    callback(list)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }
    }

}