package gst.trainingcourse.trelloapp.view.board.member

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentMemberBinding
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.request.UserBoardRequest
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.utils.Constants.BOARD_ID
import gst.trainingcourse.trelloapp.view.MainActivity

class MemberFragment : BaseFragment<FragmentMemberBinding>() {

    private var boardId = 0

    private var layoutManager: RecyclerView.LayoutManager? = null

    private var memberAdapter: MemberAdapter? = null

    private var recyclerView: RecyclerView? = null

    private var memberIdList = mutableListOf<Int>()

    private var memberList = mutableListOf<User>()

    /**
     * get data of UserId from firebase by boardId
     */
    private fun getMemberId() {
        memberIdList.clear()
        UserBoardRequest.getUserByBoard(boardId) { list ->
            if (list != null) {
                for (userBoard in list) {
                    Log.d("HuyNV31", "${userBoard.userId}")
                    memberIdList.add(userBoard.userId)
                }
                getDataMember()
            }
        }
    }

    /**
     * get data Member from firebase by Id
     */
    private fun getDataMember() {
        memberList.clear()
        for (index in memberIdList.indices) {
            UserRequest.getUserById(memberIdList[index]) { user ->
                if (user != null) {
                    memberList.add(user)
                }
                if (index == memberIdList.size - 1) {
                    memberAdapter?.setDataBoard(memberList)
                    recyclerView?.adapter = memberAdapter
                }
            }
        }
    }



    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMemberBinding {
        return FragmentMemberBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        Log.d("HuyNV31", "on initView")
        (activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.home_bar_title)
    }

    override fun initData() {
        Log.d("HuyNV31", "on initData")
        boardId = arguments?.getInt(BOARD_ID) ?: 0

        recyclerView = view?.findViewById(R.id.recyclerView_member)
        layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        memberAdapter = MemberAdapter()
        getMemberId()
    }

    override fun initAction() {
        binding.apply {
            btnAddMember.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(BOARD_ID, boardId)
                view?.findNavController()
                    ?.navigate(R.id.action_memberFragment_to_addMemberFragment, bundle)
            }
        }
    }
}

