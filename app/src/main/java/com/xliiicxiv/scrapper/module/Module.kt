package com.xliiicxiv.scrapper.module

import com.xliiicxiv.scrapper.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object Module {

    private val viewModel = module {
        viewModelOf(::LoginViewModel)
    }

    private val repository = module {
    }

    fun getModule() = listOf(
        viewModel,
        repository
    )

}