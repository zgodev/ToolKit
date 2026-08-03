package com.zhangyt.module.login.standalone

import com.zhangyt.common.CommonApplication
import com.zhangyt.network.config.NetworkConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoginStandaloneApplication : CommonApplication() {
    override fun onCreate() {
        super.onCreate()
        NetworkConfig.baseUrl = "https://fake.api.example.com/"
        NetworkConfig.debuggable = true
    }
}
