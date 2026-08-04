# ToolKit

ToolKit 是面向中大型 Android 项目的严格组件化骨架，采用 Kotlin、ViewBinding、Hilt、ARouter、Retrofit/OkHttp、Coroutines/Flow 和 MMKV，并为 Codex/Claude 持续开发提供可生成索引与自动架构守卫。

## 快速入口

- [项目索引](docs/PROJECT_INDEX.md)：模块、直接依赖、公共入口、路由、资源和 standalone 命令；优先从这里定位现有能力。
- [AI 协作规则](AGENTS.md)：AI 开发必须遵守的读取顺序、复用门槛、架构红线和完成标准。
- [组件化开发指南](FRAMEWORK_GUIDE.md)：模块归属、命名、UI、状态、网络、协程、测试和审查规范。
- 各模块目录下的 `README.md`：该模块的职责、允许依赖、公共入口和验证命令。

## 架构概览

```text
app                         正式应用组装、启动与主导航
├── module_login            登录业务与独立运行入口
├── module_home             首页业务与独立运行入口
├── module_mine             我的业务与独立运行入口
└── module_ota              OTA 业务与独立运行入口

core:*                      单一职责现代基础能力
legacy:*                    只减不增的历史兼容隔离区
build-logic                 Gradle 约定、独立组件与架构治理
```

依赖红线：core 不依赖业务；feature 默认只依赖 core，不直接依赖其他 feature；跨业务跳转复用 `core:navigation` 路由契约；app 是正式应用聚合层。完整清单见项目索引。

## 构建与治理

项目固定使用 JDK 17，依赖版本统一在 `gradle/libs.versions.toml`。首次在线构建下载完依赖后，可使用离线基线：

```bash
./gradlew verifyArchitecture verifyProjectIndex --offline
./gradlew testDebugUnitTest :app:assembleDebug --offline
```

模块、公共入口、路由或资源变化后重新生成索引：

```bash
./gradlew generateProjectIndex --offline
```

## 独立组件 APK

```bash
./gradlew :module_login:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:assembleDebug -Pstandalone=ota --offline
```

支持值仅为 `login/home/mine/ota`，未知值会在配置阶段失败。`mine` 独立包按业务需要同时组装 OTA 实现。

## 运行流程

正式应用：`SplashActivity` → 登录态判断 → `LoginActivity` → `MainActivity`。演示登录接受至少 4 位账号和至少 6 位密码；“我的”页面提供主题、语言、OTA 与退出登录示例。

构建成功不等于设备验收；OTA 安装权限、真实下载、WebView/TBS 和主题/语言视觉效果需要真机验证。
