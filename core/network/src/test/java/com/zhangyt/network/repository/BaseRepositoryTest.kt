package com.zhangyt.network.repository

import com.zhangyt.network.api.BaseResponse
import com.zhangyt.network.exception.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseRepositoryTest {

    private val repository = TestRepository()

    @Test
    fun `successful response emits its payload`() = runBlocking {
        val result = repository.execute { BaseResponse(code = 0, data = "payload") }.single()

        assertEquals("payload", result)
    }

    @Test
    fun `successful response with null payload becomes api error`() = runBlocking {
        val error = runCatching {
            repository.execute<String> { BaseResponse(code = 0, data = null) }.single()
        }.exceptionOrNull()

        assertTrue(error is ApiException)
        assertEquals(0, (error as ApiException).code)
        assertEquals("数据为空", error.message)
    }

    @Test
    fun `failed response preserves backend code and message`() = runBlocking {
        val error = runCatching {
            repository.execute<String> {
                BaseResponse(code = ApiException.TOKEN_EXPIRED, message = "登录已过期")
            }.single()
        }.exceptionOrNull()

        assertTrue(error is ApiException)
        assertEquals(ApiException.TOKEN_EXPIRED, (error as ApiException).code)
        assertEquals("登录已过期", error.message)
    }

    private class TestRepository : BaseRepository() {
        fun <T> execute(block: suspend () -> BaseResponse<T>): Flow<T> = request(block)
    }
}
