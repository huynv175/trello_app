package gst.trainingcourse.trelloapp.request


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.ActivityHistory
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.UserBoard
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class BoardRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("board")

        fun createBoard(userId: Int, board: Board, callback: (board: Board?) -> Unit): Unit {
            getLastBoardId { lastBoardId ->
                val newBoard: MutableMap<String, Any> = HashMap()
                val newId = lastBoardId + 1
                newBoard["id"] = newId
                newBoard["workspaceId"] = board.workspaceId
                newBoard["name"] = board.name
                newBoard["createdTime"] = board.createdTime

                collection
                    .add(newBoard)
                    .addOnSuccessListener { result ->
                        // create default user board
                        UserBoardRequest.createUserBoard(UserBoard(0, userId, newId)) { isAdded ->
                            Log.d("created user board?", isAdded.toString())
                        }

                        // create new activity history
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        val content = "User with ID = $userId created new board"
                        ActivityHistoryRequest.createActivityHistory(
                            ActivityHistory(0, newId, formatter.format(Calendar.getInstance().time), content)
                        ) { isAdded ->
                            Log.d("created board?", isAdded.toString())
                        }

                        callback(Board(newId, board.workspaceId, board.name, board.createdTime))
                    }.addOnFailureListener {
                        callback(null)
                    }
            }
        }

        fun getBoardByDocumentId(id: String, callback: (board: Board?) -> Unit): Unit {
            collection.document(id).get().addOnSuccessListener { result ->
                val id = Integer.parseInt(
                    (result.data?.getValue("id")?.toString() ?: -1) as String
                )
                val workspaceId = Integer.parseInt(
                    (result.data?.getValue("workspaceId")?.toString() ?: -1) as String
                )
                val name = result.data?.getValue("name")?.toString() ?: ""
                val createdTime = result.data?.getValue("createdTime")?.toString() ?: ""
                callback(Board(id, workspaceId, name, createdTime))
            }.addOnFailureListener {
                callback(null)
            }
        }

        fun getBoardByWorkspaceId(workspaceId: Int, callback: (boards: MutableList<Board>?) -> Unit) {
            collection
                .whereEqualTo("workspaceId", workspaceId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<Board>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val workspaceId = Integer.parseInt(
                            (document.data.getValue("workspaceId")?.toString() ?: -1) as String
                        )
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val createdTime = document.data.getValue("createdTime")?.toString() ?: ""
                        list.add(Board(id, workspaceId, name, createdTime))
                    }

                   callback(list)

                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun getBoardByWorkspace(userId: Int, workspaceId: Int, callback: (boards: MutableList<Board>?) -> Unit) {

            collection
                .whereEqualTo("workspaceId", workspaceId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<Board>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val workspaceId = Integer.parseInt(
                            (document.data.getValue("workspaceId")?.toString() ?: -1) as String
                        )
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val createdTime = document.data.getValue("createdTime")?.toString() ?: ""
                        list.add(Board(id, workspaceId, name, createdTime))
                    }

                    // filter by UserBoard
                    UserBoardRequest.getBoardByUser(userId) { userBoardList ->
                        var finalList = mutableListOf<Board>()
                        if (userBoardList != null) {
                            for(bItem in list) {
                                for(ubItem in userBoardList) {
                                    if(ubItem.boardId == bItem.id) {
                                        finalList.add(bItem)
                                    }
                                }
                            }
                        }
                        Log.d("ads", list.toString())
                        Log.d("ads", finalList.toString())
                        callback(finalList)
                    }

                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun getBoardById(boardId: Int, callback: (board: Board?) -> Unit): Unit {
            collection.limit(1).whereEqualTo("id", boardId).get().addOnSuccessListener { result ->
                for(document in result) {
                    val id = Integer.parseInt(
                        (document.data?.getValue("id")?.toString() ?: -1) as String
                    )
                    val workspaceId = Integer.parseInt(
                        (document.data?.getValue("workspaceId")?.toString() ?: -1) as String
                    )
                    val name = document.data?.getValue("name")?.toString() ?: ""
                    val createdTime = document.data?.getValue("createdTime")?.toString() ?: ""
                    callback(Board(id, workspaceId, name, createdTime))
                    break
                }

            }.addOnFailureListener {
                callback(null)
            }
        }

        private fun getLastBoardId(callback: (id: Int) -> Unit): Unit {
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