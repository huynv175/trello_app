package gst.trainingcourse.trelloapp.view.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentLoginBinding
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.request.WorkspaceRequest
import gst.trainingcourse.trelloapp.utils.Constants
import gst.trainingcourse.trelloapp.utils.Constants.DATA_USER
import gst.trainingcourse.trelloapp.utils.Constants.EMPTY_STRING
import gst.trainingcourse.trelloapp.utils.ResultMode
import gst.trainingcourse.trelloapp.view.MainActivity
import gst.trainingcourse.trelloapp.viewmodel.login.ValidateViewModel

/**
 * A simple [BaseFragment] subclass.
 * Use the [LoginFragment] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment<FragmentLoginBinding>(), View.OnClickListener {

    private val validateViewModel: ValidateViewModel by viewModels()
    private var userId: Int = 0

    /**
     * override fun createBinding from Base class.
     * @param inflater[LayoutInflater]
     * @param container[ViewGroup]
     * @param savedInstanceState[Bundle]
     * @return FragmentLoginBinding
     */
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    /**
     * override fun initView from Base class.
     */
    override fun initView() {
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.login_action_bar_title)
    }

    /**
     * override fun initAction from Base class.
     */
    override fun initAction() {
        binding.apply {
            btnLoginPage.setOnClickListener(this@LoginFragment)
            imvActionBarLogin.setOnClickListener(this@LoginFragment)
        }
    }

    /**
     * handle action click
     * @param v[View]]
     */
    override fun onClick(v: View?) {
        when (v) {
            binding.imvActionBarLogin -> handNavigateBack()
            binding.btnLoginPage -> handleLogin()
        }
    }

    /**
     * navigate back while pressing up icon on action bar.
     */
    private fun handNavigateBack() {
        view?.findNavController()?.popBackStack()
    }

    /**
     * override fun observeLiveData from Base class.
     */
    override fun observeLiveData() {
        //observe email and password is valid?
        validateViewModel.isValidInput.observe(viewLifecycleOwner) { resultMode ->
            if (resultMode == ResultMode.VALID) {
                validateViewModel.checkLogin()
            } else {
                val title = getString(R.string.title_invalid_acc_or_pass)
                val message = getString(R.string.message_invalid_acc_or_pass)
                showResultDiaLog(title, message)
            }
        }
        //observe login success or failed
        validateViewModel.resultLogin.observe(viewLifecycleOwner) { resultMode ->
            if (resultMode == ResultMode.SUCCESS) {
                UserRequest.getUserById(userId) { user ->
                    val bundle = Bundle()
                    bundle.putSerializable(DATA_USER, user)
                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment, bundle)
                }
            } else {
                val title = getString(R.string.title_login_failed)
                val message = getString(R.string.message_login_failed)
                showResultDiaLog(title, message)
            }
        }

        validateViewModel.userId.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("HuyNV", user.toString())
                userId = user.id!!
                handleSaveUserId(user)
            }
        }
    }


    private fun handleSaveUserId(user: User ) {
        MainActivity.user = user
        val context = context as MainActivity
        val editor = context.getSharedPreferences(Constants.MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit()
        editor.putInt("userId", user.id!!)
        editor.apply()

    }

    private fun handleLogin() {
        binding.apply {
            val email = edEmailLogin.text?.trim().toString()
            val password = edPasswordLogin.text?.trim().toString()
            if (TextUtils.isEmpty(email)) {
                edEmailLogin.error = getString(R.string.edit_text_error)
            }
            if (TextUtils.isEmpty(password)) {
                edPasswordLogin.error = getString(R.string.edit_text_error)
            }
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                checkAccountLogin(email, password)
            }
        }
    }

    private fun checkAccountLogin(email: String, password: String) {
            val user = User(null, EMPTY_STRING, password, EMPTY_STRING, EMPTY_STRING, email)
            // check email and password valid?
            validateViewModel.checkInput(user)
    }

    private fun showResultDiaLog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title).setMessage(message)
            .setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

}