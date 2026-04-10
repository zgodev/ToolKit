package com.zhangyt.toolkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhangyt.common.router.RouterManager
import com.zhangyt.common.router.RouterPath
import com.zhangyt.common.user.UserManager

/**
 * 启动页：根据登录态决定跳转到登录页或主页。
 * 不使用 ViewBinding，直接用代码布局以减少 UI 依赖。
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 根据登录态跳转
        if (UserManager.isLogin()) {
            RouterManager.start(RouterPath.Home.ACTIVITY_MAIN)
        } else {
            RouterManager.start(RouterPath.Login.ACTIVITY_LOGIN)
        }
        finish()
    }
}
