# ToolKit

ToolKit 是一个面向中大型 Android 项目的严格组件化骨架，采用 Kotlin、ViewBinding、Hilt、ARouter、Retrofit/OkHttp、Coroutines/Flow 和 MMKV。

## 架构

```text
app                         应用组装、启动与主导航
├── module_login:api/impl   登录契约与实现
├── module_home:api/impl    首页契约与实现
├── module_mine:api/impl    我的契约与实现
└── module_ota:api/impl     OTA 契约与实现

core:model                  纯 Kotlin 模型
core:ui                     Activity/Fragment/ViewModel/MVI 基类与扩展
core:designsystem           主题、通用资源、自定义 View、Loading
core:navigation             ARouter 封装与通用路由
core:network                Retrofit/OkHttp、响应与 Repository
core:session                用户与会话事件
core:locale                 多语言
core:lifecycle              Activity 栈
core:startup                通用 Application 与启动任务
core:web                    WebView/TBS（由应用显式初始化）
core:utils                  可复用的现代工具
legacy:network              旧 WebSocket/HTTP Java 实现
legacy:utils                尚未现代化的历史工具与本地库
build-logic                 Gradle 约定插件
```

边界规则：core 不依赖业务；feature 之间只依赖对方 api；只有 app 或 standalone 组装层可以聚合 impl。

## 构建

项目固定使用 JDK 17，依赖版本统一在 `gradle/libs.versions.toml`。

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
```

首次在线构建下载完依赖后，可加 `--offline` 验证缓存可复现性。

## 独立组件 APK

`-Pstandalone=<feature>` 会把对应 `impl` 切换为 Application，使用独立 Manifest、Application/Launcher 和 applicationId：

```bash
./gradlew :module_login:impl:assembleDebug -Pstandalone=login
./gradlew :module_home:impl:assembleDebug -Pstandalone=home
./gradlew :module_mine:impl:assembleDebug -Pstandalone=mine
./gradlew :module_ota:impl:assembleDebug -Pstandalone=ota
```

支持值仅为 `login/home/mine/ota`，未知值会在配置阶段失败。`mine` 独立包按业务需要同时组装 OTA 实现。

## 运行流程

主应用：`SplashActivity` → 登录态判断 → `LoginActivity` → `MainActivity`。演示登录接受至少 4 位账号和至少 6 位密码；“我的”页面提供主题、语言、OTA 与退出登录示例。

详细开发规则见 [FRAMEWORK_GUIDE.md](FRAMEWORK_GUIDE.md)，AI 协作规则见 [AGENTS.md](AGENTS.md)。
