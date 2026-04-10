package com.zhangyt.module.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.module.home.databinding.HomeFragmentContactsBinding

class ContactsFragment : BaseFragment<HomeFragmentContactsBinding>() {
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        HomeFragmentContactsBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.titleBar.setTitle("通讯录")
    }
}
