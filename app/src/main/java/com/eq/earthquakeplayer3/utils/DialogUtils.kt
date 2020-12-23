package com.eq.earthquakeplayer3.utils

import android.app.Activity
import android.app.AlertDialog


/**
 * Copyright (C) 2020 Kakao Inc. All rights reserved.
 *
 * Created by Invincible on 14/12/2020
 *
 */
object DialogUtils {
    fun createCommonDialog(activity: Activity, title: String, msg: String): AlertDialog.Builder {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setCancelable(false)
        return builder
    }
}