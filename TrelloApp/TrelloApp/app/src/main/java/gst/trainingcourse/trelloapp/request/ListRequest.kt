package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.List


class ListRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("list")

        fun createList(list: List, callback: (isAdded: Boolean) -> Unit): Unit {
            getLastListId { lastListId ->
                val newList: MutableMap<String, Any> = HashMap()
                val newId = lastListId + 1
                newList["id"] = newId
                newList["boardId"] = list.boardId
                newList["name"] = list.name
                newList["createdTime"] = list.createdTime

                collection
                    .add(newList)
                    .addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
            }
        }

        fun getListByBoard(boardId: Int, callback: (lists: MutableList<List>?) -> Unit): Unit  {
            collection
                .whereEqualTo("boardId", boardId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<List>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val boardId = Integer.parseInt(
                            (document.data.getValue("boardId")?.toString() ?: -1) as String
                        )
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val createdTime = document.data.getValue("createdTime")?.toString() ?: ""

                        list.add(List(id, boardId, name, createdTime))
                    }

                    callback(list)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun getLastListId(callback: (id: Int) -> Unit): Unit {
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
