package com.vitalo.markrun.common.http.wrapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class NetworkError(val code: Int = 0, val msg: String?) : ResultWrapper<Nothing>()
}

/**
 * val result = safeApiCall() { service.queryXXXX() }
 *
 * when (result) {
 *      is ResultWrapper.Success -> { val data = result.data }
 *      is ResultWrapper.NetworkError -> { val code = result.code; val msg = result.msg }
 * }
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    ResultWrapper.NetworkError(
                        throwable.code(),
                        throwable.response()?.errorBody()?.string()
                    )
                }
                else -> {
                    ResultWrapper.NetworkError(-1, throwable.message)
                }
            }
        }
    }
}
