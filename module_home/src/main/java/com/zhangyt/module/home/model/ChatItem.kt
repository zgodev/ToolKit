package com.zhangyt.module.home.model

data class ChatItem(
    val id: String,
    val nickname: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int
)
