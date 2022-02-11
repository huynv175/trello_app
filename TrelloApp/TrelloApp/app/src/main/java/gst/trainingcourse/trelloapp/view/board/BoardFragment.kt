package gst.trainingcourse.trelloapp.view.board

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentBoardBinding
import gst.trainingcourse.trelloapp.model.Card
import gst.trainingcourse.trelloapp.model.List
import gst.trainingcourse.trelloapp.request.ListRequest
import gst.trainingcourse.trelloapp.utils.Constants.ACTION_CARD
import gst.trainingcourse.trelloapp.utils.Constants.ACTION_SCROLL
import gst.trainingcourse.trelloapp.utils.Constants.BOARD_ID
import gst.trainingcourse.trelloapp.utils.Constants.OBJECT_CARD
import gst.trainingcourse.trelloapp.utils.Constants.POSITION_SCROLL
import gst.trainingcourse.trelloapp.utils.Constants.getCurrentDate
import gst.trainingcourse.trelloapp.view.MainActivity
import gst.trainingcourse.trelloapp.view.board.adapter.ListAdapter
import gst.trainingcourse.trelloapp.view.card.CardFragment
import gst.trainingcourse.trelloapp.view.card.adapter.CardAdapter
import gst.trainingcourse.trelloapp.viewmodel.board.BoardViewModel


/**
 * A simple [BaseFragment] subclass.
 * Use the [BoardFragment] factory method to
 * create an instance of this fragment.
 */
class BoardFragment : BaseFragment<FragmentBoardBinding>() {

    private val boardViewModel: BoardViewModel by viewModels()
    private var positionScroll: Int? = null
    private var list = mutableListOf<List>()


    private val localBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CARD) {
                val bundle = intent.extras
                positionScroll = intent.getIntExtra(POSITION_SCROLL, 0)
                if (findNavController().currentDestination?.id == R.id.boardFragment) {
                    view?.findNavController()?.navigate(R.id.action_boardFragment_to_cardFragment, bundle)
                }
            }
        }

    }

    /**
     * implement interface get list info to create list
     */
    private val iSendList = object : ListAdapter.ISendList {
        override fun actionSend(message: String) {
            ListRequest.getLastListId {
                boardViewModel.createList(
                    List(
                        it,
                        boardId!!,
                        message,
                        getCurrentDate()
                    )
                )
            }
        }

    }

    /**
     * override fun createBinding from Base class.
     * @param inflater[LayoutInflater]
     * @param container[ViewGroup]
     * @param savedInstanceState[Bundle]
     * @return FragmentBoardBinding
     */
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentBoardBinding {
        return FragmentBoardBinding.inflate(inflater, container, false)
    }


    override fun initView() {
        (activity as MainActivity).supportActionBar?.show()

        //(context as AppCompatActivity).supportActionBar?.title = "Board"
    }

    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.over_flow_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.member -> {
                val bundle = Bundle()
                bundle.putInt(BOARD_ID, boardId!!)
                view?.findNavController()?.navigate(R.id.action_boardFragment_to_memberFragment, bundle)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData() {
        val filter = IntentFilter(ACTION_CARD)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(localBroadcastManager, filter)

        boardId = arguments?.getInt(BOARD_ID)
        val boardName = arguments?.getString("Board_name")
        (context as AppCompatActivity).supportActionBar?.title = boardName
        Log.i("Test222", "$boardId")
        if (boardId != null) {
            boardViewModel.getListTask(boardId = boardId!!)
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(localBroadcastManager)
        super.onDestroy()
    }

    /**
     * override fun observeLiveData from Base class.
     */
    override fun observeLiveData() {
        boardViewModel.list.observe(viewLifecycleOwner) { list ->
            val lisTAdapter = ListAdapter(iSendList, requireContext())
            binding.rcvList.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                if(positionScroll == null) {
                    scrollToPosition(list!!.size - 1)
                } else {
                    scrollToPosition(positionScroll!!)
                    positionScroll = null
                }
                adapter = lisTAdapter
            }
            binding.rcvList.setItemViewCacheSize(9)
            if (list != null) {
                list.sortBy { it.id }
                lisTAdapter.setData(list)
            }
        }
    }

    companion object {
        var boardId: Int? = null
    }


}