package com.zhangyt.toolkit

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseActivity
import com.zhangyt.common.router.RouterManager
import com.zhangyt.common.router.RouterPath
import com.zhangyt.toolkit.databinding.AppActivityMainBinding

/**
 * 类微信主页：底部 4 Tab + 4 Fragment。
 *
 * 架构：
 * - 使用 FragmentManager 的 show/hide，避免重复创建，保持状态。
 * - 底部 BottomNavigationView 控制切换。
 */
@Route(path = RouterPath.Home.ACTIVITY_MAIN)
class MainActivity : BaseActivity<AppActivityMainBinding>() {

    private val fragments: List<Fragment> by lazy {
        listOf(
            RouterPath.Home.FRAGMENT_CHAT,
            RouterPath.Home.FRAGMENT_CONTACTS,
            RouterPath.Home.FRAGMENT_DISCOVER,
            RouterPath.Mine.FRAGMENT_MINE,
        ).map(::requiredFragment)
    }
    private var currentIndex = 0

    override fun initView() {
        // 添加所有 Fragment
        val tx = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { i, f ->
            tx.add(R.id.homeContainer, f, f.javaClass.simpleName)
            if (i != 0) tx.hide(f)
        }
        tx.commitNow()

        binding.bottomNav.setOnItemSelectedListener { item ->
            val index = when (item.itemId) {
                R.id.home_tab_chat -> 0
                R.id.home_tab_contacts -> 1
                R.id.home_tab_discover -> 2
                R.id.home_tab_mine -> 3
                else -> 0
            }
            switchTo(index)
            true
        }
    }

    private fun switchTo(index: Int) {
        if (index == currentIndex) return
        supportFragmentManager.beginTransaction()
            .hide(fragments[currentIndex])
            .show(fragments[index])
            .commit()
        currentIndex = index
    }

    private fun requiredFragment(route: String): Fragment =
        requireNotNull(RouterManager.getFragment(route)) {
            "ARouter route is not registered: $route"
        }
}
