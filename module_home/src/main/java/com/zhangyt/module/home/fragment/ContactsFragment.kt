package com.zhangyt.module.home.fragment

import com.zhangyt.common.base.BaseFragment
import com.zhangyt.module.home.databinding.HomeFragmentContactsBinding

class ContactsFragment : BaseFragment<HomeFragmentContactsBinding>() {

    override fun initView() {
        binding.titleBar.setTitle("通讯录")
    }
}
