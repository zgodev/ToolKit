# ToolKit 严格组件化架构设计

## 目标

将 ToolKit 从“业务模块依赖全量 `lib_common`”的示例工程，重构为可长期扩展的大型 Android 工程骨架：依赖方向单向、业务组件具有 `api/impl` 边界、构建约定集中管理，并支持通过 `-Pstandalone=<feature>` 独立运行单个业务组件。

## 全局约束

- 保持 Kotlin 1.9.22、AGP 8.2.2、Gradle 8.7、JDK 17、compileSdk/targetSdk 34、minSdk 21。
- 完整 App 的登录、主页、主题、多语言、WebView、OTA 行为保持不变。
- 保留 ARouter，不迁移到其他导航框架。
- 新增代码可使用 XML 或 Compose；已有 XML 页面不为迁移而重写成 Compose。
- 不触碰用户已有的 `.idea` 改动，不自动提交或推送。
- 默认依赖使用 `implementation`；只有公开 API 暴露的类型才使用 `api`。

## 目标模块拓扑

```text
:app                         产品组装、Splash、主 Tab 容器、正式 Application

:core:model                  纯模型，无 Android UI
:core:designsystem           主题资源、共享控件、LoadingDialog
:core:ui                     BaseActivity/Fragment/ViewModel、MVI、状态和 UI 扩展
:core:navigation             ARouter 封装，不依赖任何业务实现
:core:session                用户会话和 SessionEvents
:core:locale                 多语言切换
:core:network                Retrofit/OkHttp、响应模型、BaseRepository、Hilt 网络绑定
:core:startup                公共 Application、启动任务、Activity 栈、日志
:core:web                    WebActivity、WebViewPool、原生/X5 引擎
:core:utils                  小而聚焦、仍在使用的通用工具

:legacy:utils                大型历史 Java 工具和旧二进制依赖的隔离区
:legacy:network              旧 HttpUtil/WebSocket，仅依赖 legacy:utils

:module_login:api            登录路由和跨模块契约
:module_login:impl           登录 UI、Repository、DI
:module_home:api             首页路由和跨模块契约
:module_home:impl            首页三个业务 Tab
:module_mine:api             “我的”路由和跨模块契约
:module_mine:impl            MineFragment 与设置入口
:module_ota:api              OTA 路由契约
:module_ota:impl             OTA Activity、Manager、Worker
```

业务模块只能依赖 `core:*`、自己的 `api` 和其他业务的 `api`。`:app` 是唯一可以同时依赖多个业务 `impl` 的正式组装模块。独立运行时，指定业务 `impl` 临时应用 `com.android.application`，并只为该 APK 追加必要的其他 `impl`。

## 构建系统

新增 included build `build-logic`，提供以下 convention plugins：

- `toolkit.android.application`：统一 SDK、JDK、测试和 Kotlin 选项。
- `toolkit.android.library`：统一 Android Library 配置。
- `toolkit.android.feature`：根据 `-Pstandalone=<feature>` 在 Application/Library 间切换。
- `toolkit.android.hilt`：统一 Hilt + KSP。
- `toolkit.android.arouter`：统一 ARouter + kapt 参数。

依赖版本迁移到 `gradle/libs.versions.toml`。删除无效的全局 `isModule`，用单值属性避免多个 feature 同时变成 Application。

独立运行命令：

```bash
./gradlew :module_mine:impl:assembleDebug -Pstandalone=mine
./gradlew :module_login:impl:assembleDebug -Pstandalone=login
./gradlew :module_home:impl:assembleDebug -Pstandalone=home
./gradlew :module_ota:impl:assembleDebug -Pstandalone=ota
```

不传 `standalone` 时，所有 feature `impl` 均为 Library，`:app` 组装完整 APK。传入未知名称或从非目标 feature 执行 Application 任务时，构建应给出明确错误。

## 业务边界

`MainActivity` 和主 Tab 容器移入 `app`。它通过各 feature API 中的路由常量获取 Fragment，从而不直接引用 feature 实现类。`MineFragment`、布局和资源迁移到 `module_mine:impl`；OTA 独立为 `module_ota`，Mine 只依赖 OTA API。

每个 API 模块只允许放置：路由常量、跨模块数据结构、能力接口。Activity、Fragment、Repository、Hilt Module 和资源只能位于 impl。

## lib_common 拆分

- `base/mvi/state/ext` → `core:ui`。
- `theme` 和共享主题资源、`LoadingDialog`、原 `widget` → `core:designsystem`。
- `router` → `core:navigation`。
- `user` → `core:session`，用户模型 → `core:model`。
- `language` → `core:locale`。
- `CommonApplication/AppStartup/AppManager` → `core:startup`。
- `web` → `core:web`。
- `ota` → `module_ota:impl`。
- `BaseRepository/NetworkModule` → `core:network`。

迁移结束后删除 `lib_common`，不保留聚合依赖兼容层，防止新代码继续走全量依赖。

## 历史代码治理

现有大体量 Java 工具先进入 `legacy:utils`，旧 HttpUtil/WebSocket 进入 `legacy:network`。当前现代业务不依赖 legacy 模块；后续按真实需求逐个替换，而不是在本次架构迁移中高风险重写数千行历史逻辑。

`core:utils` 只保留当前核心 UI/启动代码实际使用的小工具。共享控件去除对 legacy `DipUtils` 的依赖。

## 构建可复现性与测试

- 将 `gradlew` 统一为 LF 并恢复可执行权限。
- 增加 `.java-version`，构建开始时校验 JDK 17，并为 Java/Kotlin 配置 17 toolchain。
- Convention plugin 为所有 Android 模块统一加入 JUnit 4 与 AndroidX Test。
- 删除无意义的模板测试，新增模型、会话、网络响应、Repository、独立运行选择器等行为测试。
- 验收至少包含完整 Debug APK、全量单元测试以及四个 standalone Debug APK。

## AI 配置

- 由 `CLAUDE.md` 生成项目级 `AGENTS.md`，将 Claude 专属路径改为 Codex 可识别约定。
- 完整迁移三个 `.claude/skills` 到 `.agents/skills`，修复 YAML frontmatter。
- 从 `CLAUDE.md` 和 `AGENTS.md` 删除“项目禁止 Compose”约束；保留“不要为了迁移而重写现有 XML 页面”。
- `.claude` 原配置继续保留，保证 Claude 与 Codex 都可使用。

## 验收标准

1. `./gradlew testDebugUnitTest` 成功。
2. `./gradlew :app:assembleDebug` 成功。
3. login/home/mine/ota 四个 standalone APK 均能构建。
4. 完整 App 中 Login、Home、Mine、OTA、Web 路由均可由 ARouter 解析。
5. 非 `app` 模块不同时依赖两个业务 impl；业务 API 不依赖业务 impl。
6. `lib_common`、无效 `isModule` 和模板 `addition_isCorrect` 测试全部移除。
7. `AGENTS.md` 和三个 `.agents/skills/*/SKILL.md` 可被 Codex 发现。
