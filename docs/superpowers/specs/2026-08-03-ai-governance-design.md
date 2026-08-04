# ToolKit AI 开发治理设计

> 本文涉及 feature api/impl 的部分已于 2026-08-04 被 `2026-08-04-single-feature-modules-design.md` 取代；其他治理原则仍以当前 `AGENTS.md` 为准。

## 1. 目标

为后续 Codex 持续开发建立一套可读取、可检索、可自动校验的项目治理体系，使 AI 在修改代码前能快速理解模块职责、定位可复用实现，并在提交前发现越界依赖、错误命名和文档漂移。

本次治理不改变现有业务行为，不引入新的业务框架，也不把项目特定规则重复写入多个 Skill。机械规则由 Gradle 自动校验，判断类规则由 `AGENTS.md` 和模块 README 约束。

## 2. 单一事实来源与读取顺序

项目规则按以下优先级组织：

1. `AGENTS.md`：AI 必须执行的工作流、架构红线和完成标准。
2. `docs/PROJECT_INDEX.md`：由工具生成的模块、依赖、公共入口、路由与资源快速索引。
3. 目标模块的 `README.md`：模块职责、允许依赖、目录结构、复用入口和验证命令。
4. `FRAMEWORK_GUIDE.md`：完整架构、编码、命名、资源和测试规范。
5. `.agents/skills/*`：Android 审查、XML 和 Compose 等跨任务方法，不复制项目模块清单。

Codex 处理任务时必须先读取前三层，不允许从仓库根目录开始无差别遍历。只有索引没有覆盖目标，或索引校验提示过期时，才扩大文件搜索范围。

## 3. 项目索引

新增受版本控制的 `docs/PROJECT_INDEX.md`，内容由 Gradle 任务确定性生成，包含：

- 每个 Gradle 模块的路径、类型、namespace、职责摘要和 README 链接。
- 模块间 project dependency，区分 `api` 与 `implementation`。
- 可复用 Kotlin/Java 公共入口，优先列出 `Routes`、Base 类型、Initializer、Manager、Repository 和公共 View。
- Android 资源名称、类型与所属模块，按名称排序。
- ARouter 路径与声明位置。
- 独立组件构建命令。

提供两个任务：

- `./gradlew generateProjectIndex`：仅在模块、公共入口、路由或资源发生变化时重新生成索引。
- `./gradlew verifyProjectIndex`：在临时内存中重新生成并与已提交索引比较；不一致时失败并提示执行生成任务。

索引只记录定位信息，不复制源码实现，避免文件过大和事实重复。

## 4. 模块 README

每个被 `settings.gradle` include 的 Gradle 模块，以及 `build-logic`，必须存在 `README.md`。README 使用统一结构：

1. 模块职责：一句话说明负责什么，以及明确不负责什么。
2. 模块类型：app、core、legacy、feature api、feature impl 或 build logic。
3. 依赖规则：允许和禁止的依赖方向。
4. 目录结构：只列真实存在和约定允许出现的目录。
5. 公共入口：可被其他模块复用的类型、路由、资源或 Gradle 插件。
6. 新代码放置：常见需求应放在哪个 package/file，哪些情况不得新建文件。
7. 验证命令：模块单测、assemble 或 standalone 命令。

README 不罗列每个内部类，详细文件定位交给项目索引，避免两份清单同时维护。

## 5. 命名与复用规则

### 5.1 文件和类型

- Kotlin/Java 文件与主类型同名；一个文件只承担一个主要职责。
- 禁止新增无边界名称：`Utils`、`Common`、`Helper`、`Manager`、`Base` 单独作为类型名。已有兼容类不作为新代码示例。
- 工具按能力命名，例如 `DateFormatter`、`KeyboardController`；协调对象按领域命名，例如 `OtaDownloadCoordinator`。
- 页面配套类型使用一致前缀：`LoginActivity`、`LoginViewModel`、`LoginRepository`、`LoginUiState`。
- DTO、Entity、Domain、UiModel 不混用；跨模块模型只放 feature api 或 `core:model` 中的稳定契约。

### 5.2 Android 资源

资源使用 `<owner>_<type>_<purpose>`：

- app 壳：`app_*`
- 共享 design system：`common_*`
- feature：`login_*`、`home_*`、`mine_*`、`ota_*`
- core 专用页面：使用 core 能力名，如 `web_*`

类型段使用固定词：`activity`、`fragment`、`item`、`dialog`、`view`、`bg`、`ic`、`color`、`dimen`、`style`、`menu`、`file_paths`。Launcher 系统资源和 legacy 原样迁移文件可列入显式例外；新资源不允许加入例外。

