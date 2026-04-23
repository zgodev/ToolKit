package com.zhangyt.module.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhangyt.common.base.BaseFragment
import com.zhangyt.common.ext.click
import com.zhangyt.common.ext.toast
import com.zhangyt.common.language.Language
import com.zhangyt.common.language.LanguageManager
import com.zhangyt.common.router.RouterPath
import com.zhangyt.common.theme.ThemeManager
import com.zhangyt.common.theme.ThemeStyle
import com.zhangyt.common.user.UserManager
import com.zhangyt.module.home.databinding.HomeFragmentMineBinding

/**
 * 我的 Fragment。
 *
 * 这里演示：
 * - 用户信息显示
 * - 主题切换（蓝/红/绿/紫/暗夜）
 * - 语言切换（中英日）
 * - 退出登录
 */
class MineFragment : BaseFragment<HomeFragmentMineBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        HomeFragmentMineBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.titleBar.setTitle("我的")

        // 显示用户昵称
        val user = UserManager.getUser()
        binding.tvNickname.text = user?.nickname ?: "未登录"
        binding.tvPhone.text = user?.phone ?: ""

        // 主题切换
        binding.btnThemeBlue.click { ThemeManager.switch(ThemeStyle.BLUE) }
        binding.btnThemeRed.click { ThemeManager.switch(ThemeStyle.RED) }
        binding.btnThemeGreen.click { ThemeManager.switch(ThemeStyle.GREEN) }
        binding.btnThemePurple.click { ThemeManager.switch(ThemeStyle.PURPLE) }
        binding.btnThemeDark.click { ThemeManager.switch(ThemeStyle.DARK) }

        // 语言切换
        binding.btnLangZh.click { LanguageManager.switch(requireContext(), Language.ZH_CN) }
        binding.btnLangEn.click { LanguageManager.switch(requireContext(), Language.EN) }
        binding.btnLangJa.click { LanguageManager.switch(requireContext(), Language.JA) }
        binding.btnCheckUpdate.click { navigation(RouterPath.Mine.ACTIVITY_OTA) }
        // 退出登录
        binding.btnLogout.click {
            UserManager.logout()
            toast("已退出登录")
            requireActivity().finish()
        }
    }
}
