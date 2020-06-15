package com.sanniou.support.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * Basic fragment that provides usage for [ViewModel].
 */
abstract class BaseViewModelFragment<T : ViewModel> : Fragment(), ViewModelOwner<T> {

    /**
     * This field contains the activity's [ViewModel] after this activity has been created.
     * The access to this field is read-only.
     */
    lateinit var viewModel: T
        private set

    protected lateinit var binding: ViewDataBinding
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = if (::viewModel.isInitialized) viewModel else createViewModel()
        binding = if (::binding.isInitialized) binding else
            DataBindingUtil.inflate<ViewDataBinding>(inflater, getLayoutRes(), container, false)
                .apply {
                    onBindingCreated(this)
                }
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBinding()
        super.onViewCreated(view, savedInstanceState)
    }

    @CallSuper
    protected open fun initBinding(): Boolean {
        binding.lifecycleOwner = provideLifecycleOwner()
        return binding.setVariable(getModelId(), viewModel)
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}