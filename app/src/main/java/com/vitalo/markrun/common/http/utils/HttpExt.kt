package com.vitalo.markrun.common.http.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.launch(success: (data: Response<T>) -> Unit, failed: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            success(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            failed(t)
        }
    })
}
