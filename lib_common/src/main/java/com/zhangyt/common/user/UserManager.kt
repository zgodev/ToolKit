package com.zhangyt.common.user

import com.google.gson.Gson
import com.tencent.mmkv.MMKV

/**
 * 全局用户会话管理（登录态、Token、用户信息）。
 *
 * 使用：
 * ```
 * if (UserManager.isLogin()) { ... }
 * UserManager.saveUser(user)
 * UserManager.logout()
 * ```
 */
object UserManager {

    private const val KEY_TOKEN = "user_token"
    private const val KEY_USER_INFO = "user_info"

    private val mmkv by lazy { MMKV.defaultMMKV() }
    private val gson by lazy { Gson() }

    /** 是否已登录。 */
    fun isLogin(): Boolean = !getToken().isNullOrEmpty()

    fun getToken(): String? = mmkv.decodeString(KEY_TOKEN)

    fun saveToken(token: String) = mmkv.encode(KEY_TOKEN, token)

    fun saveUser(user: UserInfo) {
        mmkv.encode(KEY_USER_INFO, gson.toJson(user))
        saveToken(user.token)
    }

    fun getUser(): UserInfo? {
        val json = mmkv.decodeString(KEY_USER_INFO) ?: return null
        return runCatching { gson.fromJson(json, UserInfo::class.java) }.getOrNull()
    }

    fun logout() {
        mmkv.remove(KEY_TOKEN)
        mmkv.remove(KEY_USER_INFO)
    }
}

/** 用户信息数据类，可根据业务字段扩展。 */
data class UserInfo(
    val userId: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val phone: String = "",
    val token: String = ""
)
