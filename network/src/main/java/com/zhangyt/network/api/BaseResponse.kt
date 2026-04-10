package com.zhangyt.network.api

/**
 * 统一后端响应结构。
 * 假设服务端协议：
 * ```
 * {
 *   "code": 0,
 *   "message": "ok",
 *   "data": { ... }
 * }
 * ```
 * 若协议不同，只需修改 [isSuccess] 或字段名。
 */
data class BaseResponse<T>(
    val code: Int = -1,
    val message: String = "",
    val data: T? = null
) {
    fun isSuccess() = code == 0
}
