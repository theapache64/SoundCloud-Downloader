package com.theah64.soundclouddownloader.utils

import okhttp3.Request

/**
 * Created by theapache64 : Nov 03 Tue,2020 @ 20:32
 */
object UrlHelper {
    fun getFinalUrl(url: String): String? {
        val request = Request.Builder()
                .url(url)
                .build()

        return OkHttpUtils.getInstance().client.newCall(request).execute().let {
            val finalUrl = it.request.url.toString()
            finalUrl
        }
    }
}