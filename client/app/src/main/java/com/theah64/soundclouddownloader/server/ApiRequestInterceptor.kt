package com.theah64.soundclouddownloader.server

import com.theah64.soundclouddownloader.utils.APIRequestBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.net.URLDecoder

/**
 * Created by theapache64 : Nov 01 Sun,2020 @ 22:16
 */
class ApiRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        @Suppress("MoveVariableDeclarationIntoWhen")
        val targetUrl = request.url.toString()

        Timber.d("intercept: $targetUrl")

        return when (targetUrl) {
            APIRequestBuilder.IN_URL -> getFakeInResponse(request)
            APIRequestBuilder.HIT_URL -> getFakeHitResponse(request)
            else -> {
                if (targetUrl.startsWith(APIRequestBuilder.DOWNLOAD_URL)) {
                    val trackId = targetUrl.split("/").last()
                    getDownloadUrl(trackId)
                } else {
                    chain.proceed(request)
                }
            }
        }
    }

    private fun getDownloadUrl(trackId: String): Response {
        return null!!
    }

    private fun getFakeHitResponse(request: Request): Response {
        val formBody = request.body as FormBody
        val soundCloudUrl = URLDecoder.decode(formBody.encodedValue(0), "utf-8")
        Timber.d("getFakeHitResponse: SoundCloudURL : $soundCloudUrl")
        val jsonTracks = SoundCloudDownloader.getSoundCloudTracks(
                soundCloudUrl.hashCode().toString(),
                soundCloudUrl
        )

        val resp = SoundCloudDownloader.toJsonResponse(jsonTracks)

        return Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .request(request)
                .message("OK")
                .body(resp.toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                .build()
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