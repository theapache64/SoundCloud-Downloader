package com.theah64.soundclouddownloader.utils

import com.theapache64.expekt.should
import org.junit.Test

/**
 * Created by theapache64 : Nov 03 Tue,2020 @ 20:34
 */
class UrlHelperTest {
    @Test
    fun test() {
        UrlHelper.getFinalUrl("https://soundcloud.app.goo.gl/1Tfs")
                .should.equal("https://soundcloud.com/mushtaq-boohar/katta_naal_rakhda_mix_rahim_pa?p=a&c=0")
    }
}