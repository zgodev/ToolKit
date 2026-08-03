package com.zhangyt.module.home.fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.module.home.api.HomeRoutes
import com.zhangyt.module.home.databinding.HomeFragmentContactsBinding

@Route(path = HomeRoutes.FRAGMENT_CONTACTS)
class ContactsFragment : BaseFragment<HomeFragmentContactsBinding>() {

    override fun initView() {
        binding.titleBar.setTitle("通讯录")
    }
}
