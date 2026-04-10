package com.zhangyt.common.ext

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/* ==================== View ==================== */
fun View.visible() { visibility = View.VISIBLE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.gone() { visibility = View.GONE }

/** 防抖点击（默认 500ms）。 */
inline fun View.click(interval: Long = 500L, crossinline block: (View) -> Unit) {
    var lastTime = 0L
    setOnClickListener {
        val now = System.currentTimeMillis()
        if (now - lastTime >= interval) {
            lastTime = now
            block(it)
        }
    }
}

/* ==================== ImageView - Glide ==================== */
/**
 * 加载图片。
 * ```
 * imageView.load("https://xxx.jpg", placeholder = R.drawable.ic_placeholder)
 * ```
 */
fun ImageView.load(
    url: Any?,
    @DrawableRes placeholder: Int = 0,
    @DrawableRes error: Int = 0,
    roundCorner: Int = 0
) {
    val options = RequestOptions()
    if (placeholder != 0) options.placeholder(placeholder)
    if (error != 0) options.error(error)
    if (roundCorner > 0) options.transform(
        com.bumptech.glide.load.resource.bitmap.RoundedCorners(roundCorner)
    )
    Glide.with(this).load(url).apply(options).into(this)
}

/** 加载圆形头像。 */
fun ImageView.loadCircle(url: Any?, @DrawableRes placeholder: Int = 0) {
    val options = RequestOptions().circleCrop()
    if (placeholder != 0) options.placeholder(placeholder)
    Glide.with(this).load(url).apply(options).into(this)
}
