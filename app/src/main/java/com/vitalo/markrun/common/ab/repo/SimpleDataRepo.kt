package com.vitalo.markrun.common.ab.repo

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

abstract class SimpleDataRepo<T> {
    protected val logTag = "DataRepo-${this::class.java.simpleName}"
    private val liveData = MutableLiveData<T>()
    private val loadStatus = AtomicReference(FetchStatus.IDLE)
    private val singleThreadPoolExecutor = Executors.newSingleThreadExecutor()
    private val initRepoLatch = CountDownLatch(1)
    private val requestVersion = AtomicInteger(0)
    private val fetchStatus = MutableLiveData<FetchStatus>()
    protected var lastFetchTime = 0L
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 这里返回自己的缓存数据
     */
    @WorkerThread
    protected abstract fun fetchCacheData(callback: (cacheData: T?) -> Unit)

    /**
     * 这里返回网络请求的数据
     */
    @WorkerThread
    protected abstract fun fetchRemoteData(callback: (remoteData: T?, success: Boolean) -> Unit)

    /**
     * 初始化仓库数据, 首次刷新数据会触发
     */
    private fun initializeRepo() {
        if (!loadStatus.compareAndSet(FetchStatus.IDLE, FetchStatus.INITIALIZE)) {
            return
        }
        Log.d(logTag, "init repo start")
        dispatchHandlerMessage(Message.obtain().apply {
            what = MESSAGE_INIT_REPO
        })
    }

    /**
     * 预加载缓存数据
     */
    fun preloadCacheData() {
        initializeRepo()
    }

    /**
     * 刷新网络数据
     */
    @AnyThread
    fun refreshRemoteData(force: Boolean = false) {
        initializeRepo()
        if (!force && !shouldFetchRemoteData()) {
            return
        }
        loadStatus.set(FetchStatus.FETCHING)
        ensureOnUiThread {
            fetchStatus.value = FetchStatus.FETCHING
        }

        val message = Message.obtain()
        message.what = MESSAGE_REFRESH_DATA
        message.arg1 = requestVersion.incrementAndGet()
        dispatchHandlerMessage(message)
        lastFetchTime = System.currentTimeMillis()
    }

    @AnyThread
    fun refreshDataAfterCheck(force: Boolean = false) {
        if (!force && !shouldFetchRemoteData()) {
            initializeRepo()
            return
        }
        loadStatus.set(FetchStatus.FETCHING)
        ensureOnUiThread {
            fetchStatus.value = FetchStatus.FETCHING
        }
        val message = Message.obtain()
        message.what = MESSAGE_REFRESH_DATA
        message.arg1 = requestVersion.incrementAndGet()
        dispatchHandlerMessage(message)
        lastFetchTime = System.currentTimeMillis()
    }

    open fun shouldFetchRemoteData(): Boolean {
        val status = loadStatus.get()
        if (status == FetchStatus.INITIALIZE) {
            return true
        }
        if (status == FetchStatus.FAILED) {
            return true
        }
        if (status == FetchStatus.FETCHING) {
            Log.d(logTag, "loading已经超过10s, 判断为可以加载")
            return System.currentTimeMillis() - lastFetchTime > 10 * 1000
        }
        return false
    }

    private fun dispatchHandlerMessage(message: Message) {
        if (message.what == MESSAGE_INIT_REPO) {
            fetchCacheData { cacheData: T? ->
                if (cacheData != null) {
                    liveData.postValue(cacheData)
                }
                initRepoLatch.countDown()
            }
        } else if (message.what == MESSAGE_REFRESH_DATA) {
            try {
                initRepoLatch.await(10, TimeUnit.SECONDS)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            val messageVersion = message.arg1
            // 请求使用当前线程阻塞时可能会出现多个请求并列的情况
            val shouldAbandonRequest = messageVersion != requestVersion.get()
            if (shouldAbandonRequest) {
                Log.d(logTag, "request is abandon.")
                return
            }
            fetchRemoteData { remoteData, success ->
                val shouldAbandonResponse = messageVersion != requestVersion.get()
                if (shouldAbandonResponse) {
                    Log.d(logTag, "response is abandon.")
                    return@fetchRemoteData
                }
                Log.d(logTag, "response receive and posting, success:${success}")
                if (success) {
                    liveData.postValue(remoteData)
                    loadStatus.set(FetchStatus.SUCCESS)
                } else {
                    liveData.postValue(null)
                    loadStatus.set(FetchStatus.FAILED)
                }
                ensureOnUiThread {
                    fetchStatus.value = if (success) {
                        FetchStatus.SUCCESS
                    } else {
                        FetchStatus.FAILED
                    }
                }
            }
        }
    }

    fun enqueueTask(r: Runnable) {
        singleThreadPoolExecutor.submit(r)
    }

    /**
     * 获取当前拉取状态
     */
    protected fun getLoadStatus(): FetchStatus {
        return loadStatus.get()
    }

    /**
     * 返回拉取状态的LiveData
     * 仅分发三个值: FETCHING, SUCCESS, FAILED
     */
    fun getStatusLiveData(): LiveData<FetchStatus> {
        return fetchStatus
    }

    fun getObservableData(): LiveData<T> {
        return liveData
    }

    private fun ensureOnUiThread(runnable: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable()
        } else {
            mainHandler.post(runnable)
        }
    }

    companion object {
        private const val MESSAGE_INIT_REPO = 1000
        private const val MESSAGE_REFRESH_DATA = 1001
    }

    enum class FetchStatus {
        IDLE,
        INITIALIZE,
        FETCHING,
        SUCCESS,
        FAILED
    }
}