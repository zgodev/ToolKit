# CLAUDE.md

本文档给 AI 协作者（Claude Code / Copilot 等）使用，帮助快速理解 ToolKit 项目的约定与边界。人也可以直接读。

> 指导原则：**解释清楚现状，给出偏好，留出判断空间**。不把 AI 当成只会照抄的机器人。遇到模糊场景，优先看代码再问。

---

## 1. 项目速览

**ToolKit** 是一个组件化的 Android 基础框架示例，目标是"拿来即用的中大型项目骨架"。

- 包名：`com.zhangyt.toolkit`
- 产品形态：原生 Android App（单 APK，未来可能拆动态模块）
- 语言：Kotlin 为主，少量遗留 Java（`network/httputil`、`network/websocket`）

---

## 2. 工具链与版本

| 项 | 值 |
|---|---|
| AGP | 8.2.2 |
| Gradle Wrapper | 8.7 |
| Kotlin | 1.9.22（K1 编译器） |
| KSP | 1.9.22-1.0.17 |
| Hilt | 2.51.1 |
| JDK | 17（source/target/jvmTarget 均 17） |
| compileSdk / targetSdk | 34 |
| minSdk | 21 |
| Retrofit / OkHttp | 2.11.0 / 4.12.0 |
| Coroutines | 1.7.3 |
| Android Studio | Hedgehog / Iguana 及以上（AGP 8.2 要求） |

**依赖集中在 [config.gradle](config.gradle)**，还未迁移到 Version Catalog（后续计划）。**不要**擅自升级这里的版本号，除非任务明确要求，或因兼容性必须同步升级。

---

## 3. 模块拓扑

```
app
 ├── module_login ──┐
 ├── module_home ───┼──► lib_common ──► network / utils / widget
 └── module_mine  ──┘
```

- `app`：壳工程，集成所有业务模块，只放 Application / SplashActivity / 项目级初始化
- `lib_common`：Base 基类、ARouter、主题、多语言、MMKV、Glide、XLog、Hilt NetworkModule、SessionEvents …
- `network`：Retrofit/OkHttp/WebSocket，与业务解耦（可独立发布）
- `utils`：纯工具类 + 部分历史 Java 工具，允许依赖少量 androidx
- `widget`：自定义 View
- `module_login` / `module_home` / `module_mine`：业务模块，通过 ARouter 暴露入口

**新增业务模块**的步骤：见 [FRAMEWORK_GUIDE.md](FRAMEWORK_GUIDE.md)。


---

## 4. 技术选型（当前在用）

| 领域 | 选型 | 备注 |
|---|---|---|
| UI | **XML + ViewBinding + DataBinding** | 项目明确 **不** 用 Compose。涉及 UI 改动时保持 XML。 |
| 架构 | MVVM 为主，`mvi/` 有骨架 | 新页面用 MVVM；复杂状态流场景可走 MVI |
| DI | **Hilt**（KSP） | `app/lib_common/module_login` 已接入；其他模块按需挂插件 |
| 注解处理 | ARouter → **kapt**；其他（Glide/Hilt/Room） → **KSP** | ARouter 不支持 KSP |
| 组件化路由 | ARouter 1.5.2 | 已停维护但项目有历史惯性；未来可能换 TheRouter |
| 网络 | Retrofit + OkHttp + Coroutines Flow | `BaseRepository.request { api.xxx() }` 自动解包 `BaseResponse` |
| 持久化 | MMKV | 用 `MMKV.defaultMMKV()` 的地方比较多 |
| 图片 | Glide 4.16.0（KSP 注解） | 不与 OkHttp 共连接池（本轮决策） |
| 日志 | XLog 1.11.1 | 5MB/日，7 天清理；`initXLog` 已挪到后台协程 |
| 屏幕适配 | AutoSize | 360dp 宽度基准；`BaseActivity.onResume` 一次 convert |
| RecyclerView 适配器 | BRVAH 3.0.7 | 惯例 |
| 事件 | EventBus + `SessionEvents`（SharedFlow） | 新代码优先 `SharedFlow` |

