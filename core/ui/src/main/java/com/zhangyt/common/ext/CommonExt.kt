package com.zhangyt.common.ext

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.Utils

/* ==================== Toast ==================== */
fun Context.toast(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
fun Fragment.toast(msg: CharSequence) { context?.toast(msg) }
fun toast(msg: CharSequence) {
    Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_SHORT).show()
}

/* ==================== dp/sp 转换 ==================== */
val Int.dp: Int get() =
    (this * Utils.getApp().resources.displayMetrics.density + 0.5f).toInt()

val Float.dp: Float get() =
    this * Utils.getApp().resources.displayMetrics.density

val Int.sp: Int get() =
    (this * Utils.getApp().resources.displayMetrics.scaledDensity + 0.5f).toInt()

/* ==================== String ==================== */
fun String?.orEmptyStr(): String = this ?: ""

fun String?.isMobile(): Boolean =
    !isNullOrBlank() && Regex("^1[3-9]\\d{9}\$").matches(this)

fun String?.isEmail(): Boolean =
    !isNullOrBlank() && Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$").matches(this)
