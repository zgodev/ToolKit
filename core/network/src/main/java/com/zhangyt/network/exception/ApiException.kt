package com.zhangyt.network.exception

/**
 * 业务异常（后端 code != 0 时抛出）。
 */
class ApiException(
    val code: Int,
    override val message: String
) : RuntimeException(message) {

    companion object {
        const val TOKEN_EXPIRED = 401
        const val NO_PERMISSION = 403
        const val NOT_FOUND = 404
        const val SERVER_ERROR = 500
    }
}
