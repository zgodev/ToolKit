模块结构

ToolKit/
├── app                   壳工程（Application、Splash）
├── lib_common            基础库（BaseActivity/Fragment/VM、MVVM/MVI、Router、Theme、Language…）
├── network               网络层（Retrofit + OkHttp + Coroutines/Flow）
├── utils                 工具类（日期/键盘/状态栏/文件/Bitmap…）
├── widget                自定义 View（TitleBar/LoadingDialog/NodeProgressBar…）
├── module_login          登录业务（MVVM 示例）
└── module_home           主页业务（类微信 4 Tab）

### 核心能力一览

| 能力            | 实现位置                                            | 使用方式                                                 |
| :-------------- | :-------------------------------------------------- | :------------------------------------------------------- |
| **MVVM**        | `BaseActivity / BaseViewModel / BaseRepository`     | 继承 `BaseActivity<VB>()` + `launch { … }`               |
| **MVI**         | `mvi/MviViewModel.kt`                               | 定义 `IUiState/IUiIntent/IUiEffect` → `dispatch(intent)` |
| **组件化路由**  | `router/RouterPath, RouterManager` + ARouter        | `RouterManager.start(RouterPath.Login.ACTIVITY_LOGIN)`   |
| **多主题**      | `theme/ThemeManager` + 5 套 `Common_Theme_*`        | `ThemeManager.switch(ThemeStyle.RED)`                    |
| **多语言**      | `language/LanguageManager` + `values-en/ja`         | `LanguageManager.switch(ctx, Language.EN)`               |
| **屏幕适配**    | AutoSize + `BaseActivity` 实现 `CustomAdapt`        | 默认 360dp 宽度基准，布局直接写 dp                       |
| **网络层**      | `RetrofitClient + NetworkConfig + Interceptors`     | `RetrofitClient.create(Api::class.java)`                 |
| **统一响应**    | `BaseResponse<T>` + `ApiException`                  | `BaseRepository.request { api.xxx() }` 自动解包          |
| **Token 注入**  | `HeaderInterceptor` + `NetworkConfig.tokenProvider` | `App.onCreate` 里配置                                    |
| **用户会话**    | `UserManager` + `MMKV`                              | `UserManager.isLogin() / saveUser() / logout()`          |
| **图片加载**    | `ext/ViewExt.load / loadCircle`                     | `iv.load(url, placeholder = …)`                          |
| **扩展函数**    | `ext/CommonExt, ViewExt, FlowExt`                   | `view.click { } / toast() / 16.dp / "...".isMobile()`    |
| **Activity 栈** | `AppManager`                                        | `AppManager.exitApp() / recreateAll()`                   |
| **Loading**     | `LoadingDialog` 内置于 `BaseActivity`               | `showLoading() / hideLoading()`                          |

### 运行流程
`SplashActivity` → 未登录 → `LoginActivity`（账号任意≥4位 + 密码任意≥6位即可登录）→ `MainActivity`（微信风格 4 Tab：微信/通讯录/发现/我的）

在 **「我的」** Tab 可以直接体验：

- 主题切换（蓝 / 红 / 绿 / 紫 / 暗夜，秒切无重启）
- 语言切换（简中 / English / 日本語）
- 退出登录

### 重要文件

- `FRAMEWORK_GUIDE.md` — 完整的框架使用指南，涵盖新建页面、MVI 示例、网络请求、主题/多语言切换、所有工具类速查等
- `config.gradle` — 统一依赖版本管理（新增模块只需引用）
- `settings.gradle` — 模块注册入口（后续加业务模块只需追加一行）