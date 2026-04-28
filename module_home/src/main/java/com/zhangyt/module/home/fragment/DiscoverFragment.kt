package com.zhangyt.module.home.fragment

import com.zhangyt.common.base.BaseFragment
import com.zhangyt.module.home.databinding.HomeFragmentDiscoverBinding

class DiscoverFragment : BaseFragment<HomeFragmentDiscoverBinding>() {

    override fun initView() {
        binding.titleBar.setTitle("发现")
    }
}
