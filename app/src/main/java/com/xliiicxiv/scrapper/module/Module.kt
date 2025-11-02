package com.xliiicxiv.scrapper.module

import com.xliiicxiv.scrapper.viewmodel.LoginViewModel
import com.xliiicxiv.scrapper.viewmodel.SiipBpjsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object Module {

    private val viewModel = module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::SiipBpjsViewModel)
    }

    private val repository = module {
    }

    fun getModule() = listOf(
        viewModel,
        repository
    )

}