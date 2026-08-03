# `:module_home:impl`

首页实现模块提供聊天、联系人和发现页签及其列表状态，并支持独立 APK 验证。

## 模块职责

实现首页业务页面、适配器、模型和 ViewModel；顶层导航组合仍由 app 负责。

## 模块类型

feature impl（支持 `standalone=home`）。

## 依赖规则

允许依赖自身 api 与所需 core；禁止依赖其他 feature impl，跨 feature 行为使用对方 api。

## 目录结构

- `fragment`、`viewmodel`、`adapter`、`model`：首页内部实现。
- `src/main/res`：`home_*` 资源。
- `src/standalone`：独立 Launcher。

## 公共入口

`ChatFragment`、`ContactsFragment`、`DiscoverFragment` 由路由发现；跨模块入口以 `HomeRoutes` 为准。

## 新代码放置

首页专属页面与状态放本模块现有分层；只有稳定跨模块契约才进入 api，可复用 UI 先查 design system/ui。

## 验证命令

`./gradlew :module_home:impl:testDebugUnitTest :module_home:impl:assembleDebug -Pstandalone=home --offline`
