package com.zhangyt.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 反射调用 `XxxBinding.inflate(...)`，让 [BaseActivity] / [BaseFragment] 子类
 * 不必再手写 `getViewBinding()`。
 *
 * 沿运行时类向上查找第一个 [ViewBinding] 子类型作为泛型实参。仅支持
 * 单层继承场景：`SubActivity : BaseActivity<XxxBinding>()`；如有中间抽象类
 * 仍然带着未具体化的 VB 类型变量，需要在最终具体子类被实例化时才能解析。
 *
 * R8 / ProGuard 需 keep `XxxBinding.inflate` 静态方法，相关规则已加在
 * `lib_common/consumer-rules.pro`。
 */
@Suppress("UNCHECKED_CAST")
private fun <VB : ViewBinding> findViewBindingClass(startClass: Class<*>): Class<VB> {
    var clazz: Class<*>? = startClass
    while (clazz != null) {
        val genericSuper = clazz.genericSuperclass
        if (genericSuper is ParameterizedType) {
            for (type in genericSuper.actualTypeArguments) {
                if (type is Class<*> && ViewBinding::class.java.isAssignableFrom(type)) {
                    return type as Class<VB>
                }
            }
        }
        clazz = clazz.superclass
    }
    error("未在 $startClass 的继承链中找到 ViewBinding 泛型实参")
}

internal fun <VB : ViewBinding> inflateActivityBinding(
    target: Class<*>,
    inflater: LayoutInflater,
): VB {
    val bindingClass = findViewBindingClass<VB>(target)
    val method = bindingClass.getMethod("inflate", LayoutInflater::class.java)
    @Suppress("UNCHECKED_CAST")
    return method.invoke(null, inflater) as VB
}

internal fun <VB : ViewBinding> inflateFragmentBinding(
    target: Class<*>,
    inflater: LayoutInflater,
    container: ViewGroup?,
): VB {
    val bindingClass = findViewBindingClass<VB>(target)
    val method = bindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.javaPrimitiveType,
    )
    @Suppress("UNCHECKED_CAST")
    return method.invoke(null, inflater, container, false) as VB
}
