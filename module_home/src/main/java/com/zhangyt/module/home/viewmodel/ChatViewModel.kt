package com.zhangyt.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zhangyt.common.base.BaseViewModel
import com.zhangyt.module.home.model.ChatItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 主页消息列表 ViewModel（演示数据）。
 */
class ChatViewModel : BaseViewModel() {

    val messages = MutableLiveData<List<ChatItem>>()

    fun loadMessages() {
        viewModelScope.launch {
            delay(200) // 模拟网络
            messages.value = listOf(
                ChatItem("1", "张三", "明天早上开会别忘了", System.currentTimeMillis() - 60_000, 2),
                ChatItem("2", "产品群", "[李四]: 需求评审已上传", System.currentTimeMillis() - 3_600_000, 9),
                ChatItem("3", "订阅号消息", "今日科技头条：AI 新突破", System.currentTimeMillis() - 7_200_000, 0),
                ChatItem("4", "家人群", "周末聚餐定在哪家？", System.currentTimeMillis() - 86_400_000, 1),
                ChatItem("5", "系统通知", "您的会员即将到期", System.currentTimeMillis() - 3 * 86_400_000, 0)
            )
        }
    }
}
