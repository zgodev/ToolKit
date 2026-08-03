# ToolKit AI Development Governance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 建立模块 README、可生成的项目索引、统一开发规范和可自动失败的架构守卫，让 Codex 在扩展 ToolKit 时先复用、遵守边界并保持命名一致。

**Architecture:** 在 `build-logic` 中实现纯 Kotlin 索引渲染与架构规则，再由根项目 convention plugin 注册生成/校验任务。项目特定判断集中在 `AGENTS.md`、`FRAMEWORK_GUIDE.md` 和模块 README；`docs/PROJECT_INDEX.md` 由工具生成并通过 freshness 校验防止漂移。

**Tech Stack:** Gradle 8.7、Kotlin DSL、Gradle Plugin API、JUnit 4、Android Gradle Plugin 8.2.2、Markdown。

## Global Constraints

- 不改变登录、首页、我的、OTA 和 Web 的业务行为。
- 不修改或暂存 `.idea/migrations.xml` 与 `.idea/misc.xml`。
- 不引入新的外部依赖；测试只使用 JUnit 4 和 Gradle 已有 API。
- 项目规则以 `AGENTS.md` 为最高项目级事实来源，机械规则由 `verifyArchitecture` 执行。
- 用户已明确要求整个任务完成并验证后创建一个 commit，不推送远端。
- 资源重命名只覆盖低风险的 app 壳、Web 和 OTA 页面资源；design system/legacy 的历史命名进入固定例外，不扩大清理范围。

---

### Task 1: 项目索引模型与确定性渲染

**Files:**
- Modify: `build-logic/build.gradle.kts`
- Create: `build-logic/src/main/kotlin/ProjectIndex.kt`
- Create: `build-logic/src/test/kotlin/ProjectIndexTest.kt`

**Interfaces:**
- Produces: `ModuleIndexEntry`、`ResourceIndexEntry`、`RouteIndexEntry`、`ProjectIndexRenderer.render(...)`。
- Output contract: 相同输入始终生成字节一致、以单个换行结尾的 Markdown。

- [x] **Step 1: 写入失败测试**

覆盖模块排序、依赖排序、README 摘要、资源去重、路由排序和重复执行稳定性：

```kotlin
@Test
fun `render sorts every section deterministically`() {
    val first = ProjectIndexRenderer.render(fixture.reversed())
    val second = ProjectIndexRenderer.render(fixture)
    assertEquals(second, first)
    assertTrue(first.endsWith("\n"))
}
```

- [x] **Step 2: 运行测试并确认 RED**

Run: `./gradlew -p build-logic test --tests ProjectIndexTest --offline`

Expected: FAIL，原因是索引模型和渲染器尚不存在。

- [x] **Step 3: 实现最小索引模型与渲染器**

`ProjectIndex.kt` 只负责不可变数据模型、稳定排序和 Markdown 渲染，不读取 Gradle Project 或文件系统。

- [x] **Step 4: 运行测试并确认 GREEN**

Run: `./gradlew -p build-logic test --tests ProjectIndexTest --offline`

Expected: PASS。

### Task 2: 架构规则与失败诊断

**Files:**
- Create: `build-logic/src/main/kotlin/ArchitectureRules.kt`
- Create: `build-logic/src/test/kotlin/ArchitectureRulesTest.kt`

**Interfaces:**
- Consumes: Task 1 的索引数据类型。
- Produces: `ArchitectureSnapshot`、`ArchitectureViolation`、`ArchitectureRules.validate(snapshot)`。
- Error contract: 每条违规包含规则编号、模块/文件位置和修复方向。

- [x] **Step 1: 写入依赖边界失败测试**

分别覆盖 core→feature、api→impl、feature impl→other impl、允许的 mine standalone→ota impl：

```kotlin
@Test
fun `core cannot depend on feature`() {
    val violations = validate(dependency(":core:ui", ":module_home:api"))
    assertEquals("ARCH_DEP_CORE_FEATURE", violations.single().ruleId)
}
```

- [x] **Step 2: 写入文档、资源和模板测试失败测试**

覆盖缺 README、README 缺必需章节、错误资源前缀、`ExampleUnitTest` 和过期索引。

- [x] **Step 3: 运行测试并确认 RED**

Run: `./gradlew -p build-logic test --tests ArchitectureRulesTest --offline`

Expected: FAIL，原因是规则实现尚不存在。

- [x] **Step 4: 实现规则并保持纯函数**

固定规则 ID：

