package com.eq.earthquakeplayer3.async

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * Copyright (C) 2020 Kakao Inc. All rights reserved.
 */
abstract class CoroutineAsyncTaskWrapper<Param, Result> : CoroutineScope {
    companion object {
        private const val TAG = "CoroutineAsyncTaskWrapper"
    }

    private var job = Job()
//    override val coroutineContext = Dispatchers.Main + job // preTask, postTask에서 UI처리가 있을수 있기 때문에 일단 Dispatcher는 Main으로 설정함

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private fun startTask(param: Param? = null) {
        launch {
            preTask()
            val result = doInBackground(param)
            postTask(result)
        }
    }

    open fun preTask() {
    }

    private suspend fun doInBackground(param: Param? = null): Result? {
        return withContext(Dispatchers.IO) {
            backgroundWork(param)
        }
    }

    abstract suspend fun backgroundWork(param: Param? = null): Result?

    open fun postTask(result: Result? = null) {
    }

    open fun cancel() {
        job.cancel()
    }

    open suspend fun cancelAndJoin() {
        job.cancelAndJoin()
    }

    fun execute(param: Param? = null) {
        startTask(param)
    }
}