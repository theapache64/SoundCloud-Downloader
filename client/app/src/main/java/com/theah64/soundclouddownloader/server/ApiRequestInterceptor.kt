package com.theah64.soundclouddownloader.server

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * Created by theapache64 : Nov 01 Sun,2020 @ 22:16
 */
class ApiRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.d("intercept: ${chain.request().url}")
        return chain.proceed(chain.request())
    }
}