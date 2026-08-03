# `:module_ota:impl`

OTA 实现模块负责版本信息、后台下载、文件共享与安装引导，并支持独立 APK 验证。

## 模块职责

实现升级页面、WorkManager 下载流程和安装权限交互。当前 `OtaActivity` 内置硬编码 `mockOtaInfo` 作为演示数据，尚未接入可配置的发布服务。

## 模块类型

feature impl（支持 `standalone=ota`）。

## 依赖规则

允许依赖自身 api、网络/UI/导航/设计系统等 core；禁止依赖其他 feature impl。

## 目录结构

- `src/main/java`：升级页面、模型、协调器与 Worker。
- `src/main/res`：`ota_*` 页面和 FileProvider 资源。
- `src/standalone`：独立 Launcher Manifest。

## 公共入口

`OtaActivity`（通过 `OtaRoutes` 路由）、`OtaManager`、`OtaWorker`；跨模块仅使用 api 路由。

## 新代码放置

升级下载、校验、安装流程留在本模块；通用 HTTP 机制复用 `core:network`，不要新建平行下载框架。接入生产时先用 Repository/配置替换 `mockOtaInfo`，不得继续散落真实制品地址和校验值。

## 验证命令

`./gradlew :module_ota:impl:testDebugUnitTest :module_ota:impl:assembleDebug -Pstandalone=ota --offline`
