package com.zhangyt.common.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 极轻量 Application 启动任务调度器。
 *
 * 目的：把非关键路径的 SDK / 日志 / 埋点初始化从主线程剥出来，避免冷启动阻塞。
 *
 * 使用模式：
 * ```
 * override fun onCreate() {
 *     super.onCreate()
 *
 *     // 主线程关键路径（相互依赖的按顺序写）
 *     AppStartup.mainInit { Utils.init(this) }
 *     AppStartup.mainInit { ARouter.init(this) }
 *     AppStartup.mainInit { registerActivityLifecycleCallbacks(AppManager) }
 *     AppStartup.mainInit { ThemeManager.init(this) }
 *
 *     // 后台并行（相互独立）
 *     AppStartup.asyncInit("xlog") { initXLog() }
 *     AppStartup.asyncInit("bugly") { Bugly.init(...) }
 * }
 * ```
 *
 * 约束：
 * - [asyncInit] 里不要访问主线程 UI，也别依赖 ARouter/MMKV 之外的主线程初始化结果
 * - 各 `asyncInit` 间相互独立（`SupervisorJob`：一个失败不影响其它）
 * - 内部异常只打日志，不重抛，避免启动时 Crash
 */
object AppStartup {

    private const val TAG = "AppStartup"

    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        Log.e(TAG, "async init failed", t)
    }

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + exceptionHandler
    )

    /**
     * 后台 IO 线程异步执行 [block]。
     * @param name 可选任务名，日志里方便排查是哪个 init 慢/失败
     */
    fun asyncInit(name: String = "task", block: suspend CoroutineScope.() -> Unit): Job =
        scope.launch {
            val start = System.currentTimeMillis()
            runCatching { block() }
                .onSuccess {
                    Log.d(TAG, "[$name] done in ${System.currentTimeMillis() - start}ms")
                }
                .onFailure { t ->
                    Log.e(TAG, "[$name] failed in ${System.currentTimeMillis() - start}ms", t)
                }
        }

    /**
     * 语义化占位：主线程立即执行。仅为了和 [asyncInit] 形成对照，使启动链路一目了然。
     */
    inline fun mainInit(block: () -> Unit) = block()
}
