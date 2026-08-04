# ToolKit 项目协作规则

## 规则优先级

项目内规则发生冲突时，按以下顺序执行：

1. 平台、系统、安全与数据保护要求。
2. 用户在当前任务中的最新明确要求。
3. 根目录 `AGENTS.md` 的项目强制规则。
4. 目标模块 `README.md` 的职责、边界和验证要求。
5. `FRAMEWORK_GUIDE.md` 的详细开发指南。
6. `.agents/skills/`、`.claude/skills/` 等项目技能中的通用检查项。

低优先级规则不得放宽或覆盖高优先级规则；同一级别冲突时，以范围更具体、时间更新且明确适用于当前任务的规则为准。发现规则与用户要求冲突时必须指出具体影响，不得静默混用两套方案。

## 开始任务前必须读取

按顺序读取，禁止一开始无差别遍历仓库：

1. 本文件：确认强制边界和完成标准。
2. `docs/PROJECT_INDEX.md`：搜索目标业务、类型、路由或资源名称。
3. 目标模块的 `README.md`：确认职责、依赖、公共入口和验证命令。
4. 仅打开索引命中的真实实现；索引未覆盖或已过期时再扩大搜索。
5. 涉及整体设计或新增模块时再读 `FRAMEWORK_GUIDE.md`。

根目录文档、AI 规则、`build-logic`、依赖治理、CI 或全局配置任务没有目标业务模块时，第 3 步改为读取与任务直接相关的根目录文档或 `build-logic/README.md`，无需遍历无关模块 README。任务后续一旦实际修改某个模块，必须在修改前补读该模块 README。

`docs/PROJECT_INDEX.md` 由 `./gradlew generateProjectIndex` 生成，禁止手工编辑。查找示例：

```bash
grep -n "关键词" docs/PROJECT_INDEX.md
grep -n "公共入口" module_xxx/README.md
```

## 项目定位与工具链

ToolKit 是面向中大型 Android 项目的严格组件化骨架。Kotlin 为主，遗留 Java 隔离在 `legacy/`；主应用包名为 `com.zhangyt.toolkit`。

- JDK 17、Gradle 8.7、AGP 8.2.2、Kotlin 1.9.22。
- 依赖版本只在 `gradle/libs.versions.toml` 维护；模块不得声明仓库或散落版本号。
- 通用 Gradle 配置在 `build-logic`；模块优先复用 `toolkit.*` 约定插件。
- 不顺手升级 Kotlin、KSP、AGP 或核心依赖。确需升级时先说明兼容、构建和发布影响。

## 复用优先与新文件门槛

新增类型、资源、扩展或 Gradle 配置前，依次检查：

1. 项目索引是否已有同语义入口或资源。
2. 当前模块现有文件能否在不混淆职责的前提下扩展。
3. `core:navigation` 或 `core:model` 是否已有跨模块契约。
4. `core:designsystem`、`core:ui`、`core:network`、`core:utils` 等是否已有能力。
5. `legacy:*` 只可兼容调用，不把新实现继续堆入 legacy。

新建文件必须能回答：已有实现为什么不能复用、为什么不应放入现有文件、为什么当前模块是唯一正确归属。共享抽象至少要有两个真实使用方且语义稳定；不要为假设中的未来需求提前抽象。

禁止新增没有边界的类型名，如单独的 `Utils`、`Common`、`Helper`、`Manager`、`Base`。已有历史类型是兼容对象，不是新代码示例。

## 模块边界

- `app`：正式应用壳、启动与跨 feature 编排；常规完整应用唯一的业务模块聚合层。
- `core:*`：单一职责基础能力，绝不依赖 `module_*`。
- `legacy:*`：历史兼容隔离区；core 不得反向依赖 legacy。
- `module_xxx`：单一业务模块，持有该领域的 UI、数据、资源与独立运行入口；默认只依赖所需 `core:*`。
- feature 之间不得建立常规项目依赖。唯一例外是 `mine` standalone 组装 OTA，且只存在于构建组装分支。
- 跨模块跳转使用 `RouterManager` 和 `core:navigation` 的 `RouterPath`，不得直接引用对方 Activity/Fragment。

新增模块必须提供一个标准 README、路由契约测试、索引更新和必要的 standalone 验证；禁止默认创建 api/impl 子模块。详细流程见 `FRAMEWORK_GUIDE.md`。

## 页面架构与数据流

- 项目业务页面默认采用 MVVM：Activity、Fragment 或 Composable 只负责渲染状态、收集生命周期安全的数据流并向 ViewModel 发送用户操作，不直接依赖 Repository、API、DAO 或组织网络与持久化流程。
- ViewModel 是页面状态和交互流程的唯一持有者；同一页面不得同时维护多套平行状态源。可恢复的 `UiState` 与导航、Toast 等一次性 `UiEffect` 必须分离，避免重建后重复消费。
- 简单页面使用 `BaseViewModel` 和 MVVM；只有存在并发加载、复杂状态转换、重试或多类事件编排时，才复用 `MviViewModel`/`MviContract`。禁止为静态页面或简单表单强行引入 MVI。
- 标准数据流为 `UI -> ViewModel -> Repository -> API/DAO`，状态结果按相反方向返回；Repository 负责数据源协调、响应解包和错误映射，不向 UI 暴露未经处理的数据访问细节。
- DTO、Entity、Domain Model、UiModel/UiState 按职责分离并在层级边界转换；禁止为了减少转换而让网络或数据库模型直接成为复杂页面状态。
- UseCase 不是默认层。只有业务规则被多个 ViewModel 复用，或单个流程需要稳定编排多个 Repository 时才创建，禁止为每个操作机械增加 UseCase。

详细的 UI、网络、协程和测试约束见 `FRAMEWORK_GUIDE.md` 第 7～11 节。

