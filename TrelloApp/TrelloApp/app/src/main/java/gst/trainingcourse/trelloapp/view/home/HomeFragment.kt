package gst.trainingcourse.trelloapp.view.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentHomeBinding
import gst.trainingcourse.trelloapp.model.Board
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.model.UserBoard
import gst.trainingcourse.trelloapp.request.BoardRequest
import gst.trainingcourse.trelloapp.request.UserBoardRequest
import gst.trainingcourse.trelloapp.utils.Constants
import gst.trainingcourse.trelloapp.utils.Constants.BOARD_ID
import gst.trainingcourse.trelloapp.utils.Constants.DATA_USER
import gst.trainingcourse.trelloapp.utils.Constants.MY_PREFS_NAME
import gst.trainingcourse.trelloapp.view.MainActivity


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var layoutManager: RecyclerView.LayoutManager? = null

    private var homeAdapter: HomeAdapter? = null

    private var recyclerView: RecyclerView? = null

    private var boardList = mutableListOf<Board>()

    //private var userBoardList = mutableListOf<UserBoard>()

    private var userInfo = MainActivity.user

    /**
     * get data of userBoard from firebase
     */
    private fun getDataUserBoard() {
        userInfo?.id?.let {
            UserBoardRequest.getBoardByUser(it) { mUserBoardList ->
                if (mUserBoardList != null) {

                    getDataBoard(mUserBoardList)
                }
            }
        }
    }



    /**
     * get data Board from firebase
     */
    private fun getDataBoard(userBoardList: MutableList<UserBoard>) {
        boardList.clear()
        for (index in userBoardList.indices) {
            BoardRequest.getBoardById(userBoardList[index].boardId) { board ->
                //Log.d("175", "${board?.workspaceId}")
                //Log.d("175", board.toString())
                if (board != null) {
                    boardList.add(board)
                }
                if (index == userBoardList.size - 1) {
                    Log.d("175", boardList.toString())
                    boardList.sortBy { it.id }
                    homeAdapter?.setDataBoard(boardList)
                    recyclerView?.adapter = homeAdapter
                }
            }
        }
    }

    private val listener = object : HomeAdapter.IOnClickItemBoard {
        override fun iOnClickItem(boardId: Int, boardName: String) {
            val bundle = Bundle()
            bundle.putInt(BOARD_ID, boardId)
            bundle.putString("Board_name", boardName)
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_boardFragment, bundle)
        }

    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        (activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.home_bar_title)
        //setUpNavDrawerInfo()
    }

    override fun initData() {
        getUserInfo()
        recyclerView = view?.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        homeAdapter = HomeAdapter(listener)
        getDataUserBoard()
    }

    /**
     * get infor user when it is no data is device
     */
    private fun getUserInfo() {
        if (userInfo == null) {
            userInfo = arguments?.getSerializable(DATA_USER) as User?
            setUpNavDrawerInfo()
        }
        setUpNavDrawerInfo()
    }

    override fun initAction() {
        val bundle = Bundle()
        bundle.putInt("userId", userInfo?.id!!)
        binding.apply {
            btnAddBoard.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_homeFragment_to_addBoardFragment, bundle)
            }
        }
    }

    /**
     * set drawerInfor for user
     */
    private fun setUpNavDrawerInfo() {
        val headerView = activity?.findViewById<NavigationView>(R.id.navView)?.getHeaderView(0)
        val avatar = headerView?.findViewById<CircleImageView>(R.id.im_avatar)
        val tvName = headerView?.findViewById<TextView>(R.id.tv_name)
        val tvUserName = headerView?.findViewById<TextView>(R.id.tv_user_name)
        val email = headerView?.findViewById<TextView>(R.id.tv_email)
        if (userInfo != null) {
            loadAlreadyUserInfo(avatar, tvName, tvUserName, email)
        }
        handleLogout()
    }

    /**
     * fun to logout
     */
    private fun handleLogout() {
        val navView = activity?.findViewById<NavigationView>(R.id.navView)
        navView?.menu?.findItem(R.id.sign_out)?.setOnMenuItemClickListener {
            if (it.itemId == R.id.sign_out) {
                val alertDialog = context?.let { it1 -> AlertDialog.Builder(it1) }
                alertDialog?.setTitle("Sign out")?.setMessage("Do you want to sign out?")
                    ?.setPositiveButton("SIGN OUT") { dialog, id ->
                        handleRemoveUser()
                        view?.findNavController()
                            ?.navigate(R.id.action_homeFragment_to_introFragment)
                        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                        drawerLayout?.closeDrawers()
                    }?.setNegativeButton("CANCEL") { dialog, id -> dialog.dismiss() }
                    ?.setCancelable(false)?.show()
            }
            true
        }
    }

    /**
     * fun to clear data after logout
     */
    private fun handleRemoveUser() {
        MainActivity.user = null
        val context = context as MainActivity
        val editor =
            context.getSharedPreferences(Constants.MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
                .edit()
        editor.clear()
        editor.apply()
    }

    /**
     * set data for user on drawerInfor
     */
    private fun loadAlreadyUserInfo(
        avatar: CircleImageView?,
        tvName: TextView?,
        tvUserName: TextView?,
        email: TextView?
    ) {
        if (userInfo?.avatar?.isEmpty() == true) {
            context?.let {
                if (avatar != null) {
                    Glide.with(it).load(R.drawable.ic_user_avatar)
                        .into(avatar)
                }
            }
        } else {
            context?.let {
                if (avatar != null) {
                    Glide.with(it).load(userInfo?.avatar?.toUri()).error(R.drawable.ic_user_avatar)
                        .into(avatar)
                }
            }
        }
        tvName?.text = userInfo?.name
        tvUserName?.text = userInfo?.accountName
        email?.text = userInfo?.email
    }


}

