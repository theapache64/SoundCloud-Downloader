package com.theah64.soundclouddownloader.server

import com.theah64.soundclouddownloader.utils.DownloadUtils

/**
 * Created by theapache64 : Nov 02 Mon,2020 @ 21:49
 */
object DirectDownloader {
    fun getFinalDownloadLink(downloadUrl: String): String? {
        val trackId = downloadUrl.split("/").last()
        return SoundCloudDownloader.getSoundCloudDownloadUrl(trackId)
    }
}