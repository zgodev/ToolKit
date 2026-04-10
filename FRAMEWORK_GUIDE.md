# ToolKit 组件化项目框架指南

一套面向大型 Android 项目的组件化基础框架。默认已集成：
**组件化 (ARouter) + MVVM/MVI + Retrofit/Flow + Glide + MMKV + 屏幕适配 + 多语言 + 多主题**。

> Android: minSdk 21 / compileSdk 31 / Kotlin 1.6.10 / Gradle 7.1.2

---

## 1. 模块结构

```
ToolKit/
├── app                   应用壳工程（Application、Splash，不写业务）
├── lib_common            基础库：BaseActivity/Fragment/ViewModel、MVI、路由、主题、多语言
├── network               网络层：Retrofit + OkHttp + Coroutines
├── utils                 工具类：文件、Bitmap、日期、状态栏…
├── widget                自定义 View：TitleBar、LoadingDialog、NodeProgressBar…
├── module_login          业务模块：登录
└── module_home           业务模块：主页（微信风格 4 Tab）
```

**依赖关系：**
```
app
 ├── module_login ──┐
 └── module_home ───┴──► lib_common ──► network / utils / widget
```

**新增业务模块步骤：**
1. `settings.gradle` 追加 `include ':module_xxx'`
2. 复制 `module_login/build.gradle` 修改包名
3. `api project(path: ':lib_common')`
4. 在 `lib_common/router/RouterPath.kt` 注册路由
5. `app/build.gradle` 中 `implementation project(path: ':module_xxx')`

---

## 2. 快速上手

### 2.1 新建一个页面（MVVM）
```kotlin
@Route(path = RouterPath.Mine.ACTIVITY_SETTINGS)
class SettingsActivity : BaseActivity<MineActivitySettingsBinding>() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun getViewBinding() = MineActivitySettingsBinding.inflate(layoutInflater)

    override fun initView()   { binding.btnSave.click { viewModel.save() } }
    override fun initData()   { viewModel.loadUser() }
    override fun observeViewModel() {
        viewModel.user.observe(this) { binding.tvNickname.text = it.nickname }
        viewModel.loadState.observe(this) {
            when (it) {
                is UiLoadState.Loading -> showLoading()
                is UiLoadState.Success -> hideLoading()
                is UiLoadState.Error   -> { hideLoading(); toast(it.message) }
                else -> {}
            }
        }
    }
}
```

### 2.2 路由跳转
```kotlin
// 简单跳转
RouterManager.start(RouterPath.Login.ACTIVITY_LOGIN)

// 带参数
RouterManager.start(RouterPath.Common.ACTIVITY_WEB) {
    withString("url", "https://www.baidu.com")
    withString("title", "百度")
}

// 获取 Fragment
val fragment = RouterManager.getFragment(RouterPath.Home.FRAGMENT_CHAT)
```

### 2.3 网络请求
```kotlin
// 1. 定义 API
interface UserApi {
    @GET("api/v1/user/info")
    suspend fun getUserInfo(@Query("id") id: String): BaseResponse<UserInfo>
}

// 2. Repository 中调用（已自动处理 BaseResponse）
class UserRepository : BaseRepository() {
    private val api = RetrofitClient.create(UserApi::class.java)
    fun getUserInfo(id: String): Flow<UserInfo> = request { api.getUserInfo(id) }
}

// 3. ViewModel 中订阅
class UserViewModel : BaseViewModel() {
    val userLiveData = MutableLiveData<UserInfo>()
    fun load(id: String) = launch(showLoading = true) {
        repo.getUserInfo(id).collect { userLiveData.value = it }
    }
}

// 4. Activity 观察
viewModel.userLiveData.observe(this) { render(it) }
```

### 2.4 MVI 写法
```kotlin
// ---- Contract ----
data class LoginState(
    val loading: Boolean = false,
    val user: UserInfo? = null,
    val error: String? = null
) : IUiState

sealed class LoginIntent : IUiIntent {
    data class Submit(val account: String, val pwd: String) : LoginIntent()
}

sealed class LoginEffect : IUiEffect {
    object NavigateHome : LoginEffect()
}

// ---- ViewModel ----
class LoginMviVM : MviViewModel<LoginState, LoginIntent, LoginEffect>() {
    override fun initialState() = LoginState()
    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Submit -> login(intent.account, intent.pwd)
        }
    }
    private fun login(a: String, p: String) = viewModelScope.launch {
        updateState { copy(loading = true) }
        runCatching { LoginRepository().login(a, p).first() }
            .onSuccess {
                updateState { copy(loading = false, user = it) }
                sendEffect(LoginEffect.NavigateHome)
            }
            .onFailure {
                updateState { copy(loading = false, error = it.message) }
            }
    }
}

// ---- Activity ----
lifecycleScope.launch { viewModel.uiState.collect { render(it) } }
lifecycleScope.launch { viewModel.uiEffect.collect { handle(it) } }
```

---

## 3. 主题切换

```kotlin
ThemeManager.switch(ThemeStyle.RED)     // 切换红色主题
ThemeManager.switch(ThemeStyle.DARK)    // 切换暗夜模式
```
- 已内置 5 套主题：`BLUE / RED / GREEN / PURPLE / DARK`
- 布局中使用 `?attr/common_color_primary` 等属性即可自动生效
- 新增主题：
  1. 在 `lib_common/values/colors.xml` 追加色值
  2. 在 `lib_common/values/themes.xml` 新增 `<style name="Common_Theme_Xxx" parent="Common_Theme_Base">`
  3. 在 `ThemeStyle` 枚举中追加

