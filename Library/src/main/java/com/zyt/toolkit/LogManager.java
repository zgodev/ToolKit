package com.zyt.toolkit;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private static Boolean MYLOG_SWITCH = true;  // 日志文件总开关

    private static Boolean MYLOG_TYPE_V = true;
    private static Boolean MYLOG_TYPE_D = true;
    private static Boolean MYLOG_TYPE_I = true;
    private static Boolean MYLOG_TYPE_W = true;
    private static Boolean MYLOG_TYPE_E = true;
    private static Boolean MYLOG_TYPE_M = true;  //详细日志输出，可调试时打开输出跟多日志

    private static Boolean MYLOG_ALL = false;
    private static Boolean MYLOG_MIDD_TRANS_LEVEL = true;//级别总开关

    private static String MYLOG_TYPE_NAME_V = "my_log_type_v";
    private static String MYLOG_TYPE_NAME_D = "my_log_type_d";
    private static String MYLOG_TYPE_NAME_I = "my_log_type_i";
    private static String MYLOG_TYPE_NAME_W = "my_log_type_w";
    private static String MYLOG_TYPE_NAME_E = "my_log_type_e";

    private static String MYLOG_ALL_NAME = "my_log_all";
    private static String MYLOG_APP_LEVEL_NAME = "my_log_app_level";
    private static String MYLOG_MIDD_TRANS_LEVEL_NAME = "my_log_midd_trans_level";

    private static Context context;

    private static boolean isWrite = false;//是否把日志写进本地文件
    public static void setDebug(boolean isDebug){
        MYLOG_SWITCH = isDebug;
    }
    public static void setWriteFile(boolean isWriteFile){
        isWrite = isWriteFile;
    }
    //for coolpad9970 log,all log change to log.e
    public static void logW(String tag, Object msg) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, msg.toString(), 'w');
                //log(tag, msg.toString(), 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void logE(String tag, Object msg) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, msg.toString(), 'e');
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }

    public static void MiddAndTransD(String tag, Object msg) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, msg.toString(), 'd');
                //log(tag, msg.toString(), 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void MiddAndTransI(String tag, Object msg) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, msg.toString(), 'i');
                //log(tag, msg.toString(), 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void MiddAndTransV(String tag, Object msg) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, msg.toString(), 'v');
                //log(tag, msg.toString(), 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void logW(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'w');
                //log(tag, text, 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void logE(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'e');
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }

    public static void MiddAndTransD(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'd');
                //log(tag, text, 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void MiddAndTransM(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'm');
                //log(tag, text, 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void MiddAndTransI(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'i');
                //log(tag, text, 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void MiddAndTransV(String tag, String text) {
        if (MYLOG_MIDD_TRANS_LEVEL)
            try {
                log(tag, text, 'v');
                //log(tag, text, 'e');
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @throws IOException
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) throws IOException {
        /*if (isWrite) {
            LogWriter writer = LogWriter.open();
            if (writer != null) {
                writer.print(tag + " " + msg);
                writer.close();
            }
        }
*/
        if (MYLOG_SWITCH) {

            if ((MYLOG_TYPE_V || MYLOG_ALL) && level == 'v')
                Log.v(tag, msg);

            if ((MYLOG_TYPE_D || MYLOG_ALL) && level == 'd')
                Log.d(tag, msg);

            if ((MYLOG_TYPE_I || MYLOG_ALL) && level == 'i')
                Log.i(tag, msg);

            if ((MYLOG_TYPE_W || MYLOG_ALL) && level == 'w')
                Log.w(tag, msg);

            if ((MYLOG_TYPE_E || MYLOG_ALL) && level == 'e')
                Log.e(tag, msg);

            if ((MYLOG_TYPE_M || MYLOG_ALL) && level == 'm')
                Log.d(tag, msg);
        }
        if (isOpenFileLog()) {
            if (df!=null) {
                String time = df.format(new Date());
                writeLog(time + "[" + tag + "]:" + msg + "\r\n");
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context mcontext) {
        context = mcontext;
    }

    static String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "AndroidLog/log.txt";
    
    static File file = new File(path);

    static BufferedWriter writer;
    static SimpleDateFormat df;
    static BufferedReader reader;
//    static String firstLine = "";

    private static boolean isOpenFileLog() {
        if (file.exists()) {
            try {
                if (writer == null)
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                if (reader == null) {
                    reader = new BufferedReader(new FileReader(file));
//                    firstLine = reader.readLine();
//                    if (firstLine != null)
//                        firstLine = firstLine.trim();
                }
                if (df == null)
                    df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]:");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    private static void writeLog(String log) {

        try {
            /*if (!"HB_SDK_LogHB_SDK_LogHB_SDK_Log".equals(firstLine)) {
                return;
            }*/
            if (writer == null) {
                return;
            }
            writer.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeWrite() {
        if (writer != null) {
            try {
                writer.close();
                writer=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
