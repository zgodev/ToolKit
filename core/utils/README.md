# `:core:utils`

现代工具模块保存边界清晰、无业务语义的小型 Android 工具，不作为通用杂物箱。

## 模块职责

提供日期、键盘和状态栏等可复用能力；复杂领域逻辑、状态和服务协调不放入工具模块。

## 模块类型

core（Android utility library）。

## 依赖规则

只依赖实现该能力必需的 AndroidX；禁止依赖 feature、网络、session 或 legacy。

## 目录结构

- `src/main/java/com/zhangyt/utils`：按具体能力命名的工具。
- `src/test`：纯逻辑和边界条件测试。

## 公共入口

`DateUtils`、`KeyboardUtils`、`StatusBarUtils`（历史名称保留，新类型使用更具体的能力名）。

## 新代码放置

先在索引中检索已有能力；仅在至少两个调用方需要且 API 边界明确时新增，禁止创建 `CommonUtils`。

## 验证命令

`./gradlew :core:utils:testDebugUnitTest :core:utils:assembleDebug --offline`
