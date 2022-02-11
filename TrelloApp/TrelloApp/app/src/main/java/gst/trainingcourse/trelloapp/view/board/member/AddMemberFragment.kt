package gst.trainingcourse.trelloapp.view.board.member


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentAddMemberBinding
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.model.UserBoard
import gst.trainingcourse.trelloapp.request.UserBoardRequest
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.utils.Constants.BOARD_ID
import gst.trainingcourse.trelloapp.view.MainActivity


class AddMemberFragment : BaseFragment<FragmentAddMemberBinding>() {

    private var etAddMember: EditText? = null

    private var tvCheckmember: TextView? = null

    private var boardId = 0

    private var member: User? = null



    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAddMemberBinding {
        return FragmentAddMemberBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        //show action bar
        (activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title = "Add Member"
        etAddMember = view?.findViewById(R.id.et_member_email)
        tvCheckmember = view?.findViewById(R.id.tv_check_member)

    }

    override fun initData() {
        boardId = arguments?.getInt(BOARD_ID) ?: 0
    }
        /**
     * override fun initAction from Base class.
     */
    override fun initAction() {
        binding.btnAddMember.setOnClickListener {
            getDataMember()
        }
    }

    private fun addMember() {
        Log.d("Huy", "On addMenmber")
        var memberId: Int? =  member?.id
        Log.d("Huy", memberId.toString())
        if (memberId != null) {
            UserBoardRequest.createUserBoard(UserBoard(0, memberId, boardId)){ isAdded ->
                view?.findNavController()?.popBackStack()
            }
        }
    }

    private fun getDataMember() {
        Log.d("Huy", "on getDataMember")
        if (etAddMember?.text.isNullOrEmpty()) {
            Log.d("Huy", "text is fail")
            tvCheckmember?.text = "Nhập email"
        } else {
            tvCheckmember?.text = ""
            val email = etAddMember?.text?.trim().toString()
            Log.d("Huy", "${email}")
            UserRequest.getUserByEmail(email) { user ->
                if (user != null) {
                    Log.d("Huy", "${user.email}")
                    member = user
                    addMember()
                } else {
                    Log.d("Huy", "email fail")
                    tvCheckmember?.setText("Sai email")
                }
            }
        }
    }
}