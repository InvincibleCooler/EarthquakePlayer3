package com.eq.earthquakeplayer3.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.data.SongDataMgr
import com.eq.earthquakeplayer3.ext.isPlayEnabled
import com.eq.earthquakeplayer3.ext.isPlaying
import com.eq.earthquakeplayer3.ext.isPrepared
import com.eq.earthquakeplayer3.playback.MusicServiceConnection


class SongPlayerViewModel(app: Application, val musicServiceConnection: MusicServiceConnection) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "SongPlayerViewModel"
    }

    val songData = MutableLiveData<SongData>().apply {
        value = null
    }

    fun playMedia(uriPath: String?) {
        val uri = if (!uriPath.isNullOrEmpty()) Uri.parse(uriPath) else null
        val transportControls = musicServiceConnection.transportControls
        val playbackState = musicServiceConnection.playbackState.value
        val currentPlayedSong = SongDataMgr.getCurrentSongData()?.data

        val isPrepared = playbackState?.isPrepared ?: false
        if (isPrepared && uriPath == currentPlayedSong) {
            playbackState?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPlayEnabled -> transportControls.play()
                    else -> Log.d(TAG, "Playable item clicked but neither play nor pause are enabled!")
                }
            }
        } else {
            transportControls.playFromUri(uri, null)
        }
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        musicServiceConnection.transportControls.skipToNext()
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