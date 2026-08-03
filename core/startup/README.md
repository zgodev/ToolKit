# `:core:startup`

启动模块提供可复用 Application 基类和进程级基础设施初始化，不编排具体业务首页。

## 模块职责

统一初始化路由、日志、存储、主题、多语言和生命周期基础能力，并向正式与 standalone Application 复用。

## 模块类型

core（Android startup library）。

## 依赖规则

允许依赖启动所需的单一职责 core 和基础三方库；禁止依赖 feature 或 legacy 业务实现。

## 目录结构

- `src/main/java/com/zhangyt/common`：Application 基类和启动协调。
- `src/test`：初始化顺序与幂等契约测试。

## 公共入口

`CommonApplication`、`AppStartup`。

## 新代码放置

仅添加所有 Application 都需要的进程级初始化；可选能力应暴露独立 Initializer，由组装层显式调用。

## 验证命令

`./gradlew :core:startup:testDebugUnitTest :core:startup:assembleDebug --offline`
