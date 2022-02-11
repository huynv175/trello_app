package gst.trainingcourse.trelloapp.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.model.Board

class HomeAdapter(private val iOnClickItemBoard: IOnClickItemBoard) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvBoardName: TextView? = null
        var tvBoardTime: TextView? = null

        init {
            tvBoardName = view.findViewById(R.id.tv_board_name)
            tvBoardTime = view.findViewById(R.id.tv_board_time)
        }
    }

    private var boardList: MutableList<Board> = mutableListOf<Board>()

    /**
     * function to set data workspace list
     */
    fun setDataBoard(boardList: MutableList<Board>) {
        this.boardList.clear()
        this.boardList = boardList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.board_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board: Board = boardList[position]
        holder.tvBoardName?.text = board.name
        holder.tvBoardTime?.text = board.createdTime

        holder.itemView.setOnClickListener() {
            iOnClickItemBoard.iOnClickItem(boardList[position].id, boardList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

    interface IOnClickItemBoard {
        fun iOnClickItem(boardId: Int, boardName: String)
    }

}