- `ARCH_DEP_CORE_FEATURE`
- `ARCH_DEP_API_IMPL`
- `ARCH_DEP_IMPL_IMPL`
- `ARCH_DEP_DIRECTION`
- `ARCH_BUILD_FILE_MISSING`
- `ARCH_README_MISSING`
- `ARCH_README_SECTION`
- `ARCH_RESOURCE_PREFIX`
- `ARCH_TEMPLATE_TEST`
- `ARCH_INDEX_STALE`

- [x] **Step 5: 运行测试并确认 GREEN**

Run: `./gradlew -p build-logic test --tests ArchitectureRulesTest --offline`

Expected: PASS。

### Task 3: Gradle 架构守卫插件

**Files:**
- Modify: `build-logic/build.gradle.kts`
- Create: `build-logic/src/main/kotlin/ArchitectureGuardPlugin.kt`
- Create: `build-logic/src/test/kotlin/ArchitectureGuardPluginTest.kt`
- Modify: `build.gradle`

**Interfaces:**
- Consumes: Task 1/2 的渲染和验证接口。
- Produces Gradle tasks: `generateProjectIndex`、`verifyProjectIndex`、`verifyArchitecture`。
- `generateProjectIndex` writes: `docs/PROJECT_INDEX.md`。

- [x] **Step 1: 写入插件注册失败测试**

使用 `ProjectBuilder` 应用插件，断言三个任务存在、group 为 `verification`/`documentation`。`verifyArchitecture` 自身包含 freshness 规则但不依赖 `verifyProjectIndex`，以便一次输出全部违规而不是被首个 stale 错误提前截断。

- [x] **Step 2: 运行测试并确认 RED**

Run: `./gradlew -p build-logic test --tests ArchitectureGuardPluginTest --offline`

Expected: FAIL，插件尚不存在。

- [x] **Step 3: 实现项目快照采集**

插件在所有项目完成配置后采集：

- `rootProject.subprojects` 的 path、projectDir、buildFile 和 project dependencies。
- `build-logic` 作为独立构建模块的 README 与公共插件入口。
- `src/main/java|kotlin` 的公共类型名和路由常量。
- `src/main/res`、`src/main/common-res` 的资源文件与 values 资源名。
- 模块 README 第一段职责摘要。

采集结果按绝对路径无关的相对路径建模，确保不同机器生成一致。

- [x] **Step 4: 注册插件并应用到根项目**

在 `gradlePlugin` 注册 `toolkit.architecture-guard`，根 `build.gradle` 使用 plugins block 应用。

- [x] **Step 5: 运行插件测试与 validatePlugins**

Run: `./gradlew -p build-logic test validatePlugins --offline`

Expected: PASS。

### Task 4: 统一低风险资源命名

**Files:**
- Rename: `app/src/main/res/layout/home_activity_main.xml` → `app_activity_main.xml`
- Rename: `app/src/main/res/menu/home_bottom_menu.xml` → `app_menu_bottom.xml`
- Rename: `app/src/main/res/color/home_tab_color.xml` → `app_color_tab.xml`
- Modify: `app/src/main/java/com/zhangyt/toolkit/MainActivity.kt`
- Modify: the renamed app XML resources
- Rename: `core/web/src/main/res/layout/activity_web.xml` → `web_activity.xml`
- Modify: `core/web/src/main/java/com/zhangyt/common/web/WebActivity.kt`
- Rename: `module_ota/impl/src/main/res/layout/test_activity_ota.xml` → `ota_activity_update.xml`
- Rename: `module_ota/impl/src/main/res/xml/test_file_paths.xml` → `ota_file_paths.xml`
- Modify: `module_ota/impl/src/main/java/com/zhangyt/common/ota/OtaActivity.kt`
- Modify: `module_ota/impl/src/main/AndroidManifest.xml`
- Modify: `module_ota/impl/src/standalone/AndroidManifest.xml`
- Modify: `module_mine/impl/src/standalone/AndroidManifest.xml`

**Interfaces:**
- Produces ViewBinding types: `AppActivityMainBinding`、`WebActivityBinding`、`OtaActivityUpdateBinding`。
- Behavior remains unchanged; only generated binding/resource identifiers change。

- [x] **Step 1: 扩展现有路由/资源契约测试形成 RED**

在 `RouteContractTest.kt` 增加关键 binding/layout 资源存在性断言，先引用新名称并确认编译失败。

- [x] **Step 2: 执行资源重命名与引用更新**

保留图标二进制和 design system 历史 values 名称，在架构规则中以固定例外冻结。

