package com.zyt.toolkit;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

}
