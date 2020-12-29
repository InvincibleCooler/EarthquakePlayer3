package com.eq.earthquakeplayer3.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eq.earthquakeplayer3.data.SongData


class SongPlayerViewModel : ViewModel() {
    companion object {
        private const val TAG = "SongPlayerViewModel"
    }

    val songData = MutableLiveData<SongData>().apply {
        value = null
    }
}