- [x] **Step 3: 运行 app 与 OTA 编译确认 GREEN**

Run: `./gradlew :app:testDebugUnitTest :app:assembleDebug :module_ota:impl:assembleDebug -Pstandalone=ota --offline`

Expected: PASS。

### Task 5: 为每个模块补齐 README

**Files:**
- Create: `app/README.md`
- Create: `build-logic/README.md`
- Create: `core/{model,lifecycle,designsystem,ui,navigation,session,locale,startup,web,network,utils}/README.md`
- Create: `legacy/{network,utils}/README.md`
- Create: `module_{login,home,mine,ota}/{api,impl}/README.md`

**Interfaces:**
- Consumes: 设计说明定义的七段 README 契约。
- Produces: 索引生成器使用的模块职责第一段和公共入口说明。

- [x] **Step 1: 运行缺 README 校验确认 RED**

Run: `./gradlew verifyArchitecture --offline`

Expected: FAIL，列出全部缺失 README。

- [x] **Step 2: 按真实模块内容逐一编写 README**

每份包含以下固定标题：

```markdown
## 模块职责
## 模块类型
## 依赖规则
## 目录结构
## 公共入口
## 新代码放置
## 验证命令
```

README 只列真实入口，不复制整个项目索引。

- [x] **Step 3: 运行 README 规则确认 GREEN**

Run: `./gradlew verifyArchitecture --offline`

Expected: README 相关规则通过；允许索引 stale 规则暂时失败。

### Task 6: 生成项目索引并校验 freshness

**Files:**
- Create: `docs/PROJECT_INDEX.md`（由任务生成）

**Interfaces:**
- Consumes: 所有模块 README、依赖、源码入口、路由与资源。
- Produces: AI 首选快速定位入口。

- [x] **Step 1: 生成第一版索引**

Run: `./gradlew generateProjectIndex --offline`

- [x] **Step 2: 连续生成验证确定性**

记录第一次文件摘要，再次运行同一任务，确认摘要不变。

- [x] **Step 3: 运行 freshness 校验**

Run: `./gradlew verifyProjectIndex --offline`

Expected: PASS。

### Task 7: 强化 AI 规则与开发指南

**Files:**
- Modify: `AGENTS.md`
- Modify: `FRAMEWORK_GUIDE.md`
- Modify: `README.md`
- Modify: `CLAUDE.md`

**Interfaces:**
- `AGENTS.md` produces mandatory AI workflow and new-file gate。
- `FRAMEWORK_GUIDE.md` produces full human/AI coding reference。
- Root `README.md` remains navigation only。

- [x] **Step 1: 扩展 AGENTS 强制工作流**

加入：读取顺序、索引优先搜索、复用决策树、三问新增文件门槛、依赖红线、命名规则、完成定义和 `verifyArchitecture`。

- [x] **Step 2: 完善完整开发规范**

覆盖模块归属、package、Kotlin、XML/Compose、状态、网络、错误、协程、日志、资源、测试、文档、Git 范围和代码审查清单。

- [x] **Step 3: 更新入口文档**

根 README 链接 `docs/PROJECT_INDEX.md`、模块 README 和开发指南；CLAUDE 继续以 AGENTS 为唯一事实来源。

- [x] **Step 4: 重新生成索引**

如果 README 第一段变化，运行 `./gradlew generateProjectIndex --offline`。

### Task 8: 最终验证与范围审计

**Files:**
- Verify only; do not modify `.idea/*`。

**Interfaces:**
- Produces: 完整验证记录和未提交工作区。

- [x] **Step 1: 运行 build-logic 测试**

Run: `./gradlew -p build-logic build --offline`

Expected: PASS，包括 `validatePlugins`。

- [x] **Step 2: 运行架构与索引校验**

Run: `./gradlew verifyArchitecture verifyProjectIndex --offline`

Expected: PASS。

- [x] **Step 3: 运行项目测试与主应用构建**

Run: `./gradlew testDebugUnitTest :app:assembleDebug --offline`

Expected: PASS。

- [x] **Step 4: 运行四个独立组件构建**

Run login/home/mine/ota 的 `assembleDebug -Pstandalone=<feature> --offline`。

Expected: 全部 PASS。

- [x] **Step 5: 运行静态检查**

Run: `git diff --check`，并检查 `git status --short`。

Expected: 无 whitespace 错误；`.idea/migrations.xml`、`.idea/misc.xml` 仍为未提交的用户修改，本轮治理文件在最终验证后作为一个主题 commit 提交。
