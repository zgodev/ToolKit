package com.zhangyt.module.home.fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.home.databinding.HomeFragmentContactsBinding

@Route(path = RouterPath.Home.FRAGMENT_CONTACTS)
class ContactsFragment : BaseFragment<HomeFragmentContactsBinding>() {

    override fun initView() {
        binding.titleBar.setTitle("通讯录")
    }
}
