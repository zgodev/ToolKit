---
name: android-code-review
description: Performs a structured Android code review. Use when the user asks to review code, check a PR, look for bugs or leaks, audit for memory/lifecycle/threading issues, or evaluate whether code follows project conventions.
---

# Android 代码审查清单

按以下顺序检查，找到问题时给出具体行号 + 修改建议。

## 1. 生命周期与内存泄漏

- [ ] Composable 中的 `LaunchedEffect` / `DisposableEffect` 有合适的 key
- [ ] 观察 Flow 用 `collectAsStateWithLifecycle()`，不是裸 `collectAsState()`
- [ ] ViewModel 中的协程跑在 `viewModelScope`，不是 `GlobalScope`
- [ ] 没有把 Activity/Fragment/View 的引用传给长生命周期对象
- [ ] BroadcastReceiver、Listener、Callback 在 `onDestroy`/`onDispose` 里注销
- [ ] Service / WorkManager 任务能正确取消

## 2. 线程与协程

- [ ] 网络/数据库调用不在 `Dispatchers.Main`
- [ ] Repository 内部切 `Dispatchers.IO`，调用方不用管
- [ ] `runBlocking` 只出现在测试或 main 方法中
- [ ] `Flow.collect` 不在主线程做重计算
- [ ] 共享可变状态用 `Mutex` 或 `StateFlow`，不用 `@Volatile` + `synchronized`

## 3. 空安全与异常

- [ ] 没有滥用 `!!`（每一个 `!!` 都要能解释为什么不会为 null）
- [ ] 网络层抛出的异常在 Repository 转换成 `Result.failure` 或领域异常
- [ ] UI 层捕获异常后有用户可见的反馈（Snackbar/空态/重试）
- [ ] 没有裸 `catch (e: Exception)` 后默默吞掉

## 4. Compose 特定问题

- [ ] 不稳定参数（`List`、lambda）导致的不必要重组
- [ ] `LazyColumn` / `LazyRow` 的 items 带 `key`
- [ ] Composable 里没有直接创建 `ViewModel`、`Repository`
- [ ] `remember` 的 key 正确（依赖变化时会重新计算）
- [ ] 没有在 Composable 里调用副作用函数（日志、网络）

## 5. 架构合规

- [ ] domain 层无 Android 依赖
- [ ] DTO/Entity 没泄漏到 UI
- [ ] ViewModel 不持有 Context / View
- [ ] 业务逻辑在 UseCase，不在 ViewModel 或 Composable
- [ ] Repository 返回 Flow / suspend，不返回回调

## 6. 资源与国际化

- [ ] 字符串全部在 `strings.xml`，不硬编码
- [ ] 尺寸用 `dimens.xml` 或设计 token，不散落 `.dp`
- [ ] 颜色走主题，不直接用 hex
- [ ] 图片优先矢量（VectorDrawable）或 WebP

## 7. 测试覆盖

- [ ] 新 UseCase 有单元测试
- [ ] ViewModel 测试使用 `Turbine` 验证状态流
- [ ] Mapper 有往返测试（DTO → Domain → DTO）
- [ ] 关键用户流程有 Compose UI 测试

## 8. 性能 & APK 大小

- [ ] 没有在循环中创建对象（尤其是 `onDraw`、`measure`）
- [ ] 图片加载用 Coil 并指定 `size`，不让它加载原始分辨率
- [ ] 新增依赖不引入 >500KB 增量（用 APK Analyzer 验证）
- [ ] Release 构建启用 R8（`isMinifyEnabled = true`）

## 输出格式

按下面结构给出审查结果：

### 🔴 必须修复
（会导致 crash / 泄漏 / 数据错误的问题）

### 🟡 建议修改
（不符合项目规范，但不会立即出问题）

### 🟢 可以优化
（风格、可读性、小优化）

每条问题给出：**文件:行号** + **问题描述** + **建议的修改代码**。