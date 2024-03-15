package com.zhangyt.utils;

import android.content.Context;

/**
 * @author zhangyt
 * @description dp转换工具
 * @Date 2023/1/11 14:25
 **/
public class DipUtils {
    /**
     * 根据像素转换成dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据dip转换成像素
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dipToPx(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}