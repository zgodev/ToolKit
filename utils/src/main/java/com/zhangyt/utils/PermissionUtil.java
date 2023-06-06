package com.zhangyt.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class PermissionUtil {
    /**
     * 检测是否开启权限
     * @param context
     * @param permission
     *
     * PermissionUtil.checkPermission(mContext, "android.permission.READ_PHONE_STATE")
     *
     * @return return true-表示开启权限  false-表示权限没开启
     */
    public static boolean checkPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
    /**
     * 判断权限集合
     * permissions 权限数组
     * "android.permission.RECORD_AUDIO"
     * return true-表示开启权限  false-表示权限没开启
     */
    public static boolean checkPermissions(Context mContexts,String [] mPermissions) {
        for (String permission : mPermissions) {
            if (!checkPermission(mContexts, permission)) {
                Log.e("TAG", "-------没有开启权限");
                return false;
            }
        }
        Log.e("TAG", "-------权限已开启");
        return true;
    }
}
