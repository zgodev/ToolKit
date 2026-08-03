package com.zhangyt.common.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.Stack

/**
 * 全局 Activity 栈管理。
 *
 * 使用：直接取出单例，无需主动创建。
 * ```
 * AppManager.currentActivity()
 * AppManager.finishAll()
 * AppManager.exitApp()
 * ```
 */
object AppManager : Application.ActivityLifecycleCallbacks {

    private val stack = Stack<Activity>()

    /** 当前栈顶 Activity。 */
    fun currentActivity(): Activity? = if (stack.isEmpty()) null else stack.peek()

    /** 结束栈顶 Activity。 */
    fun finishCurrent() = currentActivity()?.finish()

    /** 结束所有 Activity。 */
    fun finishAll() {
        val snapshot = stack.toList()
        snapshot.forEach { if (!it.isFinishing) it.finish() }
        stack.clear()
    }

    /** 重建所有 Activity（主题/语言切换用）。 */
    fun recreateAll() {
        stack.toList().forEach { it.recreate() }
    }

    /** 彻底退出 App 进程。 */
    fun exitApp() {
        finishAll()
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    // ----------- ActivityLifecycleCallbacks -----------
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { stack.add(activity) }
    override fun onActivityDestroyed(activity: Activity) { stack.remove(activity) }
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}
