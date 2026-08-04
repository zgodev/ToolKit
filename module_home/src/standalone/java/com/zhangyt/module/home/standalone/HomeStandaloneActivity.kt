package com.zhangyt.module.home.standalone

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.zhangyt.common.router.RouterManager
import com.zhangyt.common.router.RouterPath

class HomeStandaloneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this).apply { id = View.generateViewId() }
        setContentView(container)
        if (savedInstanceState == null) {
            val fragment = requireNotNull(RouterManager.getFragment(RouterPath.Home.FRAGMENT_CHAT))
            supportFragmentManager.beginTransaction().replace(container.id, fragment).commit()
        }
    }
}
