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
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

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

    fun navigation(path: String,bundle: Bundle?=null){
        if (bundle!=null)
            ARouter.getInstance().build(path).with(bundle).navigation()
        else
            ARouter.getInstance().build(path).navigation()
    }

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
}
