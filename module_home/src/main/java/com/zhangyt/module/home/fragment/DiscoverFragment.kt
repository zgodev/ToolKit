package com.zhangyt.module.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.module.home.databinding.HomeFragmentDiscoverBinding

class DiscoverFragment : BaseFragment<HomeFragmentDiscoverBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        HomeFragmentDiscoverBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.titleBar.setTitle("发现")
    }
}
