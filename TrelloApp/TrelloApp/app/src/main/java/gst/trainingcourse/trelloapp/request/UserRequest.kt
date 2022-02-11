package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.List
import gst.trainingcourse.trelloapp.model.User

class UserRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("user")

        fun login(email: String, password: String, callback: (user: User?) -> Unit): Unit {
            collection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (password == document.data.getValue("password")) {
                            val id = document.data.getValue("id").toString().toInt()
                            getUserById(id) {
                                  callback(it)
                            }
                        } else {
                            callback(null)
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun checkUserRegister(user: User, callback: (isExisted: Boolean) -> Unit) {
            // TODO: check if email is already existed
            collection
                .whereEqualTo("email", user.email)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    if (result.size() > 0) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                }
        }

        fun register(user: User, callback: (user: User?) -> Unit): Unit {
            getLastUserId { lastUserId ->
                val newUser: MutableMap<String, Any> = HashMap()
                val newId = lastUserId + 1
                newUser["id"] = newId
                newUser["accountName"] = user.accountName
                newUser["avatar"] = user.avatar
                newUser["email"] = user.email
                newUser["password"] = user.password
                newUser["name"] = user.name

                collection
                    .add(newUser)
                    .addOnSuccessListener {
                        callback(User(newId, user.accountName, user.password,user.name, user.avatar, user.email))
                        Log.d("asd", "successfully register")
                    }.addOnFailureListener {
                        callback(null)
                        Log.d("asd", "failed register")
                    }
            }

        }

        fun getUserById(userId: Int, callback: (user: User?) -> Unit): Unit {
            UserRequest.collection
                .whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { result ->
                    var user: User? = null
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val accountName = document.data.getValue("accountName")?.toString() ?: ""
                        val password = document.data.getValue("password")?.toString() ?: ""
                        val name = document.data.getValue("name")?.toString() ?: ""
                        val avatar = document.data.getValue("avatar")?.toString() ?: ""
                        val email = document.data.getValue("email")?.toString() ?: ""
                        user = User(id, accountName, password, name, avatar, email)
                    }

                    callback(user)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }


        private fun getLastUserId(callback: (id: Int) -> Unit): Unit {
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

        fun getUserByEmail(email: String, callback: (user: User?) -> Unit): Unit {
            collection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    var user: User? = null
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data?.getValue("id")?.toString() ?: -1) as String
                        )
                        val accountName = document.data?.getValue("accountName")?.toString() ?: ""
                        val password = document.data?.getValue("password")?.toString() ?: ""
                        val name = document.data?.getValue("name")?.toString() ?: ""
                        val avatar = document.data?.getValue("avatar")?.toString() ?: ""
                        val email = document.data?.getValue("email")?.toString() ?: ""
                        user = User(id, accountName, password, name, avatar, email)
                        break
                    }
                    callback(user)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }
    }
}