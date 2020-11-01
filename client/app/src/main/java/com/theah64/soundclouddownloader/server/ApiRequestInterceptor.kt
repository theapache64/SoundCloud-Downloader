package com.theah64.soundclouddownloader.server

import com.theah64.soundclouddownloader.utils.APIRequestBuilder
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Created by theapache64 : Nov 01 Sun,2020 @ 22:16
 */
class ApiRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        @Suppress("MoveVariableDeclarationIntoWhen")
        val targetUrl = request.url.toString()

        return when (targetUrl) {
            APIRequestBuilder.IN_URL -> getFakeInResponse(request)
            APIRequestBuilder.HIT_URL -> getFakeHitResponse(request)
            else -> chain.proceed(request)
        }
    }

    private fun getFakeHitResponse(request: Request): Response {
        TODO("Not yet implemented")
    }

    private fun getFakeInResponse(request: Request): Response {

        val jsonResp = """
            {
                "message":"OK",
                "error":false,
                "data":{
                    "api_key": "thisIsSomeFakeApiKey"
                }
            }
        """.trimIndent()

        return Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .request(request)
                .message("OK")
                .body(jsonResp.toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                .build()
    }
}