package gst.trainingcourse.trelloapp.view.board.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.databinding.ItemListBinding
import gst.trainingcourse.trelloapp.model.Card
import gst.trainingcourse.trelloapp.model.List
import gst.trainingcourse.trelloapp.request.CardRequest
import gst.trainingcourse.trelloapp.utils.Constants.ADD_LIST
import gst.trainingcourse.trelloapp.utils.Constants.EMPTY_STRING
import gst.trainingcourse.trelloapp.view.card.adapter.CardAdapter

class ListAdapter(private val iSendList: ISendList, val context: Context) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var list = mutableListOf<List>()
    private var listOfCards = mutableListOf<Card>()

    /**
     * set data for List
     */
    fun setData(data: MutableList<List>) {
        list.clear()
        list = data
        notifyDataSetChanged()
    }

    inner class MyViewHolder(binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleAddList = binding.tvTitleAddList
        val edListLayout = binding.edListLayout
        val edList = binding.edList
        val llActionList = binding.llActionList
        val tvCancelList = binding.tvCancelList
        val tvConfirmAddList = binding.tvAddList
        val cvCard = binding.cvCard
        val titleAddCard = binding.tvTitleAddCard
        val edCardLayout = binding.edCardLayout
        val edCard = binding.edCard
        val llActionCard = binding.llActionCard
        val tvCancelCard = binding.tvCancelCard
        val tvConfirmAddCard = binding.tvAddCard
        val rcvCard = binding.rcvCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            // set data for list and cant not click add list if list is not empty
            if (list.size > 0 && position < list.size) {
                holderDataList(holder, position)
                holderDataCardList(holder, position)
            } else {
                // card view add list doesn't display data list
                setCardViewAddList(holder)
            }
            //on cancel add list clicked
            tvCancelList.setOnClickListener {
                handleCancelAddList(holder)
            }
            //on confirm add list clicked
            tvConfirmAddList.setOnClickListener {
                handConfirmAddList(holder)
            }
            //add list click
            titleAddCard.setOnClickListener {
                handAddCard(holder)
            }
            //on cancel add card clicked
            tvCancelCard.setOnClickListener {
                handleCancelAddCard(holder)
            }
            //on confirm add card clicked
            tvConfirmAddCard.setOnClickListener {
                handleConfirmAddCard(holder, position)
            }
        }
    }

    private fun holderDataCardList(holder: MyViewHolder, position: Int) {
        holder.apply {
            val cardAdapter = CardAdapter()
            val layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rcvCard.layoutManager = layoutManager
            rcvCard.setHasFixedSize(true)
            rcvCard.adapter = cardAdapter
            Log.i("Test", "${list[position]}")
            CardRequest.getCardByListId(list[position].id) { listCard ->
                Log.i("Test", "$listCard")
                if (listCard != null) {
                    listCard.sortByDescending { it.id }
                    listOfCards.addAll(listCard)
                    cardAdapter.setData(listCard)
                }
            }
        }
    }

    private fun holderDataList(holder: ListAdapter.MyViewHolder, position: Int) {
        holder.apply {
            titleAddList.text = list[position].name
            titleAddList.isClickable = false
            cvCard.visibility = View.VISIBLE
        }
    }

    private fun setCardViewAddList(holder: MyViewHolder) {
        holder.apply {
            // if list is empty or wanna set add list
            cvCard.visibility = View.GONE
            titleAddList.text = ADD_LIST
            //add list click
            titleAddList.setOnClickListener {
                handleAddList(holder)
            }
        }
    }

    private fun handleAddList(holder: MyViewHolder) {
        holder.apply {
            titleAddList.visibility = View.GONE
            edListLayout.visibility = View.VISIBLE
            llActionList.visibility = View.VISIBLE
        }
    }

    private fun handleCancelAddList(holder: ListAdapter.MyViewHolder) {
        holder.apply {
            titleAddList.visibility = View.VISIBLE
            edListLayout.visibility = View.GONE
            llActionList.visibility = View.GONE
            edList.setText(EMPTY_STRING)
        }
    }

    private fun handConfirmAddList(holder: ListAdapter.MyViewHolder) {
        if (holder.edList.text.toString().isNotEmpty()) {
            iSendList.actionSend(holder.edList.text.toString().trim())
        }
    }

    private fun handAddCard(holder: ListAdapter.MyViewHolder) {
        holder.apply {
            edCardLayout.visibility = View.VISIBLE
            llActionCard.visibility = View.VISIBLE
            titleAddCard.visibility = View.GONE
        }
    }

    private fun handleCancelAddCard(holder: ListAdapter.MyViewHolder) {
        holder.apply {
            titleAddCard.visibility = View.VISIBLE
            edCardLayout.visibility = View.GONE
            llActionCard.visibility = View.GONE
            edCard.setText(EMPTY_STRING)
        }
    }

    private fun handleConfirmAddCard(holder: MyViewHolder, position: Int) {
        holder.apply {
            if (edCard.text.toString().isNotEmpty()) {
                val content = edCard.text.toString().trim()
                Log.i("Test", "${list[position].id}")
                val card = Card(0, list[position].id, content, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING)
                edCard.setText(EMPTY_STRING)
                edCardLayout.visibility = View.GONE
                llActionCard.visibility = View.GONE
                titleAddCard.visibility = View.VISIBLE
                CardRequest.createCard(card) { isCreated ->
                    if (isCreated) {
                        val cardAdapter = CardAdapter()
                        listOfCards.add(card)
                        listOfCards.sortByDescending { it.id }
                        cardAdapter.setData(listOfCards)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    interface ISendList {
        fun actionSend(message: String)
    }
}