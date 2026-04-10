package com.zhangyt.module.login

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseActivity
import com.zhangyt.common.ext.click
import com.zhangyt.common.ext.isMobile
import com.zhangyt.common.ext.toast
import com.zhangyt.common.router.RouterManager
import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.login.databinding.LoginActivityLoginBinding
import com.zhangyt.module.login.viewmodel.LoginViewModel

/**
 * 登录页。
 *
 * 通过 ARouter 打开：
 * ```
 * RouterManager.start(RouterPath.Login.ACTIVITY_LOGIN)
 * ```
 */
@Route(path = RouterPath.Login.ACTIVITY_LOGIN)
class LoginActivity : BaseActivity<LoginActivityLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    override fun getViewBinding() = LoginActivityLoginBinding.inflate(layoutInflater)

    /**
     * 登录页不使用 AutoSize 的"按宽度等比放大"。
     * 返回 0 告诉 AutoSize 放弃对本页面的适配，使用系统原始 density。
     * 配合 ScrollView + ConstraintLayout 的 maxWidth，在手机 / 平板都能正常显示。
     */

    override fun initView() {
        binding.btnLogin.click { doLogin() }
    }

    override fun observeViewModel() {
        viewModel.loadingState.observe(this) { loading ->
            if (loading) showLoading("登录中...") else hideLoading()
        }
        viewModel.errorState.observe(this) { err -> toast(err) }
        viewModel.loginResult.observe(this) {
            toast("登录成功，欢迎 ${it.nickname}")
            // 跳转主页
            RouterManager.start(RouterPath.Home.ACTIVITY_MAIN)
            finish()
        }
    }

    private fun doLogin() {
        val account = binding.etAccount.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        if (account.isEmpty()) { toast("请输入账号"); return }
        if (!account.isMobile() && account.length < 4) { toast("账号格式不正确"); return }
        if (password.isEmpty()) { toast("请输入密码"); return }
        viewModel.login(account, password)
    }
}
