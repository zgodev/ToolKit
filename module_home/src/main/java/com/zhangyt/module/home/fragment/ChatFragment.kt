package com.zhangyt.module.home.fragment

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.home.adapter.ChatListAdapter
import com.zhangyt.module.home.databinding.HomeFragmentChatBinding
import com.zhangyt.module.home.viewmodel.ChatViewModel

/**
 * 类微信首页：消息列表。
 * 使用 MVVM：ViewModel 提供数据，Fragment 仅负责 UI 渲染。
 */
@Route(path = RouterPath.Home.FRAGMENT_CHAT)
class ChatFragment : BaseFragment<HomeFragmentChatBinding>() {

    private val viewModel: ChatViewModel by viewModels()
    private val adapter = ChatListAdapter()

    override fun initView() {
        binding.titleBar.setTitle("微信")
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter
    }

    override fun initData() {
        viewModel.loadMessages()
    }

    override fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.setList(list)
        }
    }
}
