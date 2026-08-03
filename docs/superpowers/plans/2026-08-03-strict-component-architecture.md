# ToolKit Strict Component Architecture Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把 ToolKit 重构为具有 core/legacy/feature api-impl 边界、Convention Plugin 和单 feature 独立 APK 能力的大型 Android 工程骨架。

**Architecture:** `build-logic` 统一构建约定，`:app` 负责产品组装，业务模块使用 `api/impl`，共享能力进入小粒度 `core:*`，历史实现隔离在 `legacy:*`。`-Pstandalone=<feature>` 只把目标 impl 切换为 Application。

**Tech Stack:** Gradle 8.7、AGP 8.2.2、Kotlin 1.9.22、Groovy/Kotlin Gradle Plugin、Hilt 2.51.1、KSP、ARouter、JUnit 4。

## Global Constraints

- 使用 JDK 17、compileSdk/targetSdk 34、minSdk 21。
- 保持现有产品行为；不替换 ARouter，不为迁移重写 XML UI。
- 默认 `implementation`，仅公开 API 使用 `api`。
- 不修改 `.idea` 用户改动，不自动提交和推送。
- 每个阶段结束运行对应测试或构建任务。

---

### Task 1: 建立可复现构建基线

**Files:**
- Modify: `gradlew`
- Create: `.java-version`
- Create: `gradle/libs.versions.toml`
- Create: `build-logic/settings.gradle.kts`
- Create: `build-logic/build.gradle.kts`
- Create: `build-logic/src/main/kotlin/*.kt`
- Modify: `settings.gradle`, `build.gradle`, `gradle.properties`

**Interfaces:**
- Produces: `toolkit.android.application`、`toolkit.android.library`、`toolkit.android.feature`、`toolkit.android.hilt`、`toolkit.android.arouter`。

- [ ] 修复 wrapper 行尾与权限，记录 JDK 17。
- [ ] 先运行旧测试，保留缺少 JUnit 的失败证据。
- [ ] 创建 version catalog 和 convention plugins，统一测试依赖与 Java/Kotlin 17。
- [ ] 运行 `./gradlew help`，确认构建配置可加载。

### Task 2: 拆分 core 与 legacy

**Files:**
- Create: `core/{model,designsystem,ui,navigation,session,locale,network,startup,web,utils}`
- Create: `legacy/{utils,network}`
- Remove after migration: `lib_common`, `network`, `utils`, `widget`

**Interfaces:**
- Produces: 小粒度 core 模块；`BaseRepository` 位于 network；`CoreApplication` 位于 startup。

- [ ] 为 BaseResponse、Repository 解包和会话行为添加失败测试。
- [ ] 迁移模型、网络、UI、导航、会话、语言、启动、Web 和设计系统代码。
- [ ] 将旧大工具与旧网络实现移动到 legacy，现代模块不依赖 legacy。
- [ ] 运行 `./gradlew testDebugUnitTest`，逐项修正依赖边界。

### Task 3: 建立业务 api/impl 与 Mine/OTA 边界

**Files:**
- Create: `module_{login,home,mine,ota}/{api,impl}`
- Move: 现有 login/home/mine/ota 源码与资源
- Move: `MainActivity` 和主 Tab 容器到 `app`

**Interfaces:**
- Produces: `LoginRoutes`、`HomeRoutes`、`MineRoutes`、`OtaRoutes`；各 Fragment/Activity 仅在 impl 注册。

- [ ] 先添加路由唯一性和主 Tab 路由解析测试。
- [ ] 创建四个 API 模块并迁移路由常量。
- [ ] 创建四个 impl 模块；将 MineFragment 移到 Mine impl，将 OTA 移到 OTA impl。
- [ ] 将产品 MainActivity 移到 app，只通过路由获取 feature Fragment。
- [ ] 运行完整 App Debug 构建。

### Task 4: 实现单组件独立运行

**Files:**
- Modify: feature convention plugin
- Create: 每个 impl 的 `src/standalone/AndroidManifest.xml`、Application 和 launcher Activity
- Modify: `app/build.gradle`

**Interfaces:**
- Consumes: Gradle 属性 `standalone=login|home|mine|ota`。
- Produces: 目标 impl 的 Application variant 和 `BuildConfig.STANDALONE`。

- [ ] 添加 Gradle TestKit 测试：未知 feature 被拒绝、只选择一个目标、默认均为 library。
- [ ] 实现 feature 模式选择和 app 依赖隔离。
- [ ] 为四个 feature 提供独立入口与最小演示初始化。
- [ ] 分别构建四个 standalone Debug APK。

### Task 5: 文档与 AI 配置

**Files:**
- Modify: `README.md`, `FRAMEWORK_GUIDE.md`, `CLAUDE.md`, `.gitignore`
- Create: `AGENTS.md`
- Create: `.agents/skills/{android-code-review,compose-ui,xml-ui}/SKILL.md`

**Interfaces:**
- Produces: Claude/Codex 双运行时一致的项目指导和技能。

- [ ] 更新模块图、JDK、构建和 standalone 命令。
- [ ] 删除 Compose 禁止项，改为现有 XML 不做无目的迁移。
- [ ] 迁移并规范三个 Skills 的 frontmatter。
- [ ] 检查所有相对路径、版本号和模块名称。

### Task 6: 全量验证与边界审计

**Files:**
- Verify only: entire repository

**Interfaces:**
- Consumes: 所有前置任务产物。
- Produces: 构建、测试和依赖边界验收记录。

- [ ] 运行 `./gradlew testDebugUnitTest`。
- [ ] 运行 `./gradlew :app:assembleDebug`。
- [ ] 构建 login/home/mine/ota 四个 standalone APK。
- [ ] 使用 `rg` 审计 `lib_common`、无效 `isModule`、业务 impl 交叉依赖和模板测试。
- [ ] 检查 `git diff --check`、`git status`，确认未覆盖 `.idea` 用户改动。
