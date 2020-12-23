package com.eq.earthquakeplayer3.data


data class SongData(
    val id: Long = -1,
    val title: String = "",
    val artistName: String = "",
    val albumName: String = "",
    val duration: Long = 0,
    val data: String = "",
    val albumPath: String = ""
)
