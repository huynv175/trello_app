package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.ActivityHistory

class ActivityHistoryRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("activity_history")

        fun createActivityHistory(
            activityHistory: ActivityHistory,
            callback: (isAdded: Boolean) -> Unit
        ): Unit {
            getLastActivityHistory { lastAHId ->
                val newAH: MutableMap<String, Any> = HashMap()
                val newId = lastAHId + 1
                newAH["id"] = newId
                newAH["boardId"] = activityHistory.boardId
                newAH["time"] = activityHistory.time
                newAH["content"] = activityHistory.content

                collection
                    .add(newAH)
                    .addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
            }
        }

        private fun getLastActivityHistory(callback: (id: Int) -> Unit): Unit {
            collection
                .limit(1)
                .orderBy("id", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        callback(Integer.parseInt(document.data.getValue("id").toString()))
                    }
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(0)
                }
        }

    }

}