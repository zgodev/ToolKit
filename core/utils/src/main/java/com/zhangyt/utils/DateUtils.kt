package com.zhangyt.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日期工具类。
 *
 * 示例：
 * ```
 * DateUtils.format(System.currentTimeMillis())         // 2026-04-10 15:30:00
 * DateUtils.format(time, DateUtils.YMD)                // 2026-04-10
 * DateUtils.friendlyTime(timestamp)                    // 3 分钟前
 * ```
 */
object DateUtils {

    const val YMD_HMS = "yyyy-MM-dd HH:mm:ss"
    const val YMD = "yyyy-MM-dd"
    const val HMS = "HH:mm:ss"
    const val HM = "HH:mm"

    fun format(timestamp: Long, pattern: String = YMD_HMS): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))

    fun parse(dateStr: String, pattern: String = YMD_HMS): Long =
        runCatching {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(dateStr)?.time ?: 0L
        }.getOrDefault(0L)

    /**
     * 友好时间：刚刚 / x 分钟前 / x 小时前 / yyyy-MM-dd
     */
    fun friendlyTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000} 分钟前"
            diff < 86400_000 -> "${diff / 3600_000} 小时前"
            diff < 2 * 86400_000 -> "昨天"
            diff < 30L * 86400_000 -> "${diff / 86400_000} 天前"
            else -> format(timestamp, YMD)
        }
    }
}
