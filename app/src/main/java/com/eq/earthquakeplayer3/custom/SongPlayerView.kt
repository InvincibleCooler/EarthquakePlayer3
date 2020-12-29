package com.eq.earthquakeplayer3.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.eq.earthquakeplayer3.R


class SongPlayerView : LinearLayout {
    companion object {
        const val TAG = "SongPlayerView"
    }

    lateinit var thumbIv: ImageView
    private lateinit var songNameTv: TextView
    private lateinit var artistNameTv: TextView
    private lateinit var updateTimeTv: TextView
    private lateinit var totalTimeTv: TextView
    private lateinit var seekBar: SeekBar
    lateinit var controlView: SongPlayerControlView

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.song_player_layout, this, true)

        thumbIv = view.findViewById(R.id.iv_thumb)
        songNameTv = view.findViewById(R.id.song_name_tv)
        artistNameTv = view.findViewById(R.id.artist_name_tv)
        updateTimeTv = view.findViewById(R.id.update_time_tv)
        totalTimeTv = view.findViewById(R.id.total_time_tv)
        seekBar = view.findViewById(R.id.seek_bar)
        controlView = view.findViewById(R.id.control_view)
    }

    fun setSongName(songName: String) {
        songNameTv.text = songName
    }

    fun setArtistName(artistName: String) {
        artistNameTv.text = artistName
    }

    fun setUpdateTime(updateTime: String) {
        updateTimeTv.text = updateTime
    }

    fun setTotalTime(totalTime: String) {
        totalTimeTv.text = totalTime
    }
}