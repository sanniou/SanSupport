package com.sanniou.support.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        viewModel = createViewModel()
        binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, getLayoutRes(), container, false)
                .apply {
                    lifecycleOwner = provideLifecycleOwner()
                    setVariable(getModelId(), viewModel)
                    executePendingBindings()
                    onBindingCreated(this)
                }
        return binding.root
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}