**XML 引用规范**（推荐统一使用 attr，这样一处切换全局生效）：
```xml
<!-- 背景 -->
android:background="?attr/common_color_bg"
<!-- 主色 -->
android:textColor="?attr/common_color_primary"
<!-- 主标题 -->
android:textColor="?attr/common_color_text_main"
<!-- 副标题 -->
android:textColor="?attr/common_color_text_sub"
```

---

## 4. 多语言切换

```kotlin
LanguageManager.switch(context, Language.EN)       // 英文
LanguageManager.switch(context, Language.JA)       // 日语
LanguageManager.switch(context, Language.SYSTEM)   // 跟随系统
```
- 新增语言：`values-xx/strings.xml` 并在 `Language` 枚举中追加
- 运行时无需重启 App（内部自动 recreate 当前所有 Activity）
- `app/build.gradle` 中 `resConfigs "zh", "en", "ja"` 控制打包的语言资源

---

## 5. 屏幕适配

使用 [AutoSize](https://github.com/JessYanCoding/AndroidAutoSize)。
`BaseActivity` 已实现 `CustomAdapt`，默认以 **360dp 宽度** 为基准。
如需按高度适配：
```kotlin
override fun isBaseOnWidth(): Boolean = false
override fun getSizeInDp(): Float = 640f
```
布局中直接使用 `dp / sp` 即可，不需要写死 px。

---

## 6. 常用扩展 / 工具

| 功能 | 调用方式 |
|-----|---------|
| Toast | `toast("完成")` / `context.toast("…")` |
| 防抖点击 | `view.click { … }` |
| 显示隐藏 | `view.visible() / invisible() / gone()` |
| 加载图片 | `imageView.load(url, placeholder = R.drawable.ic_default)` |
| 圆形头像 | `imageView.loadCircle(url)` |
| dp 转换 | `16.dp` 返回 Int 像素 |
| 手机号校验 | `phone.isMobile()` |
| 日期格式化 | `DateUtils.format(time, DateUtils.YMD)` |
| 友好时间 | `DateUtils.friendlyTime(time)` // "3 分钟前" |
| 键盘显示/隐藏 | `KeyboardUtils.hide(activity)` |
| 状态栏设置 | `StatusBarUtils.setColor(this, Color.WHITE)` |
| 文件读写 | `FileUtils.xxx` |
| Bitmap 操作 | `BitmapUtil.xxx` |
| 持久化 | `MMKV.defaultMMKV().encode("k", "v")` |
| 用户 Token | `UserManager.getToken()` / `UserManager.logout()` |

---

## 7. 网络层详解

### 7.1 接口协议
统一 `BaseResponse<T>`：
```json
{ "code": 0, "message": "ok", "data": { ... } }
```
`BaseRepository#request` 会自动解包：
- `code == 0` ➜ 返回 `data`
- 否则抛出 `ApiException(code, message)`

### 7.2 配置
`App.kt` 中：
```kotlin
NetworkConfig.baseUrl      = BuildConfig.BASE_URL
NetworkConfig.debuggable   = BuildConfig.DEBUG
NetworkConfig.tokenProvider = { UserManager.getToken() }
NetworkConfig.commonHeaders = mapOf(
    "Content-Type" to "application/json",
    "App-Version"  to BuildConfig.VERSION_NAME
)
```

### 7.3 多环境
在 `app/build.gradle` 的 `buildTypes` 中：
```groovy
debug {
    buildConfigField "String", "BASE_URL", "\"https://dev.api.example.com/\""
}
release {
    buildConfigField "String", "BASE_URL", "\"https://api.example.com/\""
}
```

### 7.4 Token 过期处理
在 `HeaderInterceptor` 后新增一个 `TokenInterceptor` 检查 401 并触发强制登出 → 跳登录页。

---

## 8. 运行示例

1. Android Studio 同步 Gradle（首次会下载 ARouter / MMKV / AutoSize）
2. Run `app` 模块
3. 启动流程：`SplashActivity` → 未登录跳 `LoginActivity` → 登录成功跳 `MainActivity`（底部 Tab）
4. 在「我的」Tab 可以体验主题切换与多语言切换
5. 账号：任意（手机号格式 / 至少 4 位），密码：任意 ≥ 6 位 即可登录（演示版本地假数据）

---

## 9. 后续扩展建议

| 方向 | 建议引入 |
|-----|---------|
| 数据库 | Room（已在 config.gradle 预留版本） |
| 图片选择 | Matisse / PictureSelector |
| 下拉刷新 | SmartRefreshLayout |
| 权限 | easypermissions |
| 崩溃上报 | Bugly / Firebase Crashlytics |
| 埋点 | 封装 TrackManager，BaseActivity/Fragment 回调中调用 |
| 启动优化 | AnchorTask / Alpha（有向图初始化） |
| APK 瘦身 | resConfigs、混淆、资源压缩 |

---

## 10. 目录速查

```
lib_common/
├── base/                 BaseActivity/Fragment/ViewModel/Repository
├── mvi/                  MVI 契约 + ViewModel
├── router/               RouterPath / RouterManager
├── theme/                ThemeManager / ThemeStyle
├── language/             LanguageManager / Language
├── user/                 UserManager / UserInfo
├── state/                UiLoadState
├── ext/                  ViewExt / CommonExt / FlowExt
├── utils/                AppManager
└── widget/               LoadingDialog
```
