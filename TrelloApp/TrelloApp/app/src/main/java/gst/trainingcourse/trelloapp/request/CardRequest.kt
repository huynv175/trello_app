package gst.trainingcourse.trelloapp.request

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.Card

class CardRequest {

    companion object {
        private val collection = FirebaseFirestore.getInstance().collection("card")

        fun createCard(card: Card, callback: (isAdded: Boolean) -> Unit): Unit {
            getLastCardId { lastCardId ->
                val newCard: MutableMap<String, Any> = HashMap()
                val newId = lastCardId + 1
                newCard["id"] = newId
                newCard["listId"] = card.listId
                newCard["content"] = card.content
                newCard["startTime"] = card.startTime
                newCard["endTime"] = card.endTime

                collection
                    .add(newCard)
                    .addOnSuccessListener {
                        callback(true)
                    }.addOnFailureListener {
                        callback(false)
                    }
            }
        }

        fun getCardByListId(listId: Int, callback: (cards: MutableList<Card>?) -> Unit): Unit {
            collection
                .whereEqualTo("listId", listId)
                .get()
                .addOnSuccessListener { result ->
                    var list = mutableListOf<Card>()
                    for (document in result) {
                        val id = Integer.parseInt(
                            (document.data.getValue("id")?.toString() ?: -1) as String
                        )
                        val listId = Integer.parseInt(
                            (document.data.getValue("listId")?.toString() ?: -1) as String
                        )
                        val content = document.data.getValue("content")?.toString() ?: ""
                        val startTime = document.data.getValue("startTime")?.toString() ?: ""
                        val endTime = document.data.getValue("endTime")?.toString() ?: ""
                        val documentId = document.id
                        list.add(Card(id, listId, content, startTime, endTime, documentId))
                    }

                    callback(list)
                }.addOnFailureListener { exception ->
                    Log.d("asd", "Error getting documents: ", exception)
                    callback(null)
                }
        }

        fun updateCard(documentId: String, card: Card, callback: (isUpdated: Boolean) -> Unit) {
            val newCard: MutableMap<String, Any> = HashMap()
            newCard["id"] = card.id
            newCard["listId"] = card.listId
            newCard["content"] = card.content
            newCard["startTime"] = card.startTime ?: ""
            newCard["endTime"] = card.endTime ?: ""
            collection.document(documentId).update(newCard).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }

        private fun getLastCardId(callback: (id: Int) -> Unit): Unit {
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
