package com.sanniou.support.components

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

abstract class BaseViewModelActivity<T : ViewModel> : AppCompatActivity(), ViewModelOwner<T> {

    /**
     * This field contains the activity's [ViewModel] after this activity has been created.
     * The access to this field is read-only.
     */
    lateinit var viewModel: T
        private set

    protected lateinit var binding: ViewDataBinding
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        binding = DataBindingUtil.setContentView<ViewDataBinding>(this, getLayoutRes())
            .apply {
                setVariable(getModelId(), viewModel)
                lifecycleOwner = provideLifecycleOwner()
                onBindingCreated(this)
            }
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}