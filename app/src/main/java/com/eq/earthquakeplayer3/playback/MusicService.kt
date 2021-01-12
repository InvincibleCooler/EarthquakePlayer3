package com.eq.earthquakeplayer3.playback

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.eq.earthquakeplayer3.ext.stateName
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Browsing begins with the method [MusicService.onGetRoot], and continues in
 * the callback [MusicService.onLoadChildren].
 * For more information on implementing a MediaBrowserService,
 * visit [https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html](https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice.html).
 */


class MusicService : MediaBrowserServiceCompat() {
    companion object {
        private const val TAG = "MusicService"
        private const val EMPTY_STRING = ""
    }

    /**
     * 클라이언트가 탐색 없이 MediaSession 에 연결하도록 허용하기 위해서 EMPTY_STRING 를 사용한다.
     * null을 반환하면 연결이 거부된다.
     * Clients can connect, but this BrowserRoot is an empty hierachy
     * so onLoadChildren returns nothing. This disables the ability to browse for content.
     */
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        Log.d(TAG, "onGetRoot")
        return BrowserRoot(EMPTY_STRING, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.d(TAG, "onLoadChildren")
        if (EMPTY_STRING == parentId) {
            result.sendResult(null)
            return
        }
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // media session
    private lateinit var player: EqPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private fun createPlayer(): EqPlayer {
        Log.d(TAG, "createPlayer")
        player = EqPlayer(this)
        return player
    }

    private fun createMediaSession(): MediaSessionCompat {
        Log.d(TAG, "createMediaSession")
        mediaSession = MediaSessionCompat(this, TAG).apply {
            isActive = true
        }
        sessionToken = mediaSession.sessionToken // sessionToken 을 넣어줘야 connect가 가능함
        return mediaSession
    }

    private fun createMediaController(): MediaControllerCompat {
        Log.d(TAG, "createMediaController")
        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(object : MediaControllerCompat.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                    mediaController.playbackState?.let { state ->
                        Log.d(TAG, "MediaControllerCallback onMetadataChanged state : ${state.stateName}")
                    }
                }

                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    state?.let { state ->
                        Log.d(TAG, "MediaControllerCallback onPlaybackStateChanged state : ${state.stateName}")
                    }
                }
            })
        }
        return mediaController
    }

    private fun createMediaSessionConnector(): MediaSessionConnector {
        Log.d(TAG, "createMediaSessionConnector")
        mediaSessionConnector = MediaSessionConnector(mediaSession).also {
            val playbackPreparer = EqPlaybackPreparer(player)

            it.setPlayer(player.getPlayer())
            it.setPlaybackPreparer(playbackPreparer)
            it.setQueueNavigator(EqQueueNavigator(mediaController))
        }
        return mediaSessionConnector
    }

    override fun onCreate() {
        super.onCreate()

        createPlayer()
        createMediaSession()
        createMediaController()
        createMediaSessionConnector()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession.run {
            isActive = false
            release()
        }
        // Cancel coroutines when the service is going away.
        serviceJob.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        /**
         * By stopping playback, the player will transition to [Player.STATE_IDLE]. This will
         * cause a state change in the MediaSession, and (most importantly) call
         * [MediaControllerCallback.onPlaybackStateChanged]. Because the playback state will
         * be reported as [PlaybackStateCompat.STATE_NONE], the service will first remove
         * itself as a foreground service, and will then call [stopSelf].
         */
        player.stop(true)
    }

}