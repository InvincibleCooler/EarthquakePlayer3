package com.eq.earthquakeplayer3

import androidx.multidex.MultiDexApplication
import java.io.File


class EqApp : MultiDexApplication() {
    companion object {
        lateinit var instance: EqApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getExoPlayerCacheDir(): File {
        return File(cacheDir, "videos")
    }
}