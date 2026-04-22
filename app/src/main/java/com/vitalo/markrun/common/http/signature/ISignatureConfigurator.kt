package com.vitalo.markrun.common.http.signature

import okhttp3.Request
import okhttp3.Response

interface ISignatureConfigurator {

    fun getNewRequest(request: Request): Request

    fun getNewResponse(response: Response): Response
}
