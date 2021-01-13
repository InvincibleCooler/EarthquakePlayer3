package com.eq.earthquakeplayer3.playback

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

/**
 * ACTION_SKIP_TO_PREVIOUS 와 ACTION_SKIP_TO_NEXT 처리를 위해서 사용함
 */
class EqQueueNavigator(private val mediaController: MediaControllerCompat) : MediaSessionConnector.QueueNavigator {
    companion object {
        private const val TAG = "EqQueueNavigator"
    }

    override fun onSkipToQueueItem(player: Player, controlDispatcher: ControlDispatcher, id: Long) {
    }

    override fun onCurrentWindowIndexChanged(player: Player) {
    }

    override fun onCommand(player: Player, controlDispatcher: ControlDispatcher, command: String, extras: Bundle, cb: ResultReceiver) = false

    override fun getSupportedQueueNavigatorActions(player: Player) =
        (PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT)

    override fun getActiveQueueItemId(player: Player?) = MediaSessionCompat.QueueItem.UNKNOWN_ID.toLong()

    override fun onTimelineChanged(player: Player) {
    }

    override fun onSkipToPrevious(player: Player, controlDispatcher: ControlDispatcher) {
        Log.d(TAG, "onSkipToPrevious")
    }

    override fun onSkipToNext(player: Player, controlDispatcher: ControlDispatcher) {
        Log.d(TAG, "onSkipToNext")
    }
}