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
 * 使用示例：
 * ```
 * class HomeFragment : BaseFragment<FragmentHomeBinding>() {
 *     override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
 *         FragmentHomeBinding.inflate(inflater, container, false)
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
        _binding = getViewBinding(inflater, container)
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
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected open fun initView() {}
    protected open fun initData() {}
    protected open fun observeViewModel() {}
}
