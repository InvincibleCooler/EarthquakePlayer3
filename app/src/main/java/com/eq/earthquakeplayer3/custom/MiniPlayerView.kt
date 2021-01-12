package com.eq.earthquakeplayer3.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.eq.earthquakeplayer3.R


class MiniPlayerView : LinearLayout {
    companion object {
        const val TAG = "MiniPlayerView"
    }

    interface Callback {
        fun onPreviousClick()
        fun onPlayClick()
        fun onNextClick()
    }

    var listener: Callback? = null

    private lateinit var titleTv: TextView
    private lateinit var artistTv: TextView
    private lateinit var btnPrevIv: ImageView
    private lateinit var btnPlayIv: ImageView
    private lateinit var btnNextIv: ImageView

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.miniplayer_laytout, this, true)

        titleTv = view.findViewById(R.id.title_tv)
        artistTv = view.findViewById(R.id.artist_tv)
        btnPrevIv = view.findViewById(R.id.btn_mini_prev)
        btnPlayIv = view.findViewById(R.id.btn_mini_play)
        btnNextIv = view.findViewById(R.id.btn_mini_next)

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
        btnPlayIv.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }

    fun setSongName(songName: String) {
        titleTv.text = songName
    }

    fun setArtistName(artistName: String) {
        artistTv.text = artistName
    }
}