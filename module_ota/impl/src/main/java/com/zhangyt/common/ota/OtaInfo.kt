package com.zhangyt.common.ota

/**
 * OTA 升级信息数据类
 *
 * @param versionCode 新版本号
 * @param versionName 新版本名称
 * @param downloadUrl APK 下载地址
 * @param fileSize    文件大小（字节）
 * @param md5         文件 MD5 校验值
 * @param updateLog   更新日志
 * @param forceUpdate 是否强制更新
 */
data class OtaInfo(
    val versionCode: Int = 0,
    val versionName: String = "",
    val downloadUrl: String = "",
    val fileSize: Long = 0L,
    val md5: String = "",
    val updateLog: String = "",
    val forceUpdate: Boolean = false
)
