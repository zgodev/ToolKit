---
name: android-code-review
description: Use when reviewing Android code, pull requests, memory or lifecycle risks, threading and coroutine behavior, architecture boundaries, UI correctness, performance, or project convention compliance.
---

# Android 代码审查

按严重度输出带文件与行号的可执行问题；先验证真实调用链，不根据命名猜测。

## 检查顺序

1. 生命周期与泄漏：作用域、监听注销、Context/View 引用、WorkManager 取消。
2. 线程与协程：主线程阻塞、结构化并发、Flow 收集生命周期、共享状态竞争。
3. 空安全与异常：`!!` 不变量、异常吞噬、用户可见反馈、网络错误映射。
4. UI：XML/Compose 生命周期、状态提升、重组、无障碍、主题与资源前缀。
5. 架构：core 不依赖 feature；feature 仅通过 api 协作；app 才聚合 impl。
6. 数据边界：DTO 不泄漏到 UI，Repository 使用 suspend/Flow，ViewModel 不持有 View。
7. 测试：新行为有回归覆盖，异步状态断言稳定，测试不是模板占位。
8. 性能与产物：循环分配、图片尺寸、依赖体积、Release R8、启动关键路径。

## Compose 附加项

- 使用 `collectAsStateWithLifecycle()`；Effect 和 `remember` 的 key 与依赖一致。
- Lazy 列表提供稳定 key；Composable 内不直接发网络请求或创建 Repository。
- 参数稳定性和 lambda 不造成无意义重组。

## 输出格式

### 必须修复

会导致 crash、泄漏、数据错误、安全问题或组件边界失效。

### 建议修改

明确的可维护性、测试、性能或项目规范问题。

### 可以优化

不改变正确性的可读性与小幅优化。

每条包含：`文件:行号`、证据、影响、最小修改建议。没有发现问题时明确说明已检查范围和残余风险。
