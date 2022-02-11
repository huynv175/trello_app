package gst.trainingcourse.trelloapp.view.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentIntroBinding
import gst.trainingcourse.trelloapp.view.MainActivity

/**
 * A simple [BaseFragment] subclass.
 * Use the [IntroFragment] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : BaseFragment<FragmentIntroBinding>(), View.OnClickListener {

    /**
     * override fun createBinding from Base class.
     * @param inflater[LayoutInflater]
     * @param container[ViewGroup]
     * @param savedInstanceState[Bundle]
     * @return FragmentIntroBinding
     */
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentIntroBinding {
        return FragmentIntroBinding.inflate(inflater, container, false)
    }

    /**
     * override fun initView from Base class.
     */
    override fun initView() {
        // Hide Action Bar
        (activity as MainActivity).supportActionBar?.hide()
    }

    /**
     * override fun initAction from Base class.
     */
    override fun initAction() {
        binding.apply {
            btnSignUp.setOnClickListener(this@IntroFragment)
            btnLogin.setOnClickListener(this@IntroFragment)
        }
    }

    /**
     * override fun onClick
     * listening on click sign up button and login button.
     * @param v[View]
     */
    override fun onClick(v: View?) {
        when (v) {
            // navigate from intro screen to sign up screen
            binding.btnSignUp ->view?.findNavController()
                ?.navigate(R.id.action_introFragment_to_signUpFragment)
            // navigate from intro screen to sign up screen
            binding.btnLogin -> view?.findNavController()
                ?.navigate(R.id.action_introFragment_to_loginFragment)
        }
    }
}