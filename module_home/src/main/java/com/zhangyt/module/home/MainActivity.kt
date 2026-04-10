package com.zhangyt.module.home

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseActivity
import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.home.databinding.HomeActivityMainBinding
import com.zhangyt.module.home.fragment.ChatFragment
import com.zhangyt.module.home.fragment.ContactsFragment
import com.zhangyt.module.home.fragment.DiscoverFragment
import com.zhangyt.module.home.fragment.MineFragment

/**
 * 类微信主页：底部 4 Tab + 4 Fragment。
 *
 * 架构：
 * - 使用 FragmentManager 的 show/hide，避免重复创建，保持状态。
 * - 底部 BottomNavigationView 控制切换。
 */
@Route(path = RouterPath.Home.ACTIVITY_MAIN)
class MainActivity : BaseActivity<HomeActivityMainBinding>() {

    private val fragments: List<Fragment> by lazy {
        listOf(ChatFragment(), ContactsFragment(), DiscoverFragment(), MineFragment())
    }
    private var currentIndex = 0

    override fun getViewBinding() = HomeActivityMainBinding.inflate(layoutInflater)

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
}
