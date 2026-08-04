# ToolKit 项目索引

> 此文件由 `./gradlew generateProjectIndex` 生成。不要手工编辑；结构变更后重新生成。

## 模块

### `:app`

- 类型：`app`
- Namespace：`com.zhangyt.toolkit`
- 职责：主应用壳负责全量组件组装、应用启动与顶层导航，不承载具体业务实现。
- 说明：[`app/README.md`](../app/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:network`
  - `implementation` → `:core:session`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
  - `implementation` → `:core:web`
  - `implementation` → `:module_home`
  - `implementation` → `:module_login`
  - `implementation` → `:module_mine`
  - `implementation` → `:module_ota`
- 公共入口：
  - `App` — `app/src/main/java/com/zhangyt/toolkit/App.kt`
  - `MainActivity` — `app/src/main/java/com/zhangyt/toolkit/MainActivity.kt`
  - `SplashActivity` — `app/src/main/java/com/zhangyt/toolkit/SplashActivity.kt`

### `:core:designsystem`

- 类型：`core`
- Namespace：`com.zhangyt.core.designsystem`
- 职责：设计系统模块提供共享主题、颜色、文案与通用 View，不承载具体业务页面。
- 说明：[`core/designsystem/README.md`](../core/designsystem/README.md)
- 项目依赖：
  - `implementation` → `:core:lifecycle`
- 公共入口：
  - `LoadingDialog` — `core/designsystem/src/main/java/com/zhangyt/common/widget/LoadingDialog.kt`
  - `NodeProgressBar` — `core/designsystem/src/main/java/com/zhangyt/widget/NodeProgressBar.kt`
  - `NormalDialog` — `core/designsystem/src/main/java/com/zhangyt/widget/NormalDialog.java`
  - `ThemeManager` — `core/designsystem/src/main/java/com/zhangyt/common/theme/ThemeManager.kt`
  - `ThemeStyle` — `core/designsystem/src/main/java/com/zhangyt/common/theme/ThemeManager.kt`
  - `TitleBar` — `core/designsystem/src/main/java/com/zhangyt/widget/TitleBar.kt`
  - `ToolKitSpacing` — `core/designsystem/src/main/java/com/zhangyt/core/designsystem/compose/ToolKitSpacing.kt`
  - `ToolKitTheme` — `core/designsystem/src/main/java/com/zhangyt/core/designsystem/compose/ToolKitTheme.kt`
  - `toolKitPrimaryColor` — `core/designsystem/src/main/java/com/zhangyt/core/designsystem/compose/ToolKitTheme.kt`

### `:core:lifecycle`

- 类型：`core`
- Namespace：`com.zhangyt.core.lifecycle`
- 职责：生命周期模块维护进程内 Activity 栈等轻量生命周期能力，不处理页面业务或导航策略。
- 说明：[`core/lifecycle/README.md`](../core/lifecycle/README.md)
- 公共入口：
  - `AppManager` — `core/lifecycle/src/main/java/com/zhangyt/common/AppManager.kt`

### `:core:locale`

- 类型：`core`
- Namespace：`com.zhangyt.core.locale`
- 职责：多语言模块管理语言选择、持久化和 Context 配置，不保存业务文案资源。
- 说明：[`core/locale/README.md`](../core/locale/README.md)
- 项目依赖：
  - `implementation` → `:core:lifecycle`
- 公共入口：
  - `Language` — `core/locale/src/main/java/com/zhangyt/common/language/LanguageManager.kt`
  - `LanguageManager` — `core/locale/src/main/java/com/zhangyt/common/language/LanguageManager.kt`

### `:core:model`

- 类型：`core`
- Namespace：`-`
- 职责：模型模块保存跨功能稳定共享的纯 Kotlin 数据契约，不包含 Android、网络或 UI 行为。
- 说明：[`core/model/README.md`](../core/model/README.md)
- 公共入口：
  - `UserInfo` — `core/model/src/main/java/com/zhangyt/core/model/UserInfo.kt`

### `:core:navigation`

- 类型：`core`
- Namespace：`com.zhangyt.core.navigation`
- 职责：导航模块封装 ARouter 调用和全局通用路径，不拥有任何 feature 页面实现。
- 说明：[`core/navigation/README.md`](../core/navigation/README.md)
- 公共入口：
  - `RouterManager` — `core/navigation/src/main/java/com/zhangyt/common/router/RouterManager.kt`
  - `RouterPath` — `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt`

### `:core:network`

- 类型：`core`
- Namespace：`com.zhangyt.network`
- 职责：现代网络模块统一 Retrofit/OkHttp、响应解包、拦截器与 Repository 基线，并暂存待迁移的演示 `CommonApi`。
- 说明：[`core/network/README.md`](../core/network/README.md)
- 公共入口：
  - `ApiException` — `core/network/src/main/java/com/zhangyt/network/exception/ApiException.kt`
  - `BannerBean` — `core/network/src/main/java/com/zhangyt/network/api/CommonApi.kt`
  - `BaseRepository` — `core/network/src/main/java/com/zhangyt/network/repository/BaseRepository.kt`
  - `BaseResponse` — `core/network/src/main/java/com/zhangyt/network/api/BaseResponse.kt`
  - `BodyTruncatingLogger` — `core/network/src/main/java/com/zhangyt/network/interceptor/BodyTruncatingLogger.kt`
  - `CommonApi` — `core/network/src/main/java/com/zhangyt/network/api/CommonApi.kt`
  - `FeedItem` — `core/network/src/main/java/com/zhangyt/network/api/CommonApi.kt`
  - `FeedPage` — `core/network/src/main/java/com/zhangyt/network/api/CommonApi.kt`
  - `HeaderInterceptor` — `core/network/src/main/java/com/zhangyt/network/interceptor/HeaderInterceptor.kt`
  - `NetworkConfig` — `core/network/src/main/java/com/zhangyt/network/config/NetworkConfig.kt`
  - `NetworkModule` — `core/network/src/main/java/com/zhangyt/network/di/NetworkModule.kt`
  - `RetrofitClient` — `core/network/src/main/java/com/zhangyt/network/client/RetrofitClient.kt`
  - `TokenInterceptor` — `core/network/src/main/java/com/zhangyt/network/interceptor/TokenInterceptor.kt`

### `:core:session`

- 类型：`core`
- Namespace：`com.zhangyt.core.session`
- 职责：会话模块管理当前用户、持久化登录态和会话事件，不处理登录页面或具体认证接口。
- 说明：[`core/session/README.md`](../core/session/README.md)
- 项目依赖：
  - `api` → `:core:model`
- 公共入口：
  - `SessionEvents` — `core/session/src/main/java/com/zhangyt/common/user/SessionEvents.kt`
  - `UserManager` — `core/session/src/main/java/com/zhangyt/common/user/UserManager.kt`

### `:core:startup`

- 类型：`core`
- Namespace：`com.zhangyt.core.startup`
- 职责：启动模块提供可复用 Application 基类和进程级基础设施初始化，不编排具体业务首页。
- 说明：[`core/startup/README.md`](../core/startup/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
  - `implementation` → `:core:lifecycle`
  - `implementation` → `:core:locale`
- 公共入口：
  - `AppStartup` — `core/startup/src/main/java/com/zhangyt/common/AppStartup.kt`
  - `CommonApplication` — `core/startup/src/main/java/com/zhangyt/common/CommonApplication.kt`

### `:core:ui`

- 类型：`core`
- Namespace：`com.zhangyt.core.ui`
- 职责：UI 基础模块提供 Activity、Fragment、ViewModel、MVI 契约和通用扩展，不实现业务界面。
- 说明：[`core/ui/README.md`](../core/ui/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
- 公共入口：
  - `BaseActivity` — `core/ui/src/main/java/com/zhangyt/common/base/BaseActivity.kt`
  - `BaseFragment` — `core/ui/src/main/java/com/zhangyt/common/base/BaseFragment.kt`
  - `BaseViewModel` — `core/ui/src/main/java/com/zhangyt/common/base/BaseViewModel.kt`
  - `FlowCallback` — `core/ui/src/main/java/com/zhangyt/common/ext/FlowExt.kt`
  - `IUiEffect` — `core/ui/src/main/java/com/zhangyt/common/mvi/MviContract.kt`
  - `IUiIntent` — `core/ui/src/main/java/com/zhangyt/common/mvi/MviContract.kt`
  - `IUiState` — `core/ui/src/main/java/com/zhangyt/common/mvi/MviContract.kt`
  - `MviViewModel` — `core/ui/src/main/java/com/zhangyt/common/mvi/MviViewModel.kt`
  - `UiLoadState` — `core/ui/src/main/java/com/zhangyt/common/state/UiLoadState.kt`
  - `click` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`
  - `collectIn` — `core/ui/src/main/java/com/zhangyt/common/ext/FlowExt.kt`
  - `gone` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`
  - `invisible` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`
  - `isEmail` — `core/ui/src/main/java/com/zhangyt/common/ext/CommonExt.kt`
  - `isMobile` — `core/ui/src/main/java/com/zhangyt/common/ext/CommonExt.kt`
  - `load` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`
  - `loadCircle` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`
  - `orEmptyStr` — `core/ui/src/main/java/com/zhangyt/common/ext/CommonExt.kt`
  - `toast` — `core/ui/src/main/java/com/zhangyt/common/ext/CommonExt.kt`
  - `visible` — `core/ui/src/main/java/com/zhangyt/common/ext/ViewExt.kt`

### `:core:utils`

- 类型：`core`
- Namespace：`com.zhangyt.core.utils`
- 职责：现代工具模块保存边界清晰、无业务语义的小型 Android 工具，不作为通用杂物箱。
- 说明：[`core/utils/README.md`](../core/utils/README.md)
- 公共入口：
  - `DateUtils` — `core/utils/src/main/java/com/zhangyt/utils/DateUtils.kt`
  - `KeyboardUtils` — `core/utils/src/main/java/com/zhangyt/utils/KeyboardUtils.kt`
  - `StatusBarUtils` — `core/utils/src/main/java/com/zhangyt/utils/StatusBarUtils.kt`

### `:core:web`

- 类型：`core`
- Namespace：`com.zhangyt.core.web`
- 职责：Web 模块封装 TBS WebView 初始化、页面容器和复用池，不实现业务 H5 协议。
- 说明：[`core/web/README.md`](../core/web/README.md)
- 项目依赖：
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
- 公共入口：
  - `WebActivity` — `core/web/src/main/java/com/zhangyt/common/web/WebActivity.kt`
  - `WebEngine` — `core/web/src/main/java/com/zhangyt/common/web/WebEngine.kt`
  - `WebEngineConfig` — `core/web/src/main/java/com/zhangyt/common/web/WebEngine.kt`
  - `WebInitializer` — `core/web/src/main/java/com/zhangyt/common/web/WebInitializer.kt`
  - `WebSettingsProxy` — `core/web/src/main/java/com/zhangyt/common/web/WebViewWrapper.kt`
  - `WebViewPool` — `core/web/src/main/java/com/zhangyt/common/web/WebViewPool.kt`
  - `WebViewWrapper` — `core/web/src/main/java/com/zhangyt/common/web/WebViewWrapper.kt`

### `:legacy:network`

- 类型：`legacy`
- Namespace：`com.zhangyt.legacy.network`
- 职责：遗留网络模块隔离旧 Java HTTP 与 WebSocket 实现，仅用于兼容既有调用，不接收新的常规网络代码。
- 说明：[`legacy/network/README.md`](../legacy/network/README.md)
- 项目依赖：
  - `implementation` → `:legacy:utils`
- 公共入口：
  - `ClientListener` — `legacy/network/src/main/java/com/zhangyt/network/websocket/ClientListener.java`
  - `HttpCallback` — `legacy/network/src/main/java/com/zhangyt/network/httputil/HttpCallback.java`
  - `HttpHelper` — `legacy/network/src/main/java/com/zhangyt/network/httputil/HttpHelper.java`
  - `HttpsUtils` — `legacy/network/src/main/java/com/zhangyt/network/httputil/HttpsUtils.java`
  - `IOkHttpWebSocket` — `legacy/network/src/main/java/com/zhangyt/network/websocket/IOkHttpWebSocket.java`
  - `IReceiveMessage` — `legacy/network/src/main/java/com/zhangyt/network/websocket/IReceiveMessage.java`
  - `OkHttpWebSocket` — `legacy/network/src/main/java/com/zhangyt/network/websocket/OkHttpWebSocket.java`
  - `SecureSocketFactory` — `legacy/network/src/main/java/com/zhangyt/network/httputil/SecureSocketFactory.java`
  - `UrlConstants` — `legacy/network/src/main/java/com/zhangyt/network/httputil/UrlConstants.java`
  - `WebSocketManager` — `legacy/network/src/main/java/com/zhangyt/network/websocket/WebSocketManager.java`
  - `WebSocketPostDataPro` — `legacy/network/src/main/java/com/zhangyt/network/websocket/WebSocketPostDataPro.java`

### `:legacy:utils`

- 类型：`legacy`
- Namespace：`com.zhangyt.legacy.utils`
- 职责：遗留工具模块隔离历史 Java 工具、本地库和打包产物，仅用于兼容迁移，不作为新功能入口。
- 说明：[`legacy/utils/README.md`](../legacy/utils/README.md)
- 公共入口：
  - `AppSignUtil` — `legacy/utils/src/main/java/com/zhangyt/utils/AppSignUtil.java`
  - `BitmapUtil` — `legacy/utils/src/main/java/com/zhangyt/utils/BitmapUtil.java`
  - `DipUtils` — `legacy/utils/src/main/java/com/zhangyt/utils/DipUtils.java`
  - `ExcelUtils` — `legacy/utils/src/main/java/com/zhangyt/utils/ExcelUtils.java`
  - `FileUtils` — `legacy/utils/src/main/java/com/zhangyt/utils/FileUtils.java`
  - `LogManager` — `legacy/utils/src/main/java/com/zhangyt/utils/LogManager.java`
  - `NetCheck` — `legacy/utils/src/main/java/com/zhangyt/utils/NetCheck.java`
  - `PermissionUtil` — `legacy/utils/src/main/java/com/zhangyt/utils/PermissionUtil.java`
  - `PubUtils` — `legacy/utils/src/main/java/com/zhangyt/utils/PubUtils.java`
  - `SPUtils` — `legacy/utils/src/main/java/com/zhangyt/utils/SPUtils.java`
  - `SubjectManager` — `legacy/utils/src/main/java/com/zhangyt/utils/SubjectManager.java`
  - `TRYuvUtil` — `legacy/utils/src/main/java/com/libyuv/util/TRYuvUtil.java`
  - `ThreadPoolUtil` — `legacy/utils/src/main/java/com/zhangyt/utils/ThreadPoolUtil.java`
  - `TuringCode` — `legacy/utils/src/main/java/com/zhangyt/utils/TuringCode.java`
  - `WriteLog` — `legacy/utils/src/main/java/com/zhangyt/utils/WriteLog.java`

### `:module_home`

- 类型：`feature`
- Namespace：`com.zhangyt.module.home`
- 职责：首页业务模块，包含消息、联系人和发现页面及独立运行入口。
- 说明：[`module_home/README.md`](../module_home/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
  - `implementation` → `:core:utils`
- 公共入口：
  - `ChatFragment` — `module_home/src/main/java/com/zhangyt/module/home/fragment/ChatFragment.kt`
  - `ChatItem` — `module_home/src/main/java/com/zhangyt/module/home/model/ChatItem.kt`
  - `ChatListAdapter` — `module_home/src/main/java/com/zhangyt/module/home/adapter/ChatListAdapter.kt`
  - `ChatViewModel` — `module_home/src/main/java/com/zhangyt/module/home/viewmodel/ChatViewModel.kt`
  - `ContactsFragment` — `module_home/src/main/java/com/zhangyt/module/home/fragment/ContactsFragment.kt`
  - `DiscoverFragment` — `module_home/src/main/java/com/zhangyt/module/home/fragment/DiscoverFragment.kt`

### `:module_login`

- 类型：`feature`
- Namespace：`com.zhangyt.module.login`
- 职责：登录业务模块，包含登录页面、网络协议、Repository、ViewModel、DI 与独立运行入口。
- 说明：[`module_login/README.md`](../module_login/README.md)
- 项目依赖：
  - `implementation` → `:core:model`
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:network`
  - `implementation` → `:core:session`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
- 公共入口：
  - `LoginActivity` — `module_login/src/main/java/com/zhangyt/module/login/LoginActivity.kt`
  - `LoginApi` — `module_login/src/main/java/com/zhangyt/module/login/api/LoginApi.kt`
  - `LoginModule` — `module_login/src/main/java/com/zhangyt/module/login/di/LoginModule.kt`
  - `LoginRepository` — `module_login/src/main/java/com/zhangyt/module/login/repository/LoginRepository.kt`
  - `LoginRequest` — `module_login/src/main/java/com/zhangyt/module/login/api/LoginApi.kt`
  - `LoginResponse` — `module_login/src/main/java/com/zhangyt/module/login/api/LoginApi.kt`
  - `LoginViewModel` — `module_login/src/main/java/com/zhangyt/module/login/viewmodel/LoginViewModel.kt`
  - `SmsRequest` — `module_login/src/main/java/com/zhangyt/module/login/api/LoginApi.kt`

### `:module_mine`

- 类型：`feature`
- Namespace：`com.zhangyt.module.mine`
- 职责：“我的”业务模块，包含用户信息、主题、语言、OTA 入口和独立运行能力。
- 说明：[`module_mine/README.md`](../module_mine/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
  - `implementation` → `:core:locale`
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:session`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
- 公共入口：
  - `MineFragment` — `module_mine/src/main/java/com/zhangyt/module/mine/fragment/MineFragment.kt`
  - `MineScreen` — `module_mine/src/main/java/com/zhangyt/module/mine/ui/MineScreen.kt`
  - `MineUiEffect` — `module_mine/src/main/java/com/zhangyt/module/mine/viewmodel/MineContract.kt`
  - `MineUiState` — `module_mine/src/main/java/com/zhangyt/module/mine/viewmodel/MineContract.kt`
  - `MineViewModel` — `module_mine/src/main/java/com/zhangyt/module/mine/viewmodel/MineViewModel.kt`

### `:module_ota`

- 类型：`feature`
- Namespace：`com.zhangyt.module.ota`
- 职责：OTA 业务模块，包含版本展示、后台下载、安装引导和独立运行入口。
- 说明：[`module_ota/README.md`](../module_ota/README.md)
- 项目依赖：
  - `implementation` → `:core:designsystem`
  - `implementation` → `:core:navigation`
  - `implementation` → `:core:network`
  - `implementation` → `:core:startup`
  - `implementation` → `:core:ui`
- 公共入口：
  - `OtaActivity` — `module_ota/src/main/java/com/zhangyt/module/ota/OtaActivity.kt`
  - `OtaInfo` — `module_ota/src/main/java/com/zhangyt/module/ota/OtaInfo.kt`
  - `OtaManager` — `module_ota/src/main/java/com/zhangyt/module/ota/OtaManager.kt`
  - `OtaWorker` — `module_ota/src/main/java/com/zhangyt/module/ota/OtaWorker.kt`

### `build-logic`

- 类型：`build-logic`
- Namespace：`com.zhangyt.toolkit.buildlogic`
- 职责：构建逻辑模块集中维护 Android 约定插件、独立组件装配和项目治理任务，不包含运行时业务代码。
- 说明：[`build-logic/README.md`](../build-logic/README.md)
- 公共入口：
  - `ARouterConventionPlugin` — `build-logic/src/main/kotlin/ARouterConventionPlugin.kt`
  - `AndroidApplicationConventionPlugin` — `build-logic/src/main/kotlin/AndroidApplicationConventionPlugin.kt`
  - `AndroidFeatureConventionPlugin` — `build-logic/src/main/kotlin/AndroidFeatureConventionPlugin.kt`
  - `AndroidLibraryConventionPlugin` — `build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt`
  - `ArchitectureGuardPlugin` — `build-logic/src/main/kotlin/ArchitectureGuardPlugin.kt`
  - `ArchitectureRules` — `build-logic/src/main/kotlin/ArchitectureRules.kt`
  - `ArchitectureSnapshot` — `build-logic/src/main/kotlin/ArchitectureRules.kt`
  - `ArchitectureViolation` — `build-logic/src/main/kotlin/ArchitectureRules.kt`
  - `DependencyIndexEntry` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `HiltConventionPlugin` — `build-logic/src/main/kotlin/HiltConventionPlugin.kt`
  - `KotlinLibraryConventionPlugin` — `build-logic/src/main/kotlin/KotlinLibraryConventionPlugin.kt`
  - `ModuleIndexEntry` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `ModuleReadmeIndex` — `build-logic/src/main/kotlin/ArchitectureRules.kt`
  - `ProjectDependencyEdge` — `build-logic/src/main/kotlin/ArchitectureRules.kt`
  - `ProjectIndexRenderer` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `ProjectIndexSnapshot` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `PublicEntryIndex` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `ResourceIndexEntry` — `build-logic/src/main/kotlin/ProjectIndex.kt`
  - `RouteIndexEntry` — `build-logic/src/main/kotlin/ProjectIndex.kt`

## 路由

| 路径 | 所属模块 | 常量 | 声明位置 |
| --- | --- | --- | --- |
| `/common/activity_web` | `:core:navigation` | `RouterPath.Common.ACTIVITY_WEB` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/home/fragment_chat` | `:core:navigation` | `RouterPath.Home.FRAGMENT_CHAT` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/home/fragment_contacts` | `:core:navigation` | `RouterPath.Home.FRAGMENT_CONTACTS` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/home/fragment_discover` | `:core:navigation` | `RouterPath.Home.FRAGMENT_DISCOVER` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/login/activity_login` | `:core:navigation` | `RouterPath.Login.ACTIVITY_LOGIN` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/main/activity_main` | `:core:navigation` | `RouterPath.Home.ACTIVITY_MAIN` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/mine/fragment_mine` | `:core:navigation` | `RouterPath.Mine.FRAGMENT_MINE` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |
| `/ota/activity_ota` | `:core:navigation` | `RouterPath.Ota.ACTIVITY_OTA` | `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt` |

## Android 资源

| 名称 | 类型 | 所属模块 | 文件 |
| --- | --- | --- | --- |
| `Common_LoadingDialog` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Base` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Blue` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Dark` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Green` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Purple` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `Common_Theme_Red` | `style` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/themes.xml` |
| `NodeProgressBar` | `declare-styleable` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `Theme.ToolKit` | `style` | `:app` | `app/src/main/res/values/themes.xml` |
| `TitleBar` | `declare-styleable` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `app_activity_main` | `layout` | `:app` | `app/src/main/res/layout/app_activity_main.xml` |
| `app_color_tab` | `color` | `:app` | `app/src/main/res/color/app_color_tab.xml` |
| `app_menu_bottom` | `menu` | `:app` | `app/src/main/res/menu/app_menu_bottom.xml` |
| `app_name` | `string` | `:app` | `app/src/main/res/values-en/strings.xml` |
| `app_name` | `string` | `:app` | `app/src/main/res/values-ja/strings.xml` |
| `app_name` | `string` | `:app` | `app/src/main/res/values/strings.xml` |
| `bg_btn_gray` | `drawable` | `:core:designsystem` | `core/designsystem/src/main/res/drawable/bg_btn_gray.xml` |
| `bg_btn_green` | `drawable` | `:core:designsystem` | `core/designsystem/src/main/res/drawable/bg_btn_green.xml` |
| `black` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `black` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `blue` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `blue_press` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `common_app_name` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_app_name` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_app_name` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_bg_loading` | `drawable` | `:core:designsystem` | `core/designsystem/src/main/common-res/drawable/common_bg_loading.xml` |
| `common_bg_page` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_black` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_blue_accent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_blue_primary` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_blue_primary_dark` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_cancel` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_cancel` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_cancel` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_color_accent` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_color_bg` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_color_primary` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_color_primary_dark` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_color_text_main` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_color_text_sub` | `attr` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/attrs.xml` |
| `common_confirm` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_confirm` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_confirm` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_dark_accent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_dark_bg` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_dark_primary` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_dark_primary_dark` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_dark_text_main` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_dark_text_sub` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_empty` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_empty` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_empty` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_green_accent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_green_primary` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_green_primary_dark` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_line` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_loading` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_loading` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_loading` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_net_error` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_net_error` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_net_error` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_net_timeout` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_net_timeout` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_net_timeout` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_ok` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_ok` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_ok` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_purple_accent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_purple_primary` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_purple_primary_dark` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_red_accent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_red_primary` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_red_primary_dark` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_retry` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_retry` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_retry` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_save` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-en/strings.xml` |
| `common_save` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values-ja/strings.xml` |
| `common_save` | `string` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/strings.xml` |
| `common_text_hint` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_text_main` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_text_sub` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_transparent` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `common_white` | `color` | `:core:designsystem` | `core/designsystem/src/main/common-res/values/colors.xml` |
| `corner_gray_bg` | `drawable` | `:core:designsystem` | `core/designsystem/src/main/res/drawable/corner_gray_bg.xml` |
| `dialog_normal` | `layout` | `:core:designsystem` | `core/designsystem/src/main/res/layout/dialog_normal.xml` |
| `gray_bg` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `gray_btn` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `gray_btn_press` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `gray_text` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `green_unClickable` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `home_bg_unread` | `drawable` | `:module_home` | `module_home/src/main/res/drawable/home_bg_unread.xml` |
| `home_fragment_chat` | `layout` | `:module_home` | `module_home/src/main/res/layout/home_fragment_chat.xml` |
| `home_fragment_contacts` | `layout` | `:module_home` | `module_home/src/main/res/layout/home_fragment_contacts.xml` |
| `home_fragment_discover` | `layout` | `:module_home` | `module_home/src/main/res/layout/home_fragment_discover.xml` |
| `home_item_chat` | `layout` | `:module_home` | `module_home/src/main/res/layout/home_item_chat.xml` |
| `home_tab_chat` | `string` | `:module_home` | `module_home/src/main/res/values-en/strings.xml` |
| `home_tab_chat` | `string` | `:module_home` | `module_home/src/main/res/values/strings.xml` |
| `home_tab_contacts` | `string` | `:module_home` | `module_home/src/main/res/values-en/strings.xml` |
| `home_tab_contacts` | `string` | `:module_home` | `module_home/src/main/res/values/strings.xml` |
| `home_tab_discover` | `string` | `:module_home` | `module_home/src/main/res/values-en/strings.xml` |
| `home_tab_discover` | `string` | `:module_home` | `module_home/src/main/res/values/strings.xml` |
| `home_tab_mine` | `string` | `:module_home` | `module_home/src/main/res/values-en/strings.xml` |
| `home_tab_mine` | `string` | `:module_home` | `module_home/src/main/res/values/strings.xml` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-hdpi/ic_launcher.png` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-mdpi/ic_launcher.png` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-xhdpi/ic_launcher.png` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` |
| `ic_launcher` | `mipmap` | `:app` | `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` |
| `ic_launcher_background` | `drawable` | `:app` | `app/src/main/res/drawable/ic_launcher_background.xml` |
| `ic_launcher_foreground` | `drawable` | `:app` | `app/src/main/res/drawable-v24/ic_launcher_foreground.xml` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-hdpi/ic_launcher_round.png` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-mdpi/ic_launcher_round.png` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png` |
| `ic_launcher_round` | `mipmap` | `:app` | `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png` |
| `icon_error` | `drawable` | `:core:designsystem` | `core/designsystem/src/main/res/drawable/icon_error.png` |
| `labelTextColor` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `labelTextSize` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `login_activity_login` | `layout` | `:module_login` | `module_login/src/main/res/layout/login_activity_login.xml` |
| `login_bg_button` | `drawable` | `:module_login` | `module_login/src/main/res/drawable/login_bg_button.xml` |
| `login_bg_input` | `drawable` | `:module_login` | `module_login/src/main/res/drawable/login_bg_input.xml` |
| `login_btn_login` | `string` | `:module_login` | `module_login/src/main/res/values-en/strings.xml` |
| `login_btn_login` | `string` | `:module_login` | `module_login/src/main/res/values/strings.xml` |
| `login_hint_account` | `string` | `:module_login` | `module_login/src/main/res/values-en/strings.xml` |
| `login_hint_account` | `string` | `:module_login` | `module_login/src/main/res/values/strings.xml` |
| `login_hint_password` | `string` | `:module_login` | `module_login/src/main/res/values-en/strings.xml` |
| `login_hint_password` | `string` | `:module_login` | `module_login/src/main/res/values/strings.xml` |
| `login_tip` | `string` | `:module_login` | `module_login/src/main/res/values-en/strings.xml` |
| `login_tip` | `string` | `:module_login` | `module_login/src/main/res/values/strings.xml` |
| `login_welcome` | `string` | `:module_login` | `module_login/src/main/res/values-en/strings.xml` |
| `login_welcome` | `string` | `:module_login` | `module_login/src/main/res/values/strings.xml` |
| `max` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `mine_check_update` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_check_update` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_check_update` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_check_update_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_check_update_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_check_update_description` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_guest` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_guest` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_guest` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_language_chinese` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_language_chinese` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_language_chinese` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_language_english` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_language_english` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_language_english` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_language_japanese` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_language_japanese` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_language_japanese` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_language_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_language_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_language_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_language_system` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_language_system` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_language_system` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_logout` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_logout` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_logout` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_logout_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_logout_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_logout_description` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_logout_success` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_logout_success` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_logout_success` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_not_logged_in` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_not_logged_in` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_not_logged_in` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_personalization` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_personalization` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_personalization` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_profile_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_profile_description` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_profile_description` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_system_services` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_system_services` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_system_services` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_blue` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_blue` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_blue` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_dark` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_dark` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_dark` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_green` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_green` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_green` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_purple` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_purple` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_purple` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_red` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_red` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_red` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_theme_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_theme_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_theme_switch` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `mine_title` | `string` | `:module_mine` | `module_mine/src/main/res/values-en/strings.xml` |
| `mine_title` | `string` | `:module_mine` | `module_mine/src/main/res/values-ja/strings.xml` |
| `mine_title` | `string` | `:module_mine` | `module_mine/src/main/res/values/strings.xml` |
| `network_security_config` | `xml` | `:app` | `app/src/main/res/xml/network_security_config.xml` |
| `nodeRadius` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `ota_activity_update` | `layout` | `:module_ota` | `module_ota/src/main/res/layout/ota_activity_update.xml` |
| `ota_file_paths` | `xml` | `:module_ota` | `module_ota/src/main/res/xml/ota_file_paths.xml` |
| `primaryNodeColor` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `progress` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `progressBarHeight` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `progressColor` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `purple_200` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `purple_500` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `purple_700` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `secondaryNodeColor` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `secondaryNodeRadius` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `teal_200` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `teal_700` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `trackColor` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |
| `transparent` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `web_activity` | `layout` | `:core:web` | `core/web/src/main/res/layout/web_activity.xml` |
| `white` | `color` | `:app` | `app/src/main/res/values/colors.xml` |
| `white` | `color` | `:core:designsystem` | `core/designsystem/src/main/res/values/colors.xml` |
| `widget_title` | `attr` | `:core:designsystem` | `core/designsystem/src/main/res/values/atts.xml` |

## 独立组件构建

- `./gradlew :module_home:assembleDebug -Pstandalone=home`

- `./gradlew :module_login:assembleDebug -Pstandalone=login`

- `./gradlew :module_mine:assembleDebug -Pstandalone=mine`

- `./gradlew :module_ota:assembleDebug -Pstandalone=ota`
