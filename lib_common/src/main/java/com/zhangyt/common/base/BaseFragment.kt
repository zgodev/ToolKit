package com.zhangyt.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter

/**
 * 所有 Fragment 的基类。
 *
 * 泛型 [VB] 为 ViewBinding 类型，子类只需声明 `BaseFragment<XxxBinding>` 即可，
 * 基类通过反射调用 `XxxBinding.inflate(...)` 自动创建 binding。
 *
 * 使用示例：
 * ```
 * class HomeFragment : BaseFragment<FragmentHomeBinding>() {
 *     override fun initView() { ... }
 * }
 * ```
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = inflateFragmentBinding(javaClass, inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun navigation(path: String,bundle: Bundle?=null){
        if (bundle!=null)
            ARouter.getInstance().build(path).with(bundle).navigation()
        else
            ARouter.getInstance().build(path).navigation()
    }
    protected open fun initView() {}
    protected open fun initData() {}
    protected open fun observeViewModel() {}
}
