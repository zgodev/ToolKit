package com.zhangyt.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    static List<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, "empty path");
            return null;
        }
        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            s.add(files[i].getName());
        }
        return s;
    }


    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                new File(newPath).createNewFile();
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[2048];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject loadJSONFromAsset(Context context, String jsonFile) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeDataToFile(String fileName, byte[] bytes) throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }


    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];

        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

    }

    public static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = assetManager.open(fromAssetPath);
            (new File(toPath)).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 5];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static void copyFileOrDir(AssetManager assetManager, String path, String toPath) {
        String[] assets = null;

        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyAsset(assetManager, path, toPath + "/" + path);
            } else {
                String dirStr = null;
                if (toPath.endsWith(File.separator)) {
                    dirStr = toPath + path;
                } else {
                    dirStr = toPath + File.separator + path;
                }

                File dir = new File(dirStr);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                for (int i = 0; i < assets.length; ++i) {
                    copyFileOrDir(assetManager, path + "/" + assets[i], dirStr);
                }
            }
        } catch (IOException var7) {
            Log.e("tag", "I/O Exception", var7);
        }
    }

    /**
     * RGB转Bitmap
     *
     * @param bytes
     * @param width
     * @param height
     * @return
     */
    public static Bitmap RGB2Bitmap(byte[] bytes, int width, int height) {
        Bitmap stitchBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Bitmap stitchBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < width * height; i++) {
            byte b1 = bytes[i * 3 + 0];
            byte b2 = bytes[i * 3 + 1];
            byte b3 = bytes[i * 3 + 2];
            // set value
            rgba[i * 4 + 0] = b1;
            rgba[i * 4 + 1] = b2;
            rgba[i * 4 + 2] = b3;
            rgba[i * 4 + 3] = (byte) 255;
        }
        stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(rgba));
        return stitchBmp;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
    /**
     * 从文本文件中读出内容
     *
     * @param fileDir  文件路径
     * @param fileName 文件名称
     * @return 文件中的字符串内容
     */
    public static String readFile(String fileDir, String fileName) {
        String string = "";
        String filePath = "";
        try {
            filePath = fileDir + File.separator + fileName;
            FileInputStream in = new FileInputStream(filePath);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            in.close();
            string = outStream.toString();
        } catch (Exception e) {
            string = "";
        }
        return string;
    }

    /**
     * 从文件中读取内容
     *
     * @param filePath 文件路径
     * @return 文件中的字符串
     */
    public static String readFile(String filePath) {
        String content = "";
        try {
            FileInputStream in = new FileInputStream(filePath);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            content = outStream.toString("UTF-8");
            outStream.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static boolean fileIsExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static byte[] readFileToBytes(String path) {
        byte[] data = new byte[0];
        try {
            FileInputStream in = new FileInputStream(path);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            data = outStream.toByteArray();
            outStream.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getFormat(String url) {
        int start = url.lastIndexOf(".");
        String format = url.substring(start);
        return format;
    }

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 写入文本到文件
     *
     * @param path 文件路径
     */
    public static void writeFile(String path, String content) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, false);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void write(String path, String data) {

        try {
            FileWriter write = new FileWriter(path, true);

            BufferedWriter bufferedWriter = new BufferedWriter(write);

            bufferedWriter.write(data);
            bufferedWriter.newLine();//换行

            bufferedWriter.flush();
            write.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 将文件转为byte[]
     *
     * @param filePath 文件路径
     * @return
     */
    public static byte[] getBytes(String filePath) {
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        byte[] temp = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, i);
            }
            temp = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }


    /**
     * 创建新文件
     */
    public static boolean createNewFile(File file) throws IOException {
        boolean result = true;
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            result = file.createNewFile();
        } catch (IOException e) {
            throw e;
        }
        return result;
    }


    public static long getAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            availableBytes = stat.getAvailableBytes();
        } else {
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            availableBytes = totalBlocks * blockSize;
        }
        return availableBytes;
    }

    public static long getFolderSize(File file) {
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filePath
     * @return
     */
    public static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static String save(byte[] pathData, String dirPath, String fileName) {
        try {
            if (pathData != null && pathData.length > 1) {
                File dir = new File(dirPath);
                File file = null;
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (pathData != null) {
                    file = new File(dir, fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    OutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(pathData);
                    outputStream.close();
                }

                return file.getAbsolutePath();
            } else {
                return null;
            }
        } catch (Exception var9) {
            Log.e("BitmapTool", "save error is " + var9.toString());
            var9.printStackTrace();
            return null;
        }
    }
}
