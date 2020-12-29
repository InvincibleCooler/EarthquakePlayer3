package com.eq.earthquakeplayer3.data


object SongDataMgr {
    var songList: ArrayList<SongData>? = null
    var songCurrentIndex = 0

    fun getCurrentSongData(): SongData? {
        return songList?.get(songCurrentIndex)
    }
}