package gst.trainingcourse.trelloapp.view.card

import android.app.DatePickerDialog
import android.content.*
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.databinding.FragmentCardBinding
import gst.trainingcourse.trelloapp.model.Card
import gst.trainingcourse.trelloapp.request.CardRequest
import gst.trainingcourse.trelloapp.utils.Constants.OBJECT_CARD
import gst.trainingcourse.trelloapp.view.card.adapter.CardAdapter

class CardFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentCardBinding
    private lateinit var cardObject: Card
    private var listCard = mutableListOf<Card>()
    private var iSendCardList: ISendCardList

    init {
        val cardAdapter = CardAdapter()
        iSendCardList = cardAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
        intView()
        initAction()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initData() {
        cardObject = arguments?.getSerializable(OBJECT_CARD) as Card
        CardRequest.getCardByListId(cardObject.id) {
            it?.let { it1 -> listCard.addAll(it1) }
        }
    }

    private fun intView() {

        (context as AppCompatActivity).supportActionBar?.title = "Card"

        binding.apply {
            cardDetailTitle.text = cardObject.content
            if (cardObject.startTime.isNotEmpty()) {
                startTime.text = cardObject.startTime
            }
            if (cardObject.endTime.isNotEmpty()) {
                endTime.text = cardObject.endTime
            }
        }
    }

    override fun onStop() {
        for (item in listCard) {
            if (item.id == cardObject.id) {
                item.startTime = cardObject.startTime
                item.endTime = cardObject.endTime
            }
        }
        listCard.sortByDescending { it.id }
        cardObject.documentId?.let {
            CardRequest.updateCard(it, cardObject) {
                Log.i("Test", "$it")
            }
        }
        iSendCardList.sendData(cardObject.listId)
        super.onStop()
    }

    private fun initAction() {
        binding.apply {
            startTime.setOnClickListener(this@CardFragment)
            endTime.setOnClickListener(this@CardFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v) {
            binding.startTime -> handleStartTime()
            binding.endTime -> handleEndTime()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleEndTime() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd =
            activity?.let {
                DatePickerDialog(it, { view, year, monthOfYear, dayOfMonth ->
                    Log.i("Test1", "$monthOfYear")
                    "Kết thúc ngày $dayOfMonth tháng $monthOfYear".also {
                        cardObject.endTime = it
                        binding.endTime.text = it
                    }

                }, year, month, day)
            }

        dpd?.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleStartTime() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd =
            activity?.let {
                DatePickerDialog(it, { view, year, monthOfYear, dayOfMonth ->
                    "Bắt đầu ngày $dayOfMonth tháng $monthOfYear".also {
                        cardObject.startTime = it
                        binding.startTime.text = it
                    }

                }, year, month, day)
            }
        dpd?.show()
    }

    interface ISendCardList {
        fun sendData(listId: Int)
    }

}