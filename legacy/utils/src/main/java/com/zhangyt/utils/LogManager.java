
package com.zhangyt.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogManager {
    private static final String TAG = "ZhangYT\t";


    private static boolean debugLogOn = true;
    private static boolean mIsWrite = true;

    public static boolean isIsWrite() {
        return mIsWrite;
    }

    public static void setIsWrite(String path, boolean isWrite) {
        mIsWrite = isWrite;
        if (isWrite) {
            WriteLog.init(path, getFileName(), true);
        }
        checkAndDeleteLogFile(path);
    }

    public static void printMethodName(String TAG) {
        try {
            LogManager.e(TAG, new Throwable().getStackTrace()[1].getMethodName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMethodName() {
        try {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            if (stackTraceElements.length > 1) {
                StackTraceElement stackTraceElement = stackTraceElements[1];
                LogManager.e(stackTraceElement.getClassName(),
                        stackTraceElement.getMethodName() + " line:" + stackTraceElement.getLineNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMethodAndStr(String message) {
        try {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            if (stackTraceElements.length > 1) {
                StackTraceElement stackTraceElement = stackTraceElements[1];
                LogManager.e(stackTraceElement.getClassName(),
                        stackTraceElement.getMethodName() + " line:" + stackTraceElement.getLineNumber() + " " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回log的开关状态
     *
     * @return 开关状态;true 表示日志打开;false 表示日志关闭
     */
    public static boolean isDebugLogOn() {
        return debugLogOn;
    }

    /**
     * 设置log的开关状态
     *
     * @param debugLogOn 开关状态;true 表示日志打开;false 表示日志关闭
     */
    public static void setDebugLogOn(boolean debugLogOn) {
        LogManager.debugLogOn = debugLogOn;
    }

    private static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return "ZLog_" + date + ".txt";
    }

    private static void write(String tag, String msg) {
        if (!mIsWrite) return;
        WriteLog.writeLogFile(tag + "==" + msg);
    }

    private static void write(String tag, String msg, Throwable throwable) {
        if (!mIsWrite) return;
        WriteLog.writeLogFile(tag + "==" + msg + "\n" + Log.getStackTraceString(throwable));
    }

    @SuppressLint("SimpleDateFormat")
    private static void checkAndDeleteLogFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles(new LogFileFilter());
            if (files == null)
                return;
            for (File curFile : files) {
                Log.e(TAG, "logFileName:" + curFile.getName());
                int start = curFile.getName().indexOf("_") + 1;
                int end = curFile.getName().lastIndexOf(".");
                String time = curFile.getName().substring(start, end);
                try {
                    Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
                    if (parse != null) {
                        long time1 = parse.getTime();
                        if ((System.currentTimeMillis() - time1) > 7 * 24 * 3600 * 1000) {//删除大于七天的日志
                            boolean isDelete = curFile.delete();
                            Log.e(TAG, "删除超时日志文件：" + curFile.getName() + " isDelete:" + isDelete);
                        }
                    } else {
                        Log.e(TAG, "log file name parse error");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class LogFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("ZLog_");
        }
    }

    /**
     * 详细的log日志打印
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     */
    public static void v(String tag, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.v(TAG + tag, msg);
            write(tag, msg);
        }
    }

    /**
     * 详细的log日志打印
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     * @param tr  日志中附加的异常信息
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.v(TAG + tag, msg, tr);
            write(tag, msg, tr);
        } else if (tr != null) {
            tr.printStackTrace();
        }
    }

    /**
     * 打印Debug 日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     */
    public static void d(String tag, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.d(TAG + tag, msg);
            write(tag, msg);
        }
    }

    /**
     * 打印Debug 日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     * @param tr  日志中附加的异常信息
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.d(TAG + tag, msg, tr);
            write(tag, msg, tr);
        }else if (tr!=null){
            tr.printStackTrace();
        }
    }

    /**
     * 打印Debug日志信息
     *
     * @param tag    打印日志的唯一表示,一般是类名或者方法名
     * @param method 日志所处方法名
     * @param msg    准备输出的日志信息
     */
    public static void d(String tag, String method, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.d(TAG + tag, method + ";" + msg);
            write(tag, msg);
        }
    }

    /**
     * 打印Info 日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     */
    public static void i(String tag, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.i(TAG + tag, msg);
            write(tag, msg);
        }
    }

    /**
     * 打印Info 日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     * @param tr  日志中附加的异常信息
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.i(TAG + tag, msg, tr);
            write(tag, msg, tr);
        }else if (tr!=null){
            tr.printStackTrace();
        }
    }

    /**
     * 打印warn日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     */
    public static void w(String tag, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.w(TAG + tag, msg);
            write(tag, msg);
        }
    }

    /**
     * 打印warn日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     * @param tr  日志中附加的异常信息
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.w(TAG + tag, msg, tr);
            write(tag, msg, tr);
        }else if (tr!=null){
            tr.printStackTrace();
        }
    }

    /**
     * 打印Error日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     */
    public static void e(String tag, String msg) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.e(TAG + tag, msg);
            write(tag, msg);
        }
    }

    /**
     * 打印Error日志信息
     *
     * @param tag 打印日志的唯一表示,一般是类名或者方法名
     * @param msg 准备输出的日志信息
     * @param tr  日志中附加的异常信息
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (debugLogOn && !TextUtils.isEmpty(msg)) {
            Log.e(TAG + tag, msg, tr);
            write(tag, msg, tr);
        }else if (tr!=null){
            tr.printStackTrace();
        }
    }

}
