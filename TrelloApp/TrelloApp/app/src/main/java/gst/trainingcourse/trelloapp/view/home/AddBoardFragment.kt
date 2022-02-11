package gst.trainingcourse.trelloapp.view.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentAddBoardBinding
import gst.trainingcourse.trelloapp.databinding.FragmentHomeBinding
import gst.trainingcourse.trelloapp.databinding.FragmentIntroBinding
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.UserBoard
import gst.trainingcourse.trelloapp.model.Workspace
import gst.trainingcourse.trelloapp.request.BoardRequest
import gst.trainingcourse.trelloapp.request.UserBoardRequest
import gst.trainingcourse.trelloapp.request.WorkspaceRequest
import gst.trainingcourse.trelloapp.utils.Constants.BOARD_ID
import gst.trainingcourse.trelloapp.view.MainActivity
import java.time.LocalDate
import java.util.*


class AddBoardFragment : BaseFragment<FragmentAddBoardBinding>() {

    private var etAddBoard: EditText? = null
    private var tvCheckBoard: TextView? = null

    private var workspace: Workspace? = null

    private var userId: Int? = null

    private fun getData() {
        userId?.let {
            WorkspaceRequest.getWorkspaceByUser(it) { list ->
                if (list != null) {
                    workspace = list
                }
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAddBoardBinding {
        return FragmentAddBoardBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        //show action bar
        (activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.add_board_bar_title)
        userId = arguments?.getInt("userId")
        getData()
        etAddBoard = view?.findViewById(R.id.et_board_name)
        tvCheckBoard = view?.findViewById(R.id.tv_check_board)

    }

    /**
     * override fun initAction from Base class.
     */
    override fun initAction() {
        binding.btnCreateBoard.setOnClickListener {
            addBoard()
        }
    }

    private fun addBoard() {
        val createdTime = Calendar.getInstance().time.toString()
        var boardId = 0
        var workspaceId = 0
        if (workspace != null) {
            workspaceId = workspace?.id!!
        }
        if (etAddBoard?.text.isNullOrEmpty()) {
            tvCheckBoard?.text = "Nhap ten Board"
        } else {
            tvCheckBoard?.text = ""
            val name = etAddBoard?.text?.trim().toString()
            userId?.let {
                BoardRequest.createBoard(it, Board(0, workspaceId, name, createdTime)) { board ->
                    if (board != null) {
                        boardId = board.id
                        val bundle = Bundle()
                        bundle.putInt(BOARD_ID, boardId)
                        Log.i("Test333", "$boardId")
                        view?.findNavController()
                            ?.navigate(R.id.action_addBoardFragment_to_boardFragment, bundle)
                    }
                }

            }

        }

    }
}