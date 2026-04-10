package com.zhangyt.common.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zhangyt.common.R

/**
 * 通用 Loading 对话框。BaseActivity 内置使用。
 * 也可手动 `LoadingDialog(ctx).show()`。
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.Common_LoadingDialog) {

    private val tvMessage: TextView

    init {
        val density = context.resources.displayMetrics.density
        val padding = (24 * density).toInt()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(padding, padding, padding, padding)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.common_bg_loading)
        }
        val progress = ProgressBar(context).apply {
            val size = (28 * density).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size)
        }
        tvMessage = TextView(context).apply {
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.leftMargin = (12 * density).toInt()
            layoutParams = lp
            text = "加载中..."
        }
        layout.addView(progress)
        layout.addView(tvMessage)
        setContentView(layout)
        setCancelable(false)
        window?.setDimAmount(0.3f)
    }

    fun setMessage(msg: String) { tvMessage.text = msg }
}
