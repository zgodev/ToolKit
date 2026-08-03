package com.zhangyt.core.model

/** 用户会话中的最小跨模块模型。 */
data class UserInfo(
    val userId: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val phone: String = "",
    val token: String = "",
)
