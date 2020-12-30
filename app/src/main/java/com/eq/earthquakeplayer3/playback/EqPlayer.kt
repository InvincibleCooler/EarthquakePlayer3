package com.eq.earthquakeplayer3.playback

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util


class EqPlayer(private val context: Context) {
    companion object {
        private const val TAG = "EqPlayer"
    }

    interface Callback {
        fun onCompletion()
        fun onPlaybackStateChanged(playWhenReady: Boolean, state: Int)
        fun onError(error: String)
    }

    var callback: Callback? = null

    private val exoPlayer: SimpleExoPlayer by lazy {
        /**
         * default track selector is AdaptiveTrackSelection :
         * A bandwidth based adaptive TrackSelection, whose selected track is updated to be the one of highest quality given the current network conditions and the state of the buffer.
         */
        SimpleExoPlayer.Builder(context).build().apply {
            addListener(eventListener)
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlayerError(error: ExoPlaybackException) {
            val errorMsg = when (error.type) {
                ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message ?: ""
                ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message ?: ""
                ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message ?: ""
                ExoPlaybackException.TYPE_REMOTE, ExoPlaybackException.TYPE_OUT_OF_MEMORY -> error.message ?: ""
                else -> "Unknown: $error"
            }

            Log.e(TAG, "onPlayerError error msg : $errorMsg")
            callback?.onError(errorMsg)
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE, Player.STATE_BUFFERING, Player.STATE_READY -> {
                    callback?.onPlaybackStateChanged(playWhenReady, playbackState)
                }
                Player.STATE_ENDED -> {
                    callback?.onCompletion()
                }
            }
        }
    }

    fun getPlayer(): SimpleExoPlayer {
        return exoPlayer
    }

    fun isPlaying(): Boolean {
        return exoPlayer.playWhenReady
    }

    fun setDisplay(sh: SurfaceHolder?) {
        exoPlayer.setVideoSurfaceHolder(sh)
    }

    // Util stuff function
    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    fun getDuration(): Long {
        return exoPlayer.duration
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun setVolume(audioVolume: Float) {
        exoPlayer.volume = audioVolume
    }

    fun next() {
        if (exoPlayer.hasNext()) {
            exoPlayer.next()
        }
    }

    fun previous() {
        if (exoPlayer.hasPrevious()) {
            exoPlayer.previous()
        }
    }

    fun start() {
        exoPlayer.playWhenReady = true
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun stop(reset: Boolean) {
        exoPlayer.stop(reset)
    }

    fun release() {
        exoPlayer.release()
        exoPlayer.removeListener(eventListener)
        // should be null outside of the player
    }

    fun setDataSource(uri: Uri, isRepeat: Boolean) {
        val mediaSource = buildMediaSource(uri)
        if (mediaSource != null) {
            Log.d(TAG, "mediaSource is ready isRepeat : $isRepeat")
            if (isRepeat) {
                exoPlayer.prepare(LoopingMediaSource(mediaSource))
            } else {
                exoPlayer.prepare(mediaSource)
            }
        } else {
            Log.e(TAG, "MediaSource is not available.")
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        return when (Util.inferContentType(uri)) {
            C.TYPE_HLS -> {
                HlsMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri)
            }
            C.TYPE_OTHER -> {
                ProgressiveMediaSource.Factory(buildDataSourceFactoryWithCache()).createMediaSource(uri)
            }
            else -> {
                Log.d(TAG, "DASH, SS are not supported.")
                null
            }
        }
    }

    private fun buildDataSourceFactory() =
        DefaultDataSourceFactory(context, Util.getUserAgent(context, "Earthquake"))

    private fun buildDataSourceFactoryWithCache() =
        CacheDataSourceFactory(
            ExoVideoCache.getInstance(context),
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "Earthquake"))
        )

    // for debug
    fun stateName(state: Int): String {
        return when (state) {
            Player.STATE_IDLE -> {
                "STATE_IDLE"
            }
            Player.STATE_BUFFERING -> {
                "STATE_BUFFERING"
            }
            Player.STATE_READY -> {
                "STATE_READY"
            }
            Player.STATE_ENDED -> {
                "STATE_ENDED"
            }
            else -> {
                "illegal state"
            }
        }
    }
}