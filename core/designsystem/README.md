# `:core:designsystem`

设计系统模块提供共享主题、颜色、文案与通用 View，不承载具体业务页面。

## 模块职责

统一跨 feature 的视觉 token、主题切换、Loading、对话框和基础控件；业务专属资源归所属 feature。

## 模块类型

core（Android UI library）。

## 依赖规则

允许依赖 Android UI 基础库和 `core:lifecycle`；禁止依赖任意 feature 或业务数据层。

## 目录结构

- `src/main/common-res`：推荐使用的 `common_*` 共享资源。
- `src/main/res`：待逐步迁移的历史共享控件资源。
- `src/main/java`：主题与通用控件。

## 公共入口

`ThemeManager`、`LoadingDialog`、`TitleBar`、`NormalDialog`、`NodeProgressBar` 及 `common_*` 资源。

## 新代码放置

先复用现有 token 和控件；只有至少两个 feature 真实共用的视觉能力才新增，并使用 `common_*` 资源前缀。

## 验证命令

`./gradlew :core:designsystem:testDebugUnitTest :core:designsystem:assembleDebug --offline`
