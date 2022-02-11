package gst.trainingcourse.trelloapp.view.card.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.databinding.ItemCardBinding
import gst.trainingcourse.trelloapp.model.Card
import gst.trainingcourse.trelloapp.request.CardRequest
import gst.trainingcourse.trelloapp.utils.Constants.ACTION_CARD
import gst.trainingcourse.trelloapp.utils.Constants.ACTION_SCROLL
import gst.trainingcourse.trelloapp.utils.Constants.OBJECT_CARD
import gst.trainingcourse.trelloapp.utils.Constants.POSITION_SCROLL
import gst.trainingcourse.trelloapp.view.card.CardFragment

class CardAdapter: RecyclerView.Adapter<CardAdapter.MyViewHolder>(), CardFragment.ISendCardList {

    private lateinit var context: Context
    private var listCard = mutableListOf<Card>()
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
        Log.i("1999", "${recyclerView.context}")
    }

    fun setData(data: MutableList<Card>) {
        listCard.clear()
        listCard = data
        notifyDataSetChanged()
    }
    inner class MyViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        val titleCard = binding.titleCard
        val cardParent = binding.cardParent
        val startTime = binding.startTimeCardList
        val endTime = binding.endTimeCardList
        val ll1 = binding.ll1
        val ll2 = binding.ll2

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            titleCard.text = listCard[position].content
            cardParent.setOnClickListener {
                navigateToCardDetail(position)
            }
            if(listCard[position].startTime.isNotEmpty()) {
                ll1.visibility = View.VISIBLE
                startTime.text = listCard[position].startTime
            }
            if(listCard[position].startTime.isNotEmpty()) {
                ll2.visibility = View.VISIBLE
                endTime.text = listCard[position].endTime

            }
        }
    }

    private fun navigateToCardDetail(position: Int) {
        val intent = Intent(ACTION_CARD)
        val bundle = Bundle()
        bundle.putSerializable(OBJECT_CARD, listCard[position])
        intent.putExtras(bundle)
        intent.putExtra(POSITION_SCROLL, listCard[position].listId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun getItemCount(): Int {
        return listCard.size
    }

    override fun sendData(listId: Int) {
        CardRequest.getCardByListId(listId) {it->
            this.listCard.clear()
            this.listCard = it!!
            notifyDataSetChanged()
        }
    }
}