package com.eq.earthquakeplayer3.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
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

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.miniplayer_laytout, this, true)

        titleTv = view.findViewById(R.id.title_tv)
        artistTv = view.findViewById(R.id.artist_tv)
        btnPrevIv = view.findViewById(R.id.previous_iv)
        btnPlayIv = view.findViewById(R.id.play_iv)
        btnNextIv = view.findViewById(R.id.next_iv)

        btnPrevIv.setOnClickListener {
            Log.d(TAG, "onClickPrevious")
            listener?.onClickPrevious()
        }
        btnPlayIv.setOnClickListener {
            listener?.onClickPlay()
        }
        btnNextIv.setOnClickListener {
            Log.d(TAG, "onClickNext")
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