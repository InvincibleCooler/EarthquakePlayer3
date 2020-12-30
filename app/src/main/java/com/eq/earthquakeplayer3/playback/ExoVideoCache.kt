package com.eq.earthquakeplayer3.playback

import android.content.Context
import com.eq.earthquakeplayer3.EqApp
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache


class ExoVideoCache {
    companion object {
        private var sDownloadCache: SimpleCache? = null

        fun getInstance(context: Context): SimpleCache? {
            if (sDownloadCache == null) {
                val cacheFolder = EqApp.instance.getExoPlayerCacheDir()
                val cacheEvictor = LeastRecentlyUsedCacheEvictor(60 * 1024 * 1024)
                val databaseProvider: DatabaseProvider = ExoDatabaseProvider(context)

                sDownloadCache = SimpleCache(cacheFolder, cacheEvictor, databaseProvider)
            }
            return sDownloadCache
        }
    }
}