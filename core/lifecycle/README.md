# `:core:lifecycle`

生命周期模块维护进程内 Activity 栈等轻量生命周期能力，不处理页面业务或导航策略。

## 模块职责

集中跟踪 Activity 生命周期并提供应用级栈操作，供启动、主题和语言切换等基础能力复用。

## 模块类型

core（Android library）。

## 依赖规则

可依赖 AndroidX 生命周期基础库；禁止依赖 feature、legacy 和业务实现。

## 目录结构

- `src/main/java`：生命周期观察与 Activity 栈管理。
- `src/test`：不依赖设备的生命周期规则测试。

## 公共入口

`AppManager`。

## 新代码放置

仅放跨模块通用的生命周期协调；页面自己的监听与状态留在页面或 ViewModel。

## 验证命令

`./gradlew :core:lifecycle:testDebugUnitTest :core:lifecycle:assembleDebug --offline`
