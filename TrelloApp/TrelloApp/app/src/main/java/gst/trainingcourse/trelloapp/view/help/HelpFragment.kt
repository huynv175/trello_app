package gst.trainingcourse.trelloapp.view.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import gst.trainingcourse.trelloapp.R
import gst.trainingcourse.trelloapp.base.BaseFragment
import gst.trainingcourse.trelloapp.databinding.FragmentHelpBinding
import gst.trainingcourse.trelloapp.databinding.FragmentHomeBinding


class HelpFragment : BaseFragment<FragmentHelpBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHelpBinding {
        return FragmentHelpBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        (context as AppCompatActivity).supportActionBar?.title = "Help"
    }


}