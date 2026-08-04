package com.zhangyt.module.mine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zhangyt.common.language.LanguageManager
import com.zhangyt.common.router.RouterManager
import com.zhangyt.common.router.RouterPath
import com.zhangyt.common.theme.ThemeManager
import com.zhangyt.module.mine.R
import com.zhangyt.module.mine.ui.MineScreen
import com.zhangyt.module.mine.viewmodel.MineUiEffect
import com.zhangyt.module.mine.viewmodel.MineViewModel
import kotlinx.coroutines.launch

/**
 * @description: 承载我的 Compose 页面并处理路由、主题和语言等 Android 平台副作用
 * @author: zhangyoutao
 * @Date: 2026-04-10
 */
@Route(path = RouterPath.Mine.FRAGMENT_MINE)
class MineFragment : Fragment() {

    private val viewModel: MineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MineScreen(viewModel = viewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect(::handleEffect)
            }
        }
    }

    private fun handleEffect(effect: MineUiEffect) {
        when (effect) {
            is MineUiEffect.ApplyTheme -> ThemeManager.switch(effect.theme)
            is MineUiEffect.ApplyLanguage -> LanguageManager.switch(requireContext(), effect.language)
            MineUiEffect.OpenOta -> RouterManager.start(RouterPath.Ota.ACTIVITY_OTA)
            MineUiEffect.LoggedOut -> {
                Toast.makeText(requireContext(), R.string.mine_logout_success, Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }
}
