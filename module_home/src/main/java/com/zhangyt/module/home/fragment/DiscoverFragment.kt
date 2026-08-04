package com.zhangyt.module.home.fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.home.databinding.HomeFragmentDiscoverBinding

@Route(path = RouterPath.Home.FRAGMENT_DISCOVER)
class DiscoverFragment : BaseFragment<HomeFragmentDiscoverBinding>() {

    override fun initView() {
        binding.titleBar.setTitle("发现")
    }
}
