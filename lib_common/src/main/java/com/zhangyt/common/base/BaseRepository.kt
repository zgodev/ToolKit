package com.zhangyt.common.base

import com.zhangyt.network.api.BaseResponse
import com.zhangyt.network.exception.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Repository 基类，统一网络请求处理。
 *
 * 示例：
 * ```
 * class LoginRepository : BaseRepository() {
 *     private val api = RetrofitClient.create(LoginApi::class.java)
 *
 *     fun login(name: String, pwd: String): Flow<User> = request {
 *         api.login(name, pwd)      // 返回 BaseResponse<User>
 *     }
 * }
 * ```
 */
abstract class BaseRepository {

    /**
     * 发起请求，自动解包 [BaseResponse]。
     * - code == 0 视为成功，返回 data
     * - 否则抛出 [ApiException]
     */
    protected fun <T> request(block: suspend () -> BaseResponse<T>): Flow<T> = flow {
        val response = block()
        if (response.isSuccess()) {
            val data = response.data
                ?: throw ApiException(response.code, "数据为空")
            emit(data)
        } else {
            throw ApiException(response.code, response.message)
        }
    }.flowOn(Dispatchers.IO)
}
