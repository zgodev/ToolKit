package com.zhangyt.utils;




import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WriteLog {
    private static String TAG = "WriteLog";
    public static String DIR = "";
    private static String logfile = "";
    private static String logtimefile = "";
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");// 日志的输出格式

    public static boolean isWrite = false;

    private static final ExecutorService THREADS = Executors.newSingleThreadExecutor();

    public static void init(String path, String name, boolean _isWrite) {
        isWrite = _isWrite;
        DIR = path;
        logfile = DIR + name;
        logtimefile = DIR + "time_" + name;
        if(isWrite){
            createDir();
        }
    }

    public static void writeLogFile(final String data) {
        if(!isWrite) return;
        THREADS.execute(new Runnable() {
            @Override
            public void run() {
                writeFile(logfile, data);
            }
        });
    }
    public static void writeTiemFile(final String data) {
        if(!isWrite) return;
        THREADS.execute(new Runnable() {
            @Override
            public void run() {
                writeTimeFile(logtimefile, data);
            }
        });
    }
    private static void writeTimeFile(String fileName, String data) {
        if (!createDir()) {
            Log.e(TAG, "dir doesn't exists");
            return;
        }
        if (data == null) {
            Log.e(TAG, "write " + fileName + " failed: cause data is null");
            return;
        }
        File file = new File(fileName);
        BufferedWriter bos = null;
        try {
            FileWriter fos = new FileWriter(file, true);
            bos = new BufferedWriter(fos);
            bos.write("\t" + data);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void deleteLogFile() {
        deleteFile(logfile);
    }


    private static boolean createDir() {
        File dir = new File(DIR);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            Log.i(TAG, "make turing dir: " + mkdirs);
            return mkdirs;
        } else {
            return true;
        }
    }

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            boolean del = file.delete();
            Log.i(TAG, "delete " + fileName + " : " + del);
        }
    }
    private static void writeFile(String fileName, String data) {
        if (!createDir()) {
            Log.e(TAG, "dir doesn't exists");
            return;
        }
        if (data == null) {
            Log.e(TAG, "write " + fileName + " failed: cause data is null");
            return;
        }
        File file = new File(fileName);
        if (file.length()>20*1024*1024){//最大缓存20M
            file.delete();
            Log.e(TAG,"日志文件超限制,Size:"+ file.length() +"，删除日志文件"+fileName);
        }
        BufferedWriter bos = null;
        try {
            FileWriter fos = new FileWriter(file, true);
            bos = new BufferedWriter(fos);
            Date nowtime = new Date();
            String needWriteFiel = myLogSdf.format(nowtime);
            bos.write(needWriteFiel + "\t" + data);
            bos.write("\r\n");
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void writeFile(String fileName, byte[] data) {
        if (!createDir()) {
            Log.e(TAG, "dir doesn't exists");
            return;
        }
        if (data == null) {
            Log.e(TAG, "write " + fileName + " failed: cause data is null");
            return;
        }
        File file = new File(fileName);
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
