package com.zhangyt.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    private static final String TAG = "ThreadPoolUtil";
    /**
     * 磁盘IO线程池
     **/
    private static ExecutorService ioThreadExecutor;
    /**
     * 网络IO线程池
     **/
    private static ExecutorService netThreadExecutor;
    /**
     * UI线程
     **/
    private static MainThreadExecutor mainThread;
    private static ExecutorService fixThreadExecutor;
    private static ExecutorService singleThreadExecutor;
    /**
     * 定时任务线程池
     **/
    private static ScheduledExecutorService scheduledExecutor;

    private volatile static ThreadPoolUtil appExecutors;

    public static ThreadPoolUtil getInstance() {
        if (appExecutors == null) {
            synchronized (ThreadPoolUtil.class) {
                if (appExecutors == null) {
                    appExecutors = new ThreadPoolUtil();
                }
            }
        }
        return appExecutors;
    }

    /**
     * 核心线程数与最大线程数相同
     * @return
     */
    public static ExecutorService fixThreadExecutor() {
        if (fixThreadExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (fixThreadExecutor == null) {
                    fixThreadExecutor = Executors.newFixedThreadPool(6);
                }
            }
        }
        return fixThreadExecutor;
    }

    public static ExecutorService singleThreadExecutor() {
        if (singleThreadExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (singleThreadExecutor == null) {
                    singleThreadExecutor = Executors.newSingleThreadExecutor();
                }
            }
        }
        return singleThreadExecutor;
    }

    /**
     * 定时(延时)任务线程池
     * <p>
     * 替代Timer,执行定时任务,延时任务
     */
    public static ExecutorService scheduledExecutor() {
        if (scheduledExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (scheduledExecutor == null) {
                    scheduledExecutor = Executors.newScheduledThreadPool(5);
                }
            }
        }
        return scheduledExecutor;
    }

    /**
     * UI线程
     * <p>
     * Android 的MainThread
     * UI线程不能做的事情这个都不能做
     */
    public Executor mainThread() {
        if (mainThread == null) {
            synchronized (ThreadPoolUtil.class) {
                if (mainThread == null) {
                    mainThread = new MainThreadExecutor();
                }
            }
        }
        return mainThread;
    }

    /**
     * 磁盘IO线程池（单线程）
     * <p>
     * 和磁盘操作有关的进行使用此线程(如读写数据库,读写文件)
     * 禁止延迟,避免等待
     * 此线程不用考虑同步问题
     */
    public static ExecutorService getIOExecutor() {
        if (ioThreadExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (ioThreadExecutor == null) {
                    ioThreadExecutor = new ThreadPoolExecutor(10, 50, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), r -> new Thread(r, "io_executor"), (r, executor) -> Log.e(TAG, "rejectedExecution: disk io executor queue overflow"));
                }
            }
        }
        return ioThreadExecutor;
    }

    /**
     * 网络IO线程池
     * <p>
     * 网络请求,异步任务等适用此线程
     * 不建议在这个线程 sleep 或者 wait
     */
    public static ExecutorService getNetExecutor() {
        if (netThreadExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (netThreadExecutor == null) {
                    netThreadExecutor = new ThreadPoolExecutor(5, 10, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(50), r -> new Thread(r, "network_executor"), (r, executor) -> Log.e(TAG, "rejectedExecution: network executor queue overflow"));
                }
            }
        }
        return netThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
