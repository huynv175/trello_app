package gst.trainingcourse.trelloapp.view.board.member


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.model.UserBoard
import gst.trainingcourse.trelloapp.model.Workspace

class MemberAdapter() : RecyclerView.Adapter<MemberAdapter.MemberHolder>() {


    inner class MemberHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvMemberName: TextView? = null
        var tvMemberMail: TextView? = null

        init {
            tvMemberName = view.findViewById(R.id.tv_member_name)
            tvMemberMail = view.findViewById(R.id.tv_member_mail)
            view.setOnClickListener {
            }
        }
    }

    private var memberList: MutableList<User> = mutableListOf<User>()

    /**
     * function to set data workspace list
     */
    fun setDataBoard(memberList: MutableList<User>) {
        this.memberList.clear()
        this.memberList = memberList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.member_card, parent, false)
        return MemberHolder(v)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        val member: User = memberList[position]
        holder.tvMemberName?.text = member.name
        holder.tvMemberMail?.text = member.email

    }

    override fun getItemCount(): Int {
        return memberList.size
    }


}