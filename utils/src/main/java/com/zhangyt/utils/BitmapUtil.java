package com.zhangyt.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.text.TextUtils;
import android.util.Log;


import com.guo.android_extend.image.ImageConverter;
import com.libyuv.util.TRYuvUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";
    public static int WIDTH = 640;
    public static int HEIGHT = 480;

    public static int MIU_WIDTH = 320;
    public static int MIU_HEIGHT = 240;

    public interface OnSaveImgListener {
        void onSuccess(String filePath);

        void onImageData(byte[] image);

        void onFiled(String meaasge);
    }


    private static int dstHeight = 240;
    private static int dstWidth = 320;

    public static void rotateYuv(final byte[] data, int width, int height,
                                 final boolean isMirror, final float angel, final int quality, final int smallQuality,
                                 final OnSaveImgListener listener) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                byte[] dstDataNV21 = new byte[width * height * 3 / 2];
                TRYuvUtil.yuvI420ToNV21(data, dstDataNV21, width, height);
                Bitmap bitmap = yuvToRGB(dstDataNV21, width, height, smallQuality);
                Log.d(TAG, "bitmapWidth: " + bitmap.getWidth() + "    bitmapHeight: " + bitmap.getHeight());
                byte[] imageData;
                if (isMirror) {
                    Bitmap mirror = rotaingImageView((int) (360 - angel), bitmap, -1F, 1F);
                    imageData = bitmapToByte(mirror, quality);
                } else {
                    Bitmap rotate = BitmapUtil.rotateBitmap((int) (360 - angel), bitmap);
                    imageData = bitmapToByte(rotate, quality);
                }

                listener.onImageData(imageData);
            }
        };
        ThreadPoolUtil.getIOExecutor().execute(saveRunnable);
    }

    //NV21: YYYY VUVU
    public static byte[] NV21_mirror(byte[] nv21_data, int width, int height) {
        long t1 = System.currentTimeMillis();
        int i;
        int left, right;
        byte temp;
        int startPos = 0;

        // mirror Y
        for (i = 0; i < height; i++) {
            left = startPos;
            right = startPos + width - 1;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }


        // mirror U and V
        int offset = width * height;
        startPos = 0;
        for (i = 0; i < height / 2; i++) {
            left = offset + startPos;
            right = offset + startPos + width - 2;
            while (left < right) {
                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;

                temp = nv21_data[left];
                nv21_data[left] = nv21_data[right];
                nv21_data[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
        long t2 = System.currentTimeMillis();
        Log.e(TAG, "NV21_mirror time: " + (t2 - t1) + " ms " + "width:" + width + "height:" + height);
        return nv21_data;
    }

    public static void saveI420ToJpgFile(byte[] I420, int width, int height, String path, int rotation, boolean isMirror) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (BitmapUtil.class) {
                    byte[] Nv21 = new byte[width * height * 3 / 2];
                    TRYuvUtil.yuvI420ToNV21(I420, Nv21, width, height);
                    Bitmap bitmap = nv21ToBitmap(Nv21, rotation, isMirror, width, height);
                    String path1 = saveLdToInternalStorage(new File(path), bitmap, 100);
                    Log.e(TAG, "saveI420ToJpgFile time: path=" + path1);
                }
            }
        };
        ThreadPoolUtil.getIOExecutor().execute(runnable);

    }

    public static void saveNv21ToJpgFile(byte[] Nv21, int width, int height, String path, int rotation, boolean isMirror) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (BitmapUtil.class) {
                    Bitmap bitmap = nv21ToBitmap(Nv21, rotation, isMirror, width, height);
                    String path1 = saveLdToInternalStorage(new File(path), bitmap, 100);
                    Log.e(TAG, "saveNv21ToJpgFile time: path=" + path1);
                }
            }
        };
        ThreadPoolUtil.getIOExecutor().execute(runnable);

    }

    public static void saveNv21ToJpgFile(byte[] Nv21, int width, int height, String path, int rotation, boolean isMirror, OnSaveImgListener listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (BitmapUtil.class) {
                    Bitmap bitmap = nv21ToBitmap(Nv21, rotation, isMirror, width, height);
                    listener.onImageData(bitmapToByte(bitmap));
                    saveLdToInternalStorage(new File(path), bitmap, 100);
                }
            }
        };
        ThreadPoolUtil.getIOExecutor().execute(runnable);

    }

    public static void saveNV21Image(byte[] Nv21, int width, int height, String path) {
        File imageFile = new File(path);
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                YuvImage yuvimage = new YuvImage(Nv21, ImageFormat.NV21, width,
                        height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, width,
                        height), 100, baos);
                try {
                    FileOutputStream outputStream = new FileOutputStream(path);
//                    BufferedOutputStream outputStream =new BufferedOutputStream(fileOutput);
                    outputStream.write(baos.toByteArray());//baos.toByteArray()  cacheData
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
//        Executors.newScheduledThreadPool(2).execute(saveRunnable);
        ThreadPoolUtil.getIOExecutor().execute(saveRunnable);
    }


    /**
     * @param path     保存路径
     * @param data     图片yuv  i420
     * @param isMirror 是否需要镜像
     * @param angel    旋转角度
     * @param listener
     */
    public static void saveYuvTpJpg(final String path, final byte[] data, int width, int height,
                                    final boolean isMirror, final float angel, final int quality, final int smallQuality,
                                    final OnSaveImgListener listener) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                LogManager.e(TAG, "saveImage Path:" + path);
                String srcpath = null;

                byte[] dstDataNV21 = new byte[width * height * 3 / 2];
                TRYuvUtil.yuvI420ToNV21(data, dstDataNV21, width, height);

                Bitmap bitmap = yuvToRGB(dstDataNV21, width, height, smallQuality);
                Log.d(TAG, "bitmapWidth: " + bitmap.getWidth() + "    bitmapHeight: " + bitmap.getHeight());
                File imageFile = new File(path);
                byte[] imageData;
                if (isMirror) {
                    Bitmap mirror = rotaingImageView((int) (360 - angel), bitmap, -1F, 1F);
                    imageData = bitmapToByte(mirror);
                    srcpath = BitmapUtil.saveToInternalStorage(imageFile, mirror, quality);
                } else {
                    Bitmap rotate = BitmapUtil.rotateBitmap((int) (360 - angel), bitmap);
                    imageData = bitmapToByte(rotate);
                    srcpath = BitmapUtil.saveToInternalStorage(imageFile, rotate, quality);
                }

                listener.onImageData(imageData);
                if (TextUtils.isEmpty(srcpath)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(srcpath);
                }
            }
        };
        ThreadPoolUtil.getIOExecutor().execute(saveRunnable);
    }

    /**
     * 将argb_8888的图像转化为w*h*4的字节数组，算法使用
     */
    public static byte[] bitmap2byte(final Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();
        return bytes;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static byte[] bitmapToByte(Bitmap bitmap, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 将yuv格式的byte数组转化成RGB的bitmap
     */
    public static Bitmap yuvToRGB(byte[] data, boolean ocr, String ratio, int smallQuality) {
        int width = WIDTH;
        int height = HEIGHT;
        int quality = 100;
        if (ratio.equals("16:9")) {
            width = 852;
            height = 480;//16:9无法在此处直接设置好小图宽高，因为原图是640*360
            if (!ocr) {
                width = 426;
                height = 240;
            }
        } else {//4:3 在此处可以直接设置好小图宽高,通过运动检测后，图被压缩成了320*240
            if (!ocr) {
                width = 320;
                height = 240;
            }
        }
        if (!ocr) {
            if (smallQuality > 0 && smallQuality <= 100)
                quality = smallQuality;
        }
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), quality, baos);
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
    }


    /**
     * 将yuv格式的byte数组转化成RGB的bitmap
     */
    public static Bitmap yuvToRGB(byte[] data, int width, int height, int smallQuality) {
        int quality = 100;
        if (height == 240)//如果高是240，则ocrFlag = 0，需要小图
            quality = smallQuality;
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), quality, baos);
        Bitmap mBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
        return mBitmap;
    }

    public static Bitmap bitMapScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        bitmap = null;
        return resizeBmp;
    }

    /**
     * 保存Yuv数据到Jpg文件中
     * 暂时没有清理机制，考虑如果将来图片过大，可能需要清理一下。
     *
     * @param data
     * @param destPath
     */
    public static void saveYuvToJpgFile(byte[] data, int width, int height, String destPath) {
        File imageFile = new File(destPath);
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveDataToFile(byte[] bytes, String destPath) {
        ThreadPoolUtil.getIOExecutor().execute(new Runnable() {
            @Override
            public void run() {
                File imageFile = new File(destPath);
                if (!imageFile.getParentFile().exists()) {
                    imageFile.getParentFile().mkdirs();
                }
                if (!imageFile.exists()) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static byte[] readYUV(String destPath) {
        if (TextUtils.isEmpty(destPath) || !new File(destPath).exists()) {
            return null;
        }
        FileInputStream in = null;
        byte[] buffer = null;

        try {
            in = new FileInputStream(destPath);
            LogManager.e("YuvReaderActivityp", "readfile " + in.available() / 1024);
            buffer = new byte[in.available()];//in.available() 表示要读取的文件中的数据长度
            in.read(buffer);  //将文件中的数据读到buffer中
            //最后记得，关闭流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer;
    }

    /**
     * 将图片按照固定比例进行压缩
     */
    public static Bitmap resizeBitmapWithConstantWHRatio(Bitmap bmp, int mWidth, int mHeight) {
        if (bmp != null) {
            Bitmap bitmap = bmp;
            float width = bitmap.getWidth(); //728
            float height = bitmap.getHeight(); //480
            Log.d(TAG, "----原图片的宽度:" + bmp.getWidth() + ", 高度:" + bmp.getHeight()); //720/480 = 1.5

            float scale = 1.0f;
            float scaleX = (float) mWidth / width;
            float scaleY = (float) mHeight / height;
            if (scaleX < scaleY && (scaleX > 0 || scaleY > 0)) {
                scale = scaleX;
            }
            if (scaleY <= scaleX && (scaleX > 0 || scaleY > 0)) {
                scale = scaleY;
            }

            return resizeBitmapByScale(bmp, scale);
        }
        return null;
    }


    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        try {
            if (bitmap == null) {
                return null;
            }
            Bitmap BitmapOrg = bitmap;
            int width = BitmapOrg.getWidth();
            int height = BitmapOrg.getHeight();
            int newWidth = w;
            int newHeight = h;

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // if you want to rotate the Bitmap
            // matrix.postRotate(45);
            Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        float width = bitmap.getWidth(); //728
        float height = bitmap.getHeight(); //480
        Bitmap bmpOut = Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
        return bmpOut;
    }

    /***将bitmap写进指定路径*/
    public static String saveToInternalStorage(File imageFile, Bitmap bitmapImage, int quality) {
        String path = "";
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            path = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bitmapImage.recycle();
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path; //得到.jpg的全路径名
    }

    /**
     * NV21格式图片转bitmap
     *
     * @param dstDataNV21 数据源
     * @param imgAngle    旋转角度
     * @param isMirror    是否镜像
     * @param dstWidth    图片宽
     * @param dstHeight   图片高
     * @return
     */
    public static Bitmap nv21ToBitmap(byte[] dstDataNV21, int imgAngle, boolean isMirror, int dstWidth, int dstHeight) {
        Bitmap bitmap = null;
        try {
            YuvImage yuvImage = new YuvImage(dstDataNV21, ImageFormat.NV21, dstWidth, dstHeight, null);
            ByteArrayOutputStream fOut = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, dstWidth, dstHeight), 100, fOut);

            //将byte生成bitmap
            byte[] bitData = fOut.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bitData, 0, bitData.length);
            if (isMirror) {
                bitmap = BitmapUtil.rotaingImageView(360 - imgAngle, bitmap, -1F, 1F);
            } else {
                bitmap = (imgAngle != 0) ? BitmapUtil.rotateBitmap(360 - imgAngle, bitmap) : bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap i420ToBitmap(byte[] i420Data, int imgAngle, boolean isMirror, int dstWidth, int dstHeight) {
        byte[] dstDataNV21 = new byte[dstHeight * dstWidth * 3 / 2];
        TRYuvUtil.yuvI420ToNV21(i420Data, dstDataNV21, dstWidth, dstHeight);
        YuvImage yuvImage = new YuvImage(i420Data, ImageFormat.YUV_420_888, dstWidth, dstHeight, null);
        ByteArrayOutputStream fOut = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, dstWidth, dstHeight), 100, fOut);

        //将byte生成bitmap
        byte[] bitData = fOut.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitData, 0, bitData.length);
        if (isMirror) {
            bitmap = BitmapUtil.rotaingImageView(360 - imgAngle, bitmap, -1F, 1F);
        } else {
            bitmap = (imgAngle != 0) ? BitmapUtil.rotateBitmap(360 - imgAngle, bitmap) : bitmap;
        }
        return bitmap;
    }

    public static byte[] dealRgbData(byte[] data, int rotation, boolean isMirror, int quality) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (isMirror) {
            bitmap = BitmapUtil.rotaingImageView(360 - rotation, bitmap, -1F, 1F);
        } else {
            bitmap = (rotation != 0) ? BitmapUtil.rotateBitmap(360 - rotation, bitmap) : bitmap;
        }
        return bitmap2Bytes(bitmap, quality);
    }

    /**
     * bitmap 转文件
     */
    public static String saveLdToInternalStorage(File imageFile, Bitmap bitmapImage, int quality) {
        String path = "";
        if (bitmapImage == null || bitmapImage.isRecycled()) {
            return "";
        }
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            path = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bitmapImage.recycle();
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path; //得到.jpg的全路径名
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, int quality) {
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public static byte[] bitmapToRGBABytes(Bitmap bitmap, int quality) {
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public static byte[] imgToNV21(String imagePath) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(imagePath);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();//bitmap容易出现内存泄漏，加个异常捕获
        }
        if (bmp == null) {
            LogManager.d(TAG, "Bitmap error");
            return null;
        }

        byte[] mImageNV21 = new byte[bmp.getWidth() * bmp.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        try {
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, mImageNV21)) {
                LogManager.d(TAG, "convert ok!");
            } else {
                LogManager.d(TAG, "convert error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回收内存
            if (!bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
            convert.destroy();
            System.gc();
        }
        return mImageNV21;
    }

    /**
     * bitmap转化为byte[]数组，网络传输使用
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        return bitmap2Bytes(bitmap, 30);
    }

    /**
     * sx, sy -1, 1   左右翻转   1, -1  上下翻转
     */
    public static Bitmap rotaingImageView(int angle, Bitmap srcBitmap, float sx, float sy) {
        LogManager.e(TAG, "rotation:" + angle);
        Bitmap retBitmap = null;
        try {
            Matrix matrix = new Matrix();  //使用矩阵 完成图像变换
            if (sx != 0 || sy != 0) {
                matrix.postScale(sx, sy);  //重点代码，记住就ok
            }

            int w = srcBitmap.getWidth();
            int h = srcBitmap.getHeight();
            Bitmap cacheBitmap = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
//        }
            Canvas canvas = new Canvas(cacheBitmap);  //使用canvas在bitmap上面画像素

            matrix.postRotate(angle);
            retBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, matrix, true);
            canvas.drawBitmap(retBitmap, new Rect(0, 0, w, h), new Rect(0, 0, w, h), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retBitmap;
    }

    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Bitmap bitmap1 = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap1;
    }

    public static void cutFingerImage(final String path, final String savePath, final float startX, final float startY, final float width, final float height, final OnSaveImgListener listener) {
        (new Thread((Runnable) (new Runnable() {
            public final void run() {
                File file = new File(path);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) fis);
                if (bitmap == null) {
                    listener.onFiled("bitmap is null ！");
                    return;
                }
                int resizestartX = (int) startX;
                if (resizestartX < 0) {
                    resizestartX = 0;
                }

                if (resizestartX > bitmap.getWidth()) {
                    resizestartX = 0;
                }

                int resizestartY = (int) startY;
                if (resizestartY < 0) {
                    resizestartY = 0;
                }

                if (resizestartY > bitmap.getHeight()) {
                    resizestartY = 0;
                }

                int resizeWidth = (int) width;
                if ((int) startX + (int) width > bitmap.getWidth()) {
                    resizeWidth = bitmap.getWidth() - (int) startX;
                }

                int resizeHeight = (int) height;
                if ((int) startY + (int) height > bitmap.getHeight()) {
                    resizeHeight = bitmap.getHeight() - (int) startY;
                }
                if (resizeWidth <= 0 || resizeHeight <= 0) {
                    listener.onFiled("Picture save failed ！");
                    return;
                }
                Bitmap cutBitMap = Bitmap.createBitmap(bitmap, resizestartX, resizestartY, resizeWidth, resizeHeight, (Matrix) null, false);
                bitmap.recycle();
                File saveFile = new File(savePath);
                String result = saveLdToInternalStorage(saveFile, cutBitMap, 100);
                if (TextUtils.isEmpty((CharSequence) result)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(result);
                }
            }
        }))).start();
    }


    //使用Bitmap 进行操作 不使用文件 file 的形式
    public static void cutFingerImage(final Bitmap bitmap, final String savePath, final float startX, final float startY, final float width, final float height, final OnSaveImgListener listener) {
        (new Thread((Runnable) (new Runnable() {
            public final void run() {
                if (bitmap == null) {
                    listener.onFiled("bitmap is null ！");
                    return;
                }
                int resizestartX = (int) startX;
                if (resizestartX < 0) {
                    resizestartX = 0;
                }

                if (resizestartX > bitmap.getWidth()) {
                    resizestartX = 0;
                }

                int resizestartY = (int) startY;
                if (resizestartY < 0) {
                    resizestartY = 0;
                }

                if (resizestartY > bitmap.getHeight()) {
                    resizestartY = 0;
                }

                int resizeWidth = (int) width;
                if ((int) startX + (int) width > bitmap.getWidth()) {
                    resizeWidth = bitmap.getWidth() - (int) startX;
                }

                int resizeHeight = (int) height;
                if ((int) startY + (int) height > bitmap.getHeight()) {
                    resizeHeight = bitmap.getHeight() - (int) startY;
                }
                if (resizeWidth <= 0 || resizeHeight <= 0) {
                    listener.onFiled("Picture save failed ！");
                    return;
                }
                Bitmap cutBitMap = Bitmap.createBitmap(bitmap, resizestartX, resizestartY, resizeWidth, resizeHeight, (Matrix) null, false);
                bitmap.recycle();
                File saveFile = new File(savePath);
                String result = saveLdToInternalStorage(saveFile, cutBitMap, 100);
                if (TextUtils.isEmpty((CharSequence) result)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(result);
                }
            }
        }))).start();
    }


    //裁剪指尖高清图 进行操作 先保存成Bitmap 再根据坐标进行裁剪
    public static void cutFingerImage(final byte[] byteData, final String savePath, final float startX, final float startY, final float width, final float height, final OnSaveImgListener listener) {
        (new Thread((Runnable) (new Runnable() {
            public final void run() {

                long time1 = System.currentTimeMillis();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length, options);
                long time2 = System.currentTimeMillis();
                Log.e("xiaopeng===", "ms" + (time2 - time1) + "===" + bitmap.getHeight() + "===" + bitmap.getWidth());
                int mAngel = 180;
                if (mAngel == 0 || mAngel == 360) {
                } else {
                    bitmap = BitmapUtil.rotateBitmap(mAngel, bitmap);
                }
                if (bitmap == null) {
                    listener.onFiled("bitmap is null ！");
                    return;
                }
                int resizestartX = (int) startX;
                if (resizestartX < 0) {
                    resizestartX = 0;
                }

                if (resizestartX > bitmap.getWidth()) {
                    resizestartX = 0;
                }

                int resizestartY = (int) startY;
                if (resizestartY < 0) {
                    resizestartY = 0;
                }

                if (resizestartY > bitmap.getHeight()) {
                    resizestartY = 0;
                }

                int resizeWidth = (int) width;
                if ((int) startX + (int) width > bitmap.getWidth()) {
                    resizeWidth = bitmap.getWidth() - (int) startX;
                }

                int resizeHeight = (int) height;
                if ((int) startY + (int) height > bitmap.getHeight()) {
                    resizeHeight = bitmap.getHeight() - (int) startY;
                }
                if (resizeWidth <= 0 || resizeHeight <= 0) {
                    listener.onFiled("Picture save failed ！");
                    return;
                }
                Bitmap cutBitMap = Bitmap.createBitmap(bitmap, resizestartX, resizestartY, resizeWidth, resizeHeight, (Matrix) null, false);
                bitmap.recycle();
                File saveFile = new File(savePath);
                String result = saveLdToInternalStorage(saveFile, cutBitMap, 100);
                if (TextUtils.isEmpty((CharSequence) result)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(result);
                }
            }
        }))).start();
    }


    //裁剪指尖高清图 进行YUV数据高效处理
    public static void cutFingerImageYuvData1(final byte[] byteData, final String savePath, final float startX, final float startY, final float width, final float height, final OnSaveImgListener listener) {
        (new Thread((Runnable) (new Runnable() {
            public final void run() {
                long time1 = System.currentTimeMillis();

                YuvImage yuvImage = new YuvImage(byteData, ImageFormat.NV21, 1920, 1080, null);
                ByteArrayOutputStream fOut = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, 1920, 1080), 100, fOut);
                //将byte生成bitmap
                byte[] bitData = fOut.toByteArray();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bitData, 0, bitData.length);
                Bitmap cutBitMap = null;
                int resizestartX = (int) startX;
                if (resizestartX < 0) {
                    resizestartX = 0;
                }

                if (resizestartX > 1920) {
                    resizestartX = 0;
                }

                int resizestartY = (int) startY;
                if (resizestartY < 0) {
                    resizestartY = 0;
                }

                if (resizestartY > 1080) {
                    resizestartY = 0;
                }

                int resizeWidth = (int) width;
                if ((int) startX + (int) width > 1920) {
                    resizeWidth = 1920 - (int) startX;
                }

                int resizeHeight = (int) height;
                if ((int) startY + (int) height > 1080) {
                    resizeHeight = 1080 - (int) startY;
                }
                if (resizeWidth <= 0 || resizeHeight <= 0) {
                    listener.onFiled("Picture save failed ！");
                    return;
                }
                long time2 = System.currentTimeMillis();
                Log.e("xiaopeng===", "耗时：" + (time2 - time1) + "w:" + width + "h:" + height + "x:" + startX + "y:" + startY);
                int mAngel = 180;
                if (mAngel == 0 || mAngel == 360) {
                } else {
                    if (mAngel == 180) {
                        resizestartX = 1920 - resizestartX - resizeWidth;
                        resizestartY = 1080 - resizestartY - resizeHeight;
                        cutBitMap = Bitmap.createBitmap(bitmap, resizestartX, resizestartY, resizeWidth, resizeHeight, (Matrix) null, false);
                        long time3 = System.currentTimeMillis();
                        cutBitMap = BitmapUtil.rotateBitmap(mAngel, cutBitMap);
                        long time4 = System.currentTimeMillis();
                        Log.e("xiaopeng===43", "ms" + (time4 - time3) + "===" + bitmap.getHeight() + "===" + bitmap.getWidth());
                    }

                }
                bitmap.recycle();
                File saveFile = new File(savePath);
                String result = saveLdToInternalStorage(saveFile, cutBitMap, 100);
                cutBitMap.recycle();
                if (TextUtils.isEmpty((CharSequence) result)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(result);
                }
            }
        }))).start();
    }


    public static void cutFingerImage2(final byte[] byteData, final String savePath, final float startX, final float startY, final float width, final float height, final OnSaveImgListener listener) {
        (new Thread((Runnable) (new Runnable() {
            public final void run() {

                long time1 = System.currentTimeMillis();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length, options);
                Bitmap cutBitMap = null;
                long time2 = System.currentTimeMillis();
                Log.e("xiaopeng===43", "ms" + (time2 - time1) + "===" + bitmap.getHeight() + "===" + bitmap.getWidth());
                if (bitmap == null) {
                    listener.onFiled("bitmap is null ！");
                    return;
                }
                int resizestartX = (int) startX;
                if (resizestartX < 0) {
                    resizestartX = 0;
                }

                if (resizestartX > bitmap.getWidth()) {
                    resizestartX = 0;
                }

                int resizestartY = (int) startY;
                if (resizestartY < 0) {
                    resizestartY = 0;
                }

                if (resizestartY > bitmap.getHeight()) {
                    resizestartY = 0;
                }

                int resizeWidth = (int) width;
                if ((int) startX + (int) width > bitmap.getWidth()) {
                    resizeWidth = bitmap.getWidth() - (int) startX;
                }

                int resizeHeight = (int) height;
                if ((int) startY + (int) height > bitmap.getHeight()) {
                    resizeHeight = bitmap.getHeight() - (int) startY;
                }
                if (resizeWidth <= 0 || resizeHeight <= 0) {
                    listener.onFiled("Picture save failed ！");
                    return;
                }
                int mAngel = 180;
                if (mAngel == 0 || mAngel == 360) {
                } else {
                    if (mAngel == 180) {
                        resizestartX = bitmap.getWidth() - resizestartX - resizeWidth;
                        resizestartY = bitmap.getHeight() - resizestartY - resizeHeight;
                        cutBitMap = Bitmap.createBitmap(bitmap, resizestartX, resizestartY, resizeWidth, resizeHeight, (Matrix) null, false);
                        long time3 = System.currentTimeMillis();
                        cutBitMap = BitmapUtil.rotateBitmap(mAngel, cutBitMap);
                        long time4 = System.currentTimeMillis();
                        Log.e("xiaopeng===43", "ms" + (time4 - time3) + "===" + bitmap.getHeight() + "===" + bitmap.getWidth());
                    }

                }
                bitmap.recycle();
                File saveFile = new File(savePath);
                String result = saveLdToInternalStorage(saveFile, cutBitMap, 100);
                if (TextUtils.isEmpty((CharSequence) result)) {
                    listener.onFiled("Picture save failed ！");
                } else {
                    listener.onSuccess(result);
                }
            }
        }))).start();
    }


}
