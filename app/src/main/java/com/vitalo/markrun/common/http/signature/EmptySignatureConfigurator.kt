package com.vitalo.markrun.common.http.signature

import okhttp3.Request
import okhttp3.Response

class EmptySignatureConfigurator : ISignatureConfigurator {
    override fun getNewRequest(request: Request): Request {
        return request
    }

    override fun getNewResponse(response: Response): Response {
        return response
    }
}
