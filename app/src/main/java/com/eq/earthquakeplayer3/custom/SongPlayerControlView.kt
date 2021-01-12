package com.eq.earthquakeplayer3.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.eq.earthquakeplayer3.R


class SongPlayerControlView : LinearLayout {
    companion object {
        const val TAG = "SongPlayerControlView"
    }

    interface Callback {
        fun onPreviousClick()
        fun onPlayClick()
        fun onNextClick()
    }

    var listener: Callback? = null

    private lateinit var btnPrevIv: ImageView
    private lateinit var btnPlayIv: ImageView
    private lateinit var btnNextIv: ImageView

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.song_player_control_view, this, true)

        btnPrevIv = view.findViewById(R.id.previous_iv)
        btnPlayIv = view.findViewById(R.id.play_iv)
        btnNextIv = view.findViewById(R.id.next_iv)

        btnPrevIv.setOnClickListener {
            listener?.onPreviousClick()
        }
        btnPlayIv.setOnClickListener {
            listener?.onPlayClick()
        }
        btnNextIv.setOnClickListener {
            listener?.onNextClick()
        }
    }

    fun updatePlayButton(isPlaying: Boolean) {
        btnPlayIv.setImageResource(if (isPlaying) R.drawable.player_pause else R.drawable.player_play)
    }
}