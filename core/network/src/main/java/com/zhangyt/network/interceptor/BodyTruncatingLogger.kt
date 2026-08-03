package com.zhangyt.network.interceptor

import android.util.Log
import com.zhangyt.network.config.NetworkConfig
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Debug 网络日志 Logger：body 过大时自动截断，避免日志爆炸 / OOM。
 *
 * 使用：
 * ```
 * HttpLoggingInterceptor(BodyTruncatingLogger()).apply {
 *     level = HttpLoggingInterceptor.Level.BODY
 * }
 * ```
 */
class BodyTruncatingLogger(
    private val tag: String = "HTTP"
) : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        val max = NetworkConfig.logBodyMaxBytes.toInt()
        if (message.length <= max) {
            Log.d(tag, message)
        } else {
            // 头尾各留 1/2 max 字节，中间显示省略
            val half = max / 2
            val head = message.take(half)
            val tail = message.takeLast(half)
            Log.d(tag, head)
            Log.d(tag, "... (truncated ${message.length - max} chars) ...")
            Log.d(tag, tail)
        }
    }
}
