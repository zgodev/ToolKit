# Single Feature Modules Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将四组 feature api/impl 合并为四个单一业务模块，同时保留统一构建约定、架构守卫和独立 APK 能力。

**Architecture:** 路由常量集中到 `core:navigation/RouterPath`；业务模块默认只依赖 core，app 聚合全部 feature；Mine standalone 可按白名单组装 OTA。`build-logic` 保留并从双层 feature 识别切换为单层 feature 识别。

**Tech Stack:** Gradle 8.7、AGP 8.2.2、Kotlin 1.9.22、JDK 17、JUnit 4、Gradle TestKit、ARouter。

## Global Constraints

- 不改动或提交用户现有 `.idea/migrations.xml`、`.idea/misc.xml` 和 `publish.gradle` 移动。
- 不引入新依赖，不升级 Gradle、AGP、Kotlin、KSP 或 Android SDK 版本。
- 四个 feature 均保持现有 namespace、资源前缀、业务行为和 standalone Application/Activity。
- 同一主题只提交一个 commit，不 push。

---

### Task 1: 先用测试定义单 Feature 架构

**Files:**
- Modify: `build-logic/src/test/kotlin/ArchitectureRulesTest.kt`
- Modify: `build-logic/src/test/kotlin/ProjectGovernanceCollectorTest.kt`
- Modify: `build-logic/src/test/kotlin/ProjectIndexTest.kt`

**Interfaces:**
- Consumes: `ArchitectureRules.validate(ArchitectureSnapshot)`、`ProjectGovernanceCollector.collectIndex(Project)`。
- Produces: `:module_<name>` 类型识别、feature 依赖白名单、单层 standalone 命令的回归契约。

- [x] 将测试数据由 `:module_xxx:api/impl` 改成 `:module_xxx`，新增常规 feature-to-feature 依赖失败、Mine standalone 到 OTA 成功的断言。
- [x] 运行 `./gradlew -p build-logic test --offline`，确认测试因生产规则仍识别 api/impl 而失败。

### Task 2: 修改 Convention Plugin、索引和架构守卫

**Files:**
- Modify: `build-logic/src/main/kotlin/AndroidFeatureConventionPlugin.kt`
- Modify: `build-logic/src/main/kotlin/ArchitectureRules.kt`
- Modify: `build-logic/src/main/kotlin/ArchitectureGuardPlugin.kt`
- Modify: `build-logic/src/main/kotlin/ProjectIndex.kt`
- Modify: `build-logic/README.md`

**Interfaces:**
- Consumes: `-Pstandalone=<login|home|mine|ota>` 和 `:module_<name>` Gradle project path。
- Produces: `toolkit.android.feature` 的 Library/Application 切换；单 feature 依赖校验；`:module_<name>:assembleDebug` 索引命令。

- [x] 将 feature 识别正则、模块类型、资源前缀、Mine→OTA 例外和 standalone 命令改为单层路径。
- [x] 保留 `toolkit.android.application/library/feature/kotlin.library/hilt/arouter` 与 `toolkit.architecture-guard`，删除所有 api/impl 专属错误分支。
- [x] 运行 `./gradlew -p build-logic test --offline`，确认 Task 1 的测试通过。

### Task 3: 合并路由与业务模块目录

**Files:**
- Modify: `core/navigation/src/main/java/com/zhangyt/common/router/RouterPath.kt`
- Modify: `core/navigation/src/main/java/com/zhangyt/common/router/RouterManager.kt`
- Modify: `app/src/main/**/*.kt`
- Modify: `app/src/test/java/com/zhangyt/toolkit/RouteContractTest.kt`
- Create: `module_login/build.gradle`, `module_home/build.gradle`, `module_mine/build.gradle`, `module_ota/build.gradle`
- Move: `module_*/impl/src` → `module_*/src`
- Move: `module_*/impl/consumer-rules.pro` → `module_*/consumer-rules.pro`
- Delete: `module_*/api`, `module_*/impl`
- Modify: `settings.gradle`, `app/build.gradle`

**Interfaces:**
- Consumes: `RouterPath.Login/Home/Mine/Ota` 和现有 feature 源码。
- Produces: `:module_login`、`:module_home`、`:module_mine`、`:module_ota` 四个业务模块。

- [x] 先把路由契约测试改为 `RouterPath` 分组，运行目标测试并确认因分组尚不存在而编译失败。
- [x] 在 `RouterPath` 增加四个业务分组，迁移全部 import 和注解引用。
- [x] 提升四个 impl 的源码、资源、Manifest、standalone 源集及 consumer rules，创建根构建脚本并移除 API 依赖。
- [x] 修改 settings 与 app 聚合依赖，运行 `./gradlew :app:testDebugUnitTest :app:assembleDebug --offline`。

### Task 4: 同步治理文档和模块索引

**Files:**
- Modify: `AGENTS.md`, `FRAMEWORK_GUIDE.md`, `README.md`, `CLAUDE.md`
- Create: `module_login/README.md`, `module_home/README.md`, `module_mine/README.md`, `module_ota/README.md`
- Modify: `core/navigation/README.md`, `app/README.md`
- Modify: `docs/superpowers/specs/2026-08-03-strict-component-architecture-design.md`
- Modify: `docs/superpowers/specs/2026-08-03-ai-governance-design.md`
- Modify: `docs/superpowers/plans/2026-08-03-strict-component-architecture.md`
- Modify: `docs/superpowers/plans/2026-08-03-ai-governance.md`
- Regenerate: `docs/PROJECT_INDEX.md`

**Interfaces:**
- Consumes: 已落地的单 feature 路径和验证命令。
- Produces: AI 可直接遵循的当前架构说明；旧设计文档的 superseded 标记；确定性项目索引。

- [x] 将当前规则中的 feature api/impl 改为单 feature，旧设计与计划顶部标记被本设计取代。
- [x] 为四个业务模块各写一个包含七个标准章节的 README。
- [x] 运行 `./gradlew generateProjectIndex verifyProjectIndex verifyArchitecture --offline`。

### Task 5: 全量验证、审计与提交

**Files:**
- Verify: all task files; do not include user-owned staged files.

**Interfaces:**
- Consumes: 完成迁移后的工程。
- Produces: 一个经过构建、测试、独立组件验证和暂存区审计的 commit。

- [x] 运行 `./gradlew -p build-logic clean build --offline`。
- [x] 运行 `./gradlew verifyArchitecture verifyProjectIndex --offline`。
- [x] 运行 `./gradlew testDebugUnitTest :app:assembleDebug --offline`。
- [x] 分别运行 Login、Home、Mine、OTA 的单层 standalone assemble 命令。
- [x] 运行 `git diff --check`、敏感信息扫描和暂存区范围检查。
- [x] 只提交本任务涉及的文件，保留用户现有 `.idea` 和 `publish.gradle` 状态，不 push。