---

## 5. 代码约定（偏好，不是硬规则）

### Kotlin

- 优先协程 + Flow；避免 `GlobalScope`，用 `viewModelScope` / `lifecycleScope`
- `when` 尽量穷尽；sealed class 适合状态/事件建模
- 空安全比 `!!` 好 —— 真要 `!!` 写个注释说明不变量
- 数据类放在各自 feature 的 `model/` 或 `api/` 下，Retrofit DTO 必须在 `consumer-rules.pro` keep 住

### ViewModel / Repository

- **保留 LiveData 兼容老代码**，新 VM 推荐 **StateFlow + `launchFlow`**（见 `BaseViewModel` 的 KDoc 示例）
- 业务数据用自己的 `StateFlow<T>` / `LiveData<T>`，`loadState*` 只控 Loading/Error UI
- Repository 构造依赖走 Hilt `@Inject constructor`，别再 `RetrofitClient.create` 硬编码（老代码保留，不用集中改）

### Activity / Fragment

- 继承 `BaseActivity<VB>` / `BaseFragment<VB>`，重写 `initView / initData / observeViewModel`
- 业务里需要参与 Hilt 的 Activity 都要加 `@AndroidEntryPoint`
- Dialog / Loading 相关一律用 `showLoading / hideLoading`，别自己 new 新的 `LoadingDialog`
- 路由跳转统一走 `RouterManager`，禁止 `startActivity(Intent(...))` 跨模块

### 资源

- 每个 module 有 `resourcePrefix`（`common_` / `home_` / `login_` / `test_` / `widget_`），新增资源请保持前缀
- 布局颜色/背景优先用 `?attr/common_color_xxx` 而不是写死色值，保证主题切换生效

### 网络

- API 接口放在各 feature 的 `api/` 目录，方法统一 `suspend` + 返回 `BaseResponse<T>`
- Token 通过 `NetworkConfig.tokenProvider` 设置一次，别在每个 interceptor 里重复
- 401 由 `TokenInterceptor` → `NetworkConfig.onUnauthorized` → `SessionEvents.emitLogout()` 自动处理，不要在业务代码里手写登出跳转

---

## 6. 和 AI 协作的建议

### ✅ 鼓励
- 修 Bug / 重构时，先读相关文件再动手；不要凭命名猜实现
- 拿不准的决策（版本升级、插件引入、架构微调）**先问再做**
- 小改动直接做；涉及多模块 / 多文件的请先产出计划
- 在本文档没覆盖的地方用常识；遇到反直觉的历史代码，先 `git blame / git log` 看意图

### ❌ 别做
- 不要把 UI 改成 Compose
- 不要把 ARouter 换掉（哪怕你觉得 TheRouter/Navigation 更好）
- 不要在根 `build.gradle` / `settings.gradle` 里加回那些 http 镜像、`allowInsecureProtocol`（e-iceblue 那条是唯一例外）
- 不要在业务代码里写 `RetrofitClient.create(...)`（Hilt 链路已经铺好）；老代码保留
- 不要主动启用/升级 Kotlin 2.0 或 KSP2，除非任务明确提出

### 🤔 可以聊
- Version Catalog 迁移
- ARouter → TheRouter 渐进迁移
- AutoSize 换 sw<>dp 资源限定符
- LeakCanary 接入（仅 debug）
- CI/CD（目前没有）

---

## 7. 文档导航

- [README.md](README.md) — 项目简介、能力表
- [FRAMEWORK_GUIDE.md](FRAMEWORK_GUIDE.md) — 详细上手指南、MVI 模板、主题/多语言扩展
- 本文 — 约定 + 和 AI 协作的指引

每轮较大规模的优化会写在 `~/.claude/plans/` 下，可以问我要最新一轮的计划文件。

---

_Last updated: 2026-04 — Wave 3 (Base 基类 + 启动优化) 完成后。_