## 编码、UI 与资源

- Kotlin/Java 文件与主要类型同名，一个文件只承担一个主要职责。
- 每个新建的 Kotlin/Java 顶层类型都必须在声明前添加类级注释，包含文件或类型功能说明、作者和创建日期；`class`、`data class`、`enum class`、`interface`、`object` 均适用，字段不得留空。具有独立职责的嵌套类型同样必须添加，匿名对象、局部类和 `companion object` 豁免。统一模板：

```kotlin
/**
 * @description: 说明该文件或类型的职责、使用场景和必要边界
 * @author: 创建者姓名
 * @Date: yyyy-MM-dd
 */
```

- `@author` 读取 `git config user.name`，不得填写 Codex、Claude 等 AI 产品名称；`@Date` 按北京时间（UTC+8）填写首次创建日期。后续修改已有类型时保留原作者与创建日期，不把修改时间覆盖为创建时间。
- `verifyArchitecture` 自动校验顶层类型的三项注释。`config/architecture/class-header-baseline.txt` 只冻结规则启用前的历史欠账，禁止为新类型增加基线条目；历史类型补齐注释后应删除对应条目。
- 页面配套类型使用同一领域前缀，如 `LoginActivity`、`LoginViewModel`、`LoginRepository`、`LoginUiState`。
- DTO、持久化 Entity、领域模型和 UiModel 不混用；跨模块稳定模型放 `core:model`，业务内部模型留在所属 feature。
- 新增业务页面和可复用 UI 组件默认使用 Compose，并复用项目 Material 主题、design token 与已有 Composable；除下述例外外不得新建 XML 布局。
- 仅在维护稳定的既有 XML 页面且迁移明显扩大任务范围、必须接入缺少可靠 Compose 适配的第三方/系统 View，或存在已验证的兼容性、性能、无障碍阻塞时，才允许继续使用或新增 XML；必须在实现说明中记录具体原因，开发习惯和暂时不熟悉 Compose 不构成例外。
- 不在无关需求中整体重写稳定 XML 页面；确需 View/Compose 混用时，将 `AndroidView` 或 `ComposeView` 封装在清晰边界内，禁止同一页面长期维护两套平行状态源。
- Activity/Fragment 优先复用现有 Base 类，并遵循上述页面架构与数据流规则。
- 资源使用所有者前缀：`app_*`、`common_*`、`web_*`、`login_*`、`home_*`、`mine_*`、`ota_*`。
- 优先复用主题属性和 design system，禁止业务 XML 新增硬编码颜色、重复文案或平行通用控件。
- 协程使用结构化作用域，禁止 `GlobalScope`；Flow 在 UI 层按生命周期收集。
- 网络响应经过 `BaseRepository` 或明确领域映射；错误必须保留可诊断原因，禁止空 catch。
- 日志不得输出 token、密码、Cookie、完整请求体或用户敏感信息。

## 测试与完成标准

功能和 Bug 修复先补有业务意义的测试；禁止 `ExampleUnitTest`、`ExampleInstrumentedTest`、`addition_isCorrect` 等模板测试。测试按最小稳定边界选择：纯规则单测、Repository/状态测试、路由/资源契约测试，再到 assemble。

验证按本次改动触达的最高风险等级执行；无法确定等级时按更高一级处理。所有等级都必须运行 `git diff --check`，并在结束前检查 `git status --short`。

- **L0 文档与规则**：仅修改 Markdown、项目技能或注释，且不影响 Gradle、源码、资源、Manifest 或索引输入。检查文档互相一致、镜像技能同步和 `git diff --check`；不强制运行 Android 构建。
- **L1 单模块实现**：只影响一个模块内部实现且不改变跨模块契约。运行目标模块 README 指定的测试和 assemble，并运行 `verifyArchitecture`、`verifyProjectIndex`。
- **L2 应用或多模块协作**：影响 `app`、公共 core、跨模块路由/契约或多个模块。运行全项目单元测试、app Debug 构建、架构和索引校验；涉及 standalone 时追加对应独立构建。
- **L3 构建与架构治理**：影响 `build-logic`、约定插件、模块边界、依赖规则、standalone 机制、架构守卫或索引生成。执行完整治理基线；跨组件治理变化必须构建 login、home、mine、ota 全部 standalone。

当模块、依赖、公开类型、路由或 Android 资源变化时，先运行 `generateProjectIndex`，再执行索引校验并提交生成结果。L3 完整治理基线为：

```bash
./gradlew -p build-logic build --offline
./gradlew verifyArchitecture verifyProjectIndex --offline
./gradlew testDebugUnitTest :app:assembleDebug --offline
./gradlew :module_login:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:assembleDebug -Pstandalone=ota --offline
git diff --check
```

构建成功不等于设备验收，涉及安装权限、真实下载、WebView/TBS、主题或语言视觉效果时明确说明真机边界。

完成前自审：

- 是否先复用后新增，且没有平行实现。
- 依赖方向、包和资源前缀是否正确。
- 公共入口、模块职责或资源变化后是否重新生成索引。
- 模块职责变化后是否同步 README；通用规则变化后是否同步本文件和开发指南。
- 是否保留用户的无关改动，暂存区只含本任务文件。

## Git 范围

- 不覆盖、不清理用户的无关本地修改。
- 同一功能主题合并为一个 commit；默认不提交、不推送，只有用户明确要求时执行。
- 提交前检查 `git status --short`、`git diff --cached`、`git diff --check`，敏感配置和凭据不得进入 Git。

## 项目技能

- Android 代码审查：`.agents/skills/android-code-review/SKILL.md`
- Compose UI：`.agents/skills/compose-ui/SKILL.md`
- XML UI：`.agents/skills/xml-ui/SKILL.md`
