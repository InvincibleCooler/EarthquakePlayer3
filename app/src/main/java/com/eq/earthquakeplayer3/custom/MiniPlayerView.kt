package com.eq.earthquakeplayer3.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.eq.earthquakeplayer3.R


class MiniPlayerView(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {
    companion object {
        const val TAG = "MiniPlayerView"
    }

    interface Callback {
        fun onClickPrevious()
        fun onClickPlay()
        fun onClickNext()
    }

    var listener: Callback? = null

    private lateinit var titleTv: TextView
    private lateinit var artistTv: TextView
    private lateinit var btnPrevIv: ImageView
    private lateinit var btnPlayIv: ImageView
    private lateinit var btnNextIv: ImageView

    init {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.miniplayer_laytout, this, true)

        titleTv = findViewById(R.id.title_tv)
        artistTv = findViewById(R.id.artist_tv)
        btnPrevIv = findViewById(R.id.previous_iv)
        btnPlayIv = findViewById(R.id.play_iv)
        btnNextIv = findViewById(R.id.next_iv)

        btnPrevIv.setOnClickListener {
            listener?.onClickPrevious()
        }
        btnPlayIv.setOnClickListener {
            listener?.onClickPlay()
        }
        btnNextIv.setOnClickListener {
            listener?.onClickNext()
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