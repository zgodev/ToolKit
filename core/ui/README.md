# `:core:ui`

UI 基础模块提供 Activity、Fragment、ViewModel、MVI 契约和通用扩展，不实现业务界面。

## 模块职责

统一 ViewBinding 基类、生命周期安全的状态收集和通用 View 扩展，减少 feature 页面样板代码。

## 模块类型

core（Android UI foundation library）。

## 依赖规则

允许依赖 AndroidX UI、协程和 `core:designsystem`；禁止依赖 feature、网络实现和 legacy。

## 目录结构

- `base`：Activity、Fragment、ViewModel 与 ViewBinding 基础设施。
- `mvi`、`state`：状态契约。
- `ext`：边界明确的 UI/Flow 扩展。

## 公共入口

`BaseActivity`、`BaseFragment`、`BaseViewModel`、`MviViewModel`、`MviContract`、`UiLoadState`。

## 新代码放置

页面优先复用现有基类和扩展；没有两个真实使用方的页面辅助代码留在 feature，禁止新增笼统 `Utils`。

## 验证命令

`./gradlew :core:ui:testDebugUnitTest :core:ui:assembleDebug --offline`
