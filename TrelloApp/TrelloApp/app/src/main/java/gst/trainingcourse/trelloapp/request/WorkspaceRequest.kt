package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.Workspace

class WorkspaceRequest {

    companion object {
        val collection = FirebaseFirestore.getInstance().collection("workspace")

        fun createWorkspace(workspace: Workspace, callback: (isAdded: Boolean) -> Unit): Unit {
            getLastWorkspaceId { lastWsId ->
                val newWs: MutableMap<String, Any> = HashMap()
                val newId = lastWsId + 1
                newWs["id"] = newId
                newWs["userId"] = workspace.userId
                newWs["name"] = workspace.name
                newWs["createdTime"] = workspace.createdTime

                collection
                    .add(newWs)
                    .addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
            }
        }

        fun getWorkspaceByUser(userId: Int, callback: (workspace: Workspace? ) -> Unit) {
            collection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    var workspace: Workspace? = null
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val userId = Integer.parseInt(
                            (document.data.getValue("userId")?.toString() ?: -1) as String
                        )
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val createdTime = document.data.getValue("createdTime")?.toString() ?: ""
                        workspace = Workspace(id, userId, name, createdTime)
                        break
                    }

                    callback(workspace)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        private fun getLastWorkspaceId(callback: (id: Int) -> Unit): Unit {
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