package com.xliiicxiv.scrapper.module

import com.xliiicxiv.scrapper.viewmodel.DptViewModel
import com.xliiicxiv.scrapper.viewmodel.LasikViewModel
import com.xliiicxiv.scrapper.viewmodel.LoginViewModel
import com.xliiicxiv.scrapper.viewmodel.SiipBpjsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object Module {

    private val viewModel = module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::SiipBpjsViewModel)
        viewModelOf(::DptViewModel)
        viewModelOf(::LasikViewModel)
    }

    private val repository = module {
    }

    fun getModule() = listOf(
        viewModel,
        repository
    )

}