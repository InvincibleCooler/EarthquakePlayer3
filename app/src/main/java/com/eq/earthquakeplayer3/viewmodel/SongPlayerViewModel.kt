package com.eq.earthquakeplayer3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.playback.MusicServiceConnection


class SongPlayerViewModel(app: Application, musicServiceConnection: MusicServiceConnection) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "SongPlayerViewModel"
    }

    val songData = MutableLiveData<SongData>().apply {
        value = null
    }

    class Factory(
        private val app: Application,
        private val musicServiceConnection: MusicServiceConnection
    ) : ViewModelProvider.AndroidViewModelFactory(app) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SongPlayerViewModel(app, musicServiceConnection) as T
        }
    }
}