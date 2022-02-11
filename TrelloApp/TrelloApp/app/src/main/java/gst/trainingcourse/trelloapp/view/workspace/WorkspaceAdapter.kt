package gst.trainingcourse.trelloapp.view.workspace

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.Workspace
import gst.trainingcourse.trelloapp.view.home.HomeAdapter

class WorkspaceAdapter(private val iOnClickItemBoard: HomeAdapter.IOnClickItemBoard) : RecyclerView.Adapter<WorkspaceAdapter.WorkspaceHolder>() {
    /**
     * class Workspace holder
     */
    inner class WorkspaceHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvWorkspaceName: TextView? = null
        var tvWorkspaceTime: TextView? = null

        init {
            tvWorkspaceName = view.findViewById(R.id.tv_workspace_name)
            tvWorkspaceTime = view.findViewById(R.id.tv_workspace_time)
            view.setOnClickListener {
            }
        }
    }

    private var boardList= mutableListOf<Board>()

    /**
     * function to set data workspace list
     */
    fun setDataWorkspace(boardList: MutableList<Board>) {
        this.boardList.clear()
        this.boardList = boardList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.workspace_card, parent, false)
        return WorkspaceHolder(v)
    }

    override fun onBindViewHolder(holder: WorkspaceHolder, position: Int) {
        val board: Board = boardList[position]
        Log.d("HuyNV31", "${board.name}")
        holder.tvWorkspaceName?.text = board.name
        holder.tvWorkspaceTime?.text = board.createdTime
        holder.itemView.setOnClickListener() {
            iOnClickItemBoard.iOnClickItem(boardList[position].id, boardList[position].name)
        }

    }

    override fun getItemCount(): Int {
        return boardList.size
    }


}