### 5.3 复用决策

新增文件前按固定顺序查找：

1. `docs/PROJECT_INDEX.md` 中搜索业务名、类型名和资源用途。
2. 当前模块内部是否已有可扩展实现。
3. 所属 feature api 是否已有跨模块契约。
4. `core:designsystem`、`core:ui`、`core:network`、`core:utils` 等单一职责 core 是否已有能力。
5. `legacy:*` 只允许兼容调用，不把新代码继续放入 legacy。

共享抽象必须至少有两个明确使用方，且语义稳定；单页面代码先留在 feature 内，不为假设中的未来需求提前创建公共层。

## 6. 自动化架构守卫

在 `build-logic` 中新增根项目插件 `toolkit.architecture-guard`，由根 `build.gradle` 应用，提供 `verifyArchitecture` 聚合任务。

校验规则包括：

- project dependency 按模块类型使用完整 allowlist：core 只依赖 core；legacy 只依赖 core/legacy；feature api 只依赖 core/其他 api；feature impl 只依赖 core/feature api；app 才能聚合常规 impl。
- `module_xxx:api` 不依赖任意 feature impl。
- feature impl 不依赖其他 feature impl；唯一允许的 standalone 装配关系必须由白名单声明。
- `app` 是常规完整应用唯一的 impl 聚合层。
- 每个显式 include 的叶子模块都有构建文件；每个 Gradle 模块和 `build-logic` 都有符合章节结构的 README。
- feature、app、design system 和 core 页面资源符合所有者前缀；例外清单固定且只减不增。
- 不存在 `addition_isCorrect`、`ExampleUnitTest` 等模板测试。
- `docs/PROJECT_INDEX.md` 与当前项目结构一致。

`verifyArchitecture` 只读项目结构，不修改文件；失败信息给出违规文件、规则和修复方向。它加入文档规定的提交前必跑命令，但暂不强制挂到所有 `assemble`，避免日常局部构建被无关文档更新阻塞。

## 7. AI 开发工作流

Codex 每次任务遵循以下闭环：

1. 定位：读取 `AGENTS.md`、项目索引和目标模块 README。
2. 复用：从索引查找已有类型、路由和资源，再打开最相关文件确认真实行为。
3. 设计：明确改动归属、依赖方向、公共契约和测试边界。
4. 实现：沿用目标模块现有目录、命名、状态管理和依赖注入方式。
5. 自审：检查是否新增了可复用重复实现、模糊命名、跨层访问或无意义文件。
6. 验证：运行受影响测试、assemble、`verifyArchitecture` 和 `git diff --check`。
7. 交付：说明编译验证与设备验收边界；默认不提交、不推送。

新建文件必须能回答三个问题：现有索引为什么没有可复用实现、为什么不能放入已有文件、为什么目标模块是唯一正确归属。无法回答时不得创建。

## 8. 文档调整

- 扩充 `AGENTS.md`，加入强制读取顺序、新文件门槛、复用规则和验证命令。
- 重写 `FRAMEWORK_GUIDE.md` 为完整开发规范，覆盖模块、package、命名、资源、状态、网络、并发、错误处理、测试和 Git 范围。
- 根 `README.md` 保持项目入口定位，只链接架构指南、项目索引和各模块文档，不复制详细规范。
- 现有 XML/Compose/代码审查 Skill 保持方法论定位；只有与触发场景直接相关的简短入口需要同步，不把模块清单写入 Skill。

## 9. 迁移策略

本次先为所有现有模块补齐 README，生成第一版索引，并对少量明显违背新规则、且修改风险低的资源名称进行统一。大规模 legacy 类型重命名不纳入本次范围，通过例外清单冻结现状，后续在真实业务改造时逐步清理。

架构守卫先覆盖可确定的机械规则，不校验主观代码风格；主观质量由 `android-code-review` 和人工/Codex 审查承担。

## 10. 验收标准

1. 每个 Gradle 模块和 `build-logic` 都有结构统一、内容真实的 README。
2. AI 只读取项目索引即可定位所有模块、公共入口、路由和 Android 资源。
3. `generateProjectIndex` 连续执行两次结果完全一致。
4. 人为制造 core→feature、缺 README、错误资源前缀或过期索引时，相应校验明确失败。
5. 修复测试样例后，`verifyArchitecture`、`verifyProjectIndex`、全量单测和 app Debug 构建成功。
6. `AGENTS.md` 明确要求先复用、后新增，并规定新增文件的可审计理由。
7. 不修改或提交用户现有 `.idea/migrations.xml` 与 `.idea/misc.xml`。
