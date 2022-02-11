package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.*

class UserBoardRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("user_board")

        fun createUserBoard(userBoard: UserBoard, callback: (isAdded: Boolean) -> Unit): Unit {
            getLastUserBoardId { lastUBId ->
                val newUB: MutableMap<String, Any> = HashMap()
                val newId = lastUBId + 1
                newUB["id"] = newId
                newUB["userId"] = userBoard.userId
                newUB["boardId"] = userBoard.boardId

                collection
                    .add(newUB)
                    .addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
            }
        }


        fun getBoardByUser(
            userId: Int,
            callback: (userBoards: MutableList<UserBoard>?) -> Unit
        ): Unit {
            collection
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<UserBoard>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val userId = Integer.parseInt(
                            (document.data.getValue("userId")?.toString() ?: -1) as String
                        )
                        val boardId = Integer.parseInt(
                            (document.data.getValue("boardId")?.toString() ?: -1) as String
                        )

                        list.add(UserBoard(id, userId, boardId))
                    }

                    callback(list)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun getUserByBoard(
            boardId: Int,
            callback: (userBoards: MutableList<UserBoard>?) -> Unit
        ): Unit {
            collection
                .whereEqualTo("boardId", boardId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<UserBoard>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val userId = Integer.parseInt(
                            (document.data.getValue("userId")?.toString() ?: -1) as String
                        )
                        val boardId = Integer.parseInt(
                            (document.data.getValue("boardId")?.toString() ?: -1) as String
                        )

                        list.add(UserBoard(id, userId, boardId))
                    }

                    callback(list)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        private fun getLastUserBoardId(callback: (id: Int) -> Unit): Unit {
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