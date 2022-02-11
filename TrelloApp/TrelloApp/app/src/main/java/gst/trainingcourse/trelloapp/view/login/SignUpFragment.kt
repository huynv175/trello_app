package gst.trainingcourse.trelloapp.view.login

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentSignUpBinding
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.model.Workspace
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.request.WorkspaceRequest
import gst.trainingcourse.trelloapp.utils.Constants
import gst.trainingcourse.trelloapp.utils.Constants.EMPTY_STRING
import gst.trainingcourse.trelloapp.utils.Constants.PERMISSION_CODE
import gst.trainingcourse.trelloapp.utils.ResultMode
import gst.trainingcourse.trelloapp.view.MainActivity
import gst.trainingcourse.trelloapp.viewmodel.login.ValidateViewModel


/**
 * A simple [BaseFragment] subclass.
 * Use the [SignUpFragment] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(), View.OnClickListener {

    private val validateViewModel: ValidateViewModel by viewModels()
    private var avatar: String = EMPTY_STRING
    private var userId: Int = 0


    /**
     * override fun createBinding from Base class.
     * @param inflater[LayoutInflater]
     * @param container[ViewGroup]
     * @param savedInstanceState[Bundle]
     * @return FragmentSignUpBinding
     */
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }

    /**
     * override fun initView from Base class.
     */
    override fun initView() {
        //show action bar
        //(activity as MainActivity).supportActionBar?.show()
        // Set title action bar
        (context as AppCompatActivity).supportActionBar?.title =
            getString(R.string.sign_up_action_bar_title)
    }

    /**
     * override fun initAction from Base class.
     */
    override fun initAction() {
        binding.apply {
            imvActionBarSignUp.setOnClickListener(this@SignUpFragment)
            btnSignUpPage.setOnClickListener(this@SignUpFragment)
            myProfileUserImage.setOnClickListener(this@SignUpFragment)
        }
    }

    /**
     * handle action click
     * @param v[View]]
     */
    override fun onClick(v: View?) {
        when (v) {
            binding.imvActionBarSignUp -> handNavigateBack()
            binding.myProfileUserImage -> handleChangeAvatar()
            binding.btnSignUpPage -> handleSignUp()
        }
    }

    /**
     * override fun observeLiveData from Base class.
     */
    override fun observeLiveData() {
        //observe email and password is valid?
        validateViewModel.isValidInput.observe(viewLifecycleOwner) { resultMode ->
            if (resultMode == ResultMode.VALID) {
                binding.prbLoading.visibility = View.VISIBLE
                binding.prbLoading.showContextMenu()
            } else {
                val title = getString(R.string.title_invalid_acc_or_pass)
                val message = getString(R.string.message_invalid_acc_or_pass)
                showResultDiaLog(title, message)
            }
        }
        //observe user exists or -> failed unless sign up success
        validateViewModel.resultSignUp.observe(viewLifecycleOwner) { resultMode ->
            if (resultMode == ResultMode.SUCCESS) {
                UserRequest.getUserById(userId) { user ->
                    WorkspaceRequest.createWorkspace(
                        Workspace(
                            0,
                            userId,
                            "Không gian làm việc của ${user?.name}",
                            Constants.getCurrentDate()
                        )
                    ) {
                        if(!isAdded) {
                            Log.d("Test", "WorkSpace added failed")
                        }
                    }
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.DATA_USER, user)
                    view?.findNavController()
                        ?.navigate(R.id.action_signUpFragment_to_homeFragment, bundle)
                }
            } else {
                binding.prbLoading.visibility = View.GONE
                val title = getString(R.string.title_sign_up_failed)
                val message = getString(R.string.message_sign_up_failed)
                showResultDiaLog(title, message)
            }
        }

        validateViewModel.userId.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                userId = user.id!!
                handleSaveUserId(user)
            }
        }
    }

    private fun handleSaveUserId(user: User) {
        MainActivity.user = user
        val context = context as MainActivity
        val editor = context.getSharedPreferences(Constants.MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit()
        editor.putInt("userId", user.id!!)
        editor.apply()
    }

    /**
     * navigate back while pressing up icon on action bar.
     */
    private fun handNavigateBack() {
        view?.findNavController()?.popBackStack()
    }

    private fun handleChangeAvatar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_DENIED) {
                //permission denied
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ///show popup to request runtime permission
                @Suppress("DEPRECATION")
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                pickImageFromGallery()
            }
        } else {
            // version < M
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        @Suppress("DEPRECATION")
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Picture"
            ), PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    pickImageFromGallery()
                } else {
                    //permission was denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PERMISSION_CODE) {
            val selectedImageUri: Uri? = data?.data
            avatar = selectedImageUri.toString()
            context?.let { Glide.with(it).load(selectedImageUri).into(binding.myProfileUserImage) }
        }
    }


    /**
     * handle sign up case.
     */
    private fun handleSignUp() {
        binding.apply {
            val user = initUserInfo()
            //set error edit text empty
            if (TextUtils.isEmpty(user.accountName) || TextUtils.isEmpty(user.password)
                || TextUtils.isEmpty(user.name) || TextUtils.isEmpty(user.email)
            ) {
                // if user info is empty, create a dialog to notify
                val title = getString(R.string.title_error_empty_input)
                val message = getString(R.string.message_empty_input)
                showResultDiaLog(title, message)
            } else {
                // check email and password valid?
                validateViewModel.checkInput(user)
            }

        }
    }

    /**
     * get user info from user's input.
     */
    private fun initUserInfo(): User {
        binding.apply {
            val accountName = edAccountNameSignUp.text?.trim().toString()
            val password = edPasswordSignIn.text?.trim().toString()
            val name = edNameSignUp.text?.trim().toString()
            val email = edEmailSignUp.text?.trim().toString()
            return User(null, accountName, password, name, avatar, email)
        }
    }

    /**
     * display dialogs corresponding to login states.
     * @param title[String]
     * @param message[String]
     */
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