package com.eq.earthquakeplayer3.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.eq.earthquakeplayer3.async.CoroutineAsyncTaskWrapper
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.data.SongDataMgr


class SongListViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "SongListViewModel"

        private val songProjection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Albums.ALBUM_ID,  // album_art
            MediaStore.Audio.Media.DURATION
        )

        private val AUDIO_MEDIA_DATA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI // content://media/external/audio/media
        private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")
    }

    private val task: SongListAsyncTask by lazy {
        SongListAsyncTask()
    }

    val isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

    val dataList = MutableLiveData<ArrayList<SongData>>().apply {
        task.execute()
    }

    fun cancelJob() {
        task.cancel()
    }

    private inner class SongListAsyncTask : CoroutineAsyncTaskWrapper<Context, ArrayList<SongData>>() {
        override fun preTask() {
            isLoading.postValue(true)
        }

        override suspend fun backgroundWork(param: Context?): ArrayList<SongData> {
            val songDataList = ArrayList<SongData>()

            val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
            val selectionArgs = arrayOf(mimeType)
            val orderBy = MediaStore.Audio.Media.DATE_MODIFIED

            getApplication<Application>().contentResolver.query(AUDIO_MEDIA_DATA_URI, songProjection, selection, selectionArgs, "$orderBy ASC").use { c ->
                if (c?.moveToFirst() == true) {
                    do {
                        val id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID))
                        val title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val artistName = c.getString(c.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                        val albumName = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                        val albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID))
                        val duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION))
                        val contentUri = ContentUris.withAppendedId(AUDIO_MEDIA_DATA_URI, id).toString()
                        val albumPath = ContentUris.withAppendedId(ALBUM_ART_URI, albumId).toString()

                        Log.d(TAG, "id : $id")
                        Log.d(TAG, "title : $title")
                        Log.d(TAG, "artistName : $artistName")
                        Log.d(TAG, "albumName : $albumName")
                        Log.d(TAG, "albumId : $albumId")
                        Log.d(TAG, "duration : $duration")
                        Log.d(TAG, "contentUri : $contentUri")
                        Log.d(TAG, "albumPath : $albumPath")
                        songDataList.run {
                            add(SongData(id, title, artistName, albumName, duration, contentUri, albumPath))
                        }
                    } while (c.moveToNext())
                }
            }
            return songDataList
        }

        override fun postTask(result: ArrayList<SongData>?) {
            SongDataMgr.songList = result
            dataList.postValue(result)
            isLoading.postValue(false)
        }
    }
}