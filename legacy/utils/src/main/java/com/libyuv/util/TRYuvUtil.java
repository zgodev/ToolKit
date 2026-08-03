package com.libyuv.util;


/**
 * 作者：请叫我百米冲刺 on 2017/8/28 上午11:05
 * 邮箱：mail@hezhilin.cc
 */

public class TRYuvUtil {

    static {
        System.loadLibrary("myimg");
    }

    /**
     * 初始化
     *
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     **/
//    public static native void init(int width, int height, int dst_width, int dst_height);


    /**
     * YUV数据的基本的处理
     *
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param mode       压缩模式。这里为0，1，2，3 速度由快到慢，质量由低到高，一般用0就好了，因为0的速度最快
     * @param degree     旋转的角度，90，180和270三种
     * @param isMirror   是否镜像，一般只有270的时候才需要镜像
     **/
//    public static native void compressYUV(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int mode, int degree, boolean isMirror);

    /**
     * yuv数据的裁剪操作
     *
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param left       裁剪的x的开始位置，必须为偶数，否则显示会有问题
     * @param top        裁剪的y的开始位置，必须为偶数，否则显示会有问题
     **/
    public static native void cropYUV(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int left, int top);

    /**
     * yuv数据的裁剪前的坐标转换操作
     *
     * @param srcWidth  原始的宽
     * @param srcHeight 原始的高
     * @param inputRect 坐标信息
     *                  //     * @param x        起始点x
     *                  //     * @param y  起始点y
     *                  //     * @param w 宽
     *                  //     * @param h  高
     **/
//    public static int[] cropPointTrans(int srcWidth, int srcHeight, int x, int y, int w, int h){
    public static int[] cropPointTrans(int srcWidth, int srcHeight, int[] inputRect) {
        int x = inputRect[0];
        int y = inputRect[1];
        int w = inputRect[2];
        int h = inputRect[3];
        if (x < 0) x = 0;
        if (x >= srcWidth) x = srcWidth;// 可以返回异常
        if (y < 0) y = 0;
        if (y >= srcHeight) y = srcHeight;// 可以返回异常
        x = (x / 2) * 2; // 偶数转换
        y = (y / 2) * 2; // 偶数转换
        if (x + w >= srcWidth) {
            w = srcWidth - x;
        }
        if (y + h >= srcHeight) {
            h = srcHeight - y;
        }
        w = (w / 2) * 2;// 偶数转换
        h = (h / 2) * 2;// 偶数转换
        int[] rect = new int[4];
        rect[0] = x;
        rect[1] = y;
        rect[2] = w;
        rect[3] = h;
        return rect;
    }

    /**
     * yuv数据的裁剪操作
     *
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param mode       缩放模式 这里为0，1，2，3 速度由快到慢，质量由低到高，一般用0就好了
     **/
    public static native void resizeYUV(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int mode);

    /**
     * yuv数据的裁剪操作
     *
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param degree     旋转的角度，90，180和270三种
     **/
    public static native void rotateYUV(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int degree);

    /**
     * 将I420转化为NV21
     *
     * @param i420Src 原始I420数据
     * @param nv21Src 转化后的NV21数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
    public static native void yuvI420ToNV21(byte[] i420Src, byte[] nv21Src, int width, int height);

    /**
     * 将NV21转化为I420
     *
     * @param i420Src 原始NV21数据
     * @param nv21Src 转化后的I420数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
    public static native void yuvNV21ToI420(byte[] nv21Src, byte[] i420Src, int width, int height);

    /**
     * yuv420转为RGBA
     * 没测试
     * @param i420Src 原始I420数据
     * @param rgba    转化后的rgba数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
//    public static native void yuv420ToRGBA(byte[] i420Src, byte[] rgba, int width, int height);

    /**
     * yuv420转为ABGR
     *
     * @param i420Src 原始I420数据
     * @param rgba    转化后的rgba数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
    public static native void yuv420ToABGR(byte[] i420Src, byte[] rgba, int width, int height);

}
