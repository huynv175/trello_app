package gst.trainingcourse.trelloapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException

abstract class BaseFragment<out T : ViewBinding> : Fragment() {

    private var _binding: T? = null

    /**
     * Available after [Fragment.onCreateView] returns
     * and before [Fragment.onDestroyView]'s super is called
     */
    protected val binding: T
        get() = _binding ?: throw IllegalStateException(
            "binding is only valid between onCreateView and onDestroyView"
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    /**
     * Called in [Fragment.onCreateView] to get the rootView of [binding]
     */
    abstract fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): T

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        initView()
        initData()
        initAction()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * open function initView is not required to override in a subclass.
     * using to initialize view.
     */
    open fun initView() = Unit

    /**
     * open function initData is not required to override in a subclass.
     * use to initialize data.
     */
    open fun initData() = Unit

    /**
     * open function initAction is not required to override in a subclass.
     * use to initialize action
     */
    open fun initAction() = Unit

    /**
     * open function observeLiveData is not required to override in a subclass.
     * use to observe live data.
     */
    open fun observeLiveData() = Unit
}