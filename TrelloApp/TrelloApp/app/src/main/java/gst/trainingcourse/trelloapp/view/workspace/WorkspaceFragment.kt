package gst.trainingcourse.trelloapp.view.workspace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentWorkspaceBinding
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.Workspace
import gst.trainingcourse.trelloapp.request.BoardRequest
import gst.trainingcourse.trelloapp.request.WorkspaceRequest
import gst.trainingcourse.trelloapp.utils.Constants
import gst.trainingcourse.trelloapp.view.MainActivity
import gst.trainingcourse.trelloapp.view.home.HomeAdapter


class WorkspaceFragment : BaseFragment<FragmentWorkspaceBinding>() {

    private var userInfo = MainActivity.user

    private var layoutManager: RecyclerView.LayoutManager? = null

    private var workspaceAdapter: WorkspaceAdapter? = null

    private var recyclerView: RecyclerView? = null

    private var workspace: Workspace? = null

    private var boardList = mutableListOf<Board>()


    private fun getData() {
        Log.d("HuyNV31", "on getData")
        if (userInfo != null) {
            boardList.clear()
            WorkspaceRequest.getWorkspaceByUser(userInfo?.id!!) { mWorkspace ->
                Log.d("HuyNV31", mWorkspace.toString())
                if (mWorkspace != null) {
                    workspace = mWorkspace
                    BoardRequest.getBoardByWorkspaceId(workspace!!.id) { list ->
                        Log.d("HuyNv31", list.toString())
                        if (list != null) {
                            boardList = list
                        } else {
                            boardList = mutableListOf<Board>()
                        }
                        workspaceAdapter?.setDataWorkspace(boardList)
                        recyclerView?.adapter = workspaceAdapter

                    }
                }
            }
        }
    }

    /**
     * fun to get position of board to chane to boardFragment
     */
    private val listener = object : HomeAdapter.IOnClickItemBoard {
        override fun iOnClickItem(boardId: Int, boardName: String) {
            val bundle = Bundle()
            bundle.putInt(Constants.BOARD_ID, boardId)
            bundle.putString("Board_name", boardName)
            view?.findNavController()
                ?.navigate(R.id.action_workspaceFragment_to_boardFragment, bundle)
        }
    }

    /**
     * fun to binding fragment with layout
     */
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentWorkspaceBinding {
        return FragmentWorkspaceBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        (activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.workspaces_bar_title)
    }

    override fun initData() {
        recyclerView = view?.findViewById(R.id.recyclerView_workspace)
        layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        workspaceAdapter = WorkspaceAdapter(listener)
        getData()
    }
}
