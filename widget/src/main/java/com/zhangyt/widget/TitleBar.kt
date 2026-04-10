package com.zhangyt.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes

/**
 * 通用 TitleBar：左侧返回按钮 + 中间标题 + 右侧按钮。
 *
 * 使用：
 * ```xml
 * <com.zhangyt.widget.TitleBar
 *     android:id="@+id/titleBar"
 *     android:layout_width="match_parent"
 *     android:layout_height="48dp"
 *     app:widget_title="登录" />
 * ```
 * ```
 * titleBar.setTitle("我的")
 * titleBar.setRight("保存") { ... }
 * titleBar.setOnBackClick { finish() }
 * ```
 */
class TitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val tvTitle: TextView
    private val ivBack: ImageView
    private val tvRight: TextView

    init {
        setBackgroundColor(Color.WHITE)
        val density = resources.displayMetrics.density
        val padH = (16 * density).toInt()

        ivBack = ImageView(context).apply {
            val size = (24 * density).toInt()
            layoutParams = LayoutParams(size, size, Gravity.CENTER_VERTICAL or Gravity.START).apply {
                leftMargin = padH
            }
            setImageResource(android.R.drawable.ic_menu_revert)
            setOnClickListener { (context as? android.app.Activity)?.finish() }
        }
        addView(ivBack)

        tvTitle = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            textSize = 18f
            setTextColor(Color.parseColor("#333333"))
        }
        addView(tvTitle)

        tvRight = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL or Gravity.END
            ).apply { rightMargin = padH }
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            visibility = View.GONE
        }
        addView(tvRight)

        // 解析 xml 自定义属性
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.TitleBar)
            ta.getString(R.styleable.TitleBar_widget_title)?.let { t -> tvTitle.text = t }
            ta.recycle()
        }
    }

    fun setTitle(title: CharSequence) { tvTitle.text = title }

    fun setBackIcon(@DrawableRes res: Int) { ivBack.setImageResource(res) }

    fun setOnBackClick(onClick: (View) -> Unit) {
        ivBack.setOnClickListener(onClick)
    }

    /** 显示右侧文字按钮。 */
    fun setRight(text: CharSequence, onClick: (View) -> Unit) {
        tvRight.visibility = View.VISIBLE
        tvRight.text = text
        tvRight.setOnClickListener(onClick)
    }
}
