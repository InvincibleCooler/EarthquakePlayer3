package com.eq.earthquakeplayer3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eq.earthquakeplayer3.EqApp


class SongListViewModelFactory(private val app: EqApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SongListViewModel(app) as T
    }
}