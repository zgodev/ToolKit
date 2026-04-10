package com.zhangyt.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.zhangyt.common.widget.LoadingDialog
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.internal.CancelAdapt
import me.jessyan.autosize.internal.CustomAdapt

/**
 * 所有 Activity 的基类（MVVM / MVI 通用）。
 *
 * 泛型 [VB] 为 ViewBinding 类型。子类通过 [getViewBinding] 提供绑定实例。
 *
 * 使用示例（MVVM）：
 * ```
 * class LoginActivity : BaseActivity<ActivityLoginBinding>() {
 *     private val viewModel: LoginViewModel by viewModels()
 *
 *     override fun getViewBinding() = ActivityLoginBinding.inflate(layoutInflater)
 *
 *     override fun initView() { ... }
 *     override fun initData() { ... }
 *     override fun observeViewModel() {
 *         viewModel.userLiveData.observe(this) { ... }
 *     }
 * }
 * ```
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), CustomAdapt {

    protected lateinit var binding: VB
        private set

    /** 全局 Loading Dialog（懒加载）。 */
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注入 ARouter 参数（若 Activity 声明了 @Autowired）
        ARouter.getInstance().inject(this)

        binding = getViewBinding()
        setContentView(binding.root)

        initView()
        initData()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (this is CustomAdapt) {
            AutoSizeCompat.autoConvertDensity(super.getResources(), sizeInDp, isBaseOnWidth)
        } else {
            if (this !is CancelAdapt) {
                AutoSize.autoConvertDensityOfGlobal(this)
            }
        }
    }
    /** 提供 ViewBinding 实例。 */
    protected abstract fun getViewBinding(): VB

    /** 初始化 View。 */
    protected open fun initView() {}

    /** 初始化数据（网络请求等）。 */
    protected open fun initData() {}

    /** 订阅 ViewModel 的 LiveData / Flow。 */
    protected open fun observeViewModel() {}

    /** 显示全局 Loading。 */
    fun showLoading(message: String = "加载中...") {
        if (!isFinishing && !loadingDialog.isShowing) {
            loadingDialog.setMessage(message)
            loadingDialog.show()
        }
    }

    /** 隐藏全局 Loading。 */
    fun hideLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    // --------------- AutoSize 屏幕适配 -----------------
    /**
     * 设计稿基准尺寸（dp）。
     * - 设计稿宽度（竖屏时使用）
     * - 设计稿高度（横屏时使用）
     * 项目里如果设计稿不一样，子类重写或修改这两个常量即可。
     */
    protected open val designWidthDp: Float get() = 360f
    protected open val designHeightDp: Float get() = 640f

    /** 当前是否横屏 */
    private val isLandscape: Boolean
        get() = resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE

    /**
     * 自动选择"窄边"作为基准维度：
     * - 竖屏（手机/平板都包含）：宽是窄边 → isBaseOnWidth = true
     * - 横屏：高是窄边 → isBaseOnWidth = false
     *
     * 这样可以避免横屏时所有 View 被压缩、平板竖屏时按高度溢出的问题。
     */
    override fun isBaseOnWidth(): Boolean = isLandscape

    /**
     * 配合 [isBaseOnWidth]：
     * - 竖屏返回设计宽
     * - 横屏返回设计高
     */
    override fun getSizeInDp(): Float = designWidthDp
}
