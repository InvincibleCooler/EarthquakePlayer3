package com.eq.earthquakeplayer3.playback

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData


class MusicServiceConnection(context: Context, serviceComponent: ComponentName) {
    companion object {
        private const val TAG = "MusicServiceConnection"

        // For Singleton instantiation.
        @Volatile
        private var instance: MusicServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: MusicServiceConnection(context, serviceComponent).also {
                    instance = it
                }
            }
    }

    val isConnected = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
    val playbackState = MutableLiveData<PlaybackStateCompat>().apply {
        postValue(PLAYBACK_STATE_NONE)
    }

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(context, serviceComponent, mediaBrowserConnectionCallback, null).apply {
        Log.d(TAG, "MediaBrowserCompat connect")
        connect()
    }
    private lateinit var mediaController: MediaControllerCompat
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private inner class MediaBrowserConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {
        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            Log.d(TAG, "onConnected")
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            isConnected.postValue(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            Log.d(TAG, "onConnectionSuspended")
            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            Log.d(TAG, "onConnectionFailed")
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "onPlaybackStateChanged state : $state")
            playbackState.postValue(state ?: PLAYBACK_STATE_NONE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.d(TAG, "onMetadataChanged metadata : $metadata")
        }

        override fun onSessionDestroyed() {
            Log.d(TAG, "onSessionDestroyed")
        }
    }

    @Suppress("PropertyName")
    val PLAYBACK_STATE_NONE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()
}