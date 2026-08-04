# `:module_ota`

OTA 业务模块，包含版本展示、后台下载、安装引导和独立运行入口。

## 模块职责

通过 WorkManager 执行 APK 下载、进度观察、文件校验与系统安装流程。

## 模块类型

feature（支持 `standalone=ota`）。

## 依赖规则

只依赖 OTA 所需的 `core:*` 和 AndroidX WorkManager 等三方库；不依赖其他业务模块。

## 目录结构

- `src/main/java/com/zhangyt/module/ota`：OTA Activity、Manager、Worker 与模型，包名与模块 namespace 一致。
- `src/main/res` 与 Manifest：OTA 资源和组件声明。
- `src/test`：公开 OTA 路由契约测试。
- `src/standalone`：独立 APK Manifest。

## 公共入口

`OtaActivity`、`OtaManager`、`OtaWorker`；跨模块路由为 `RouterPath.Ota.ACTIVITY_OTA`。

## 新代码放置

升级检查、下载和安装流程放在本模块；当前 `mockOtaInfo` 仅为演示数据，生产接入必须替换为真实且可验证的版本服务。

## 验证命令

`./gradlew :module_ota:testDebugUnitTest :module_ota:assembleDebug -Pstandalone=ota --offline`
