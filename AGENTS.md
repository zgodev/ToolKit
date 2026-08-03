# ToolKit 项目协作规则

## 开始任务前必须读取

按顺序读取，禁止一开始无差别遍历仓库：

1. 本文件：确认强制边界和完成标准。
2. `docs/PROJECT_INDEX.md`：搜索目标业务、类型、路由或资源名称。
3. 目标模块的 `README.md`：确认职责、依赖、公共入口和验证命令。
4. 仅打开索引命中的真实实现；索引未覆盖或已过期时再扩大搜索。
5. 涉及整体设计或新增模块时再读 `FRAMEWORK_GUIDE.md`。

`docs/PROJECT_INDEX.md` 由 `./gradlew generateProjectIndex` 生成，禁止手工编辑。查找示例：

```bash
grep -n "关键词" docs/PROJECT_INDEX.md
grep -n "公共入口" module_xxx/impl/README.md
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
3. 所属 feature api 是否已有跨模块契约。
4. `core:designsystem`、`core:ui`、`core:network`、`core:utils` 等是否已有能力。
5. `legacy:*` 只可兼容调用，不把新实现继续堆入 legacy。

新建文件必须能回答：已有实现为什么不能复用、为什么不应放入现有文件、为什么当前模块是唯一正确归属。共享抽象至少要有两个真实使用方且语义稳定；不要为假设中的未来需求提前抽象。

禁止新增没有边界的类型名，如单独的 `Utils`、`Common`、`Helper`、`Manager`、`Base`。已有历史类型是兼容对象，不是新代码示例。

## 模块边界

- `app`：正式应用壳、启动与跨 feature 编排；常规完整应用唯一的 feature impl 聚合层。
- `core:*`：单一职责基础能力，绝不依赖 `module_*`。
- `legacy:*`：历史兼容隔离区；core 不得反向依赖 legacy。
- `module_xxx:api`：路由、跨模块接口和必要稳定模型；不得依赖任意 impl。
- `module_xxx:impl`：业务实现；只依赖自身 api、其他 feature api 和所需 core。
- feature 之间不得依赖对方 impl。唯一例外是 `mine` standalone 组装 OTA impl，且只存在于构建组装分支。
- 跨模块跳转使用 `RouterManager` 和目标 feature api 的 `XxxRoutes`，不得直接引用对方 Activity/Fragment。

新增模块必须同时提供 api/impl README、路由契约测试、索引更新和必要的 standalone 验证；详细流程见 `FRAMEWORK_GUIDE.md`。

## 编码、UI 与资源

- Kotlin/Java 文件与主要类型同名，一个文件只承担一个主要职责。
- 页面配套类型使用同一领域前缀，如 `LoginActivity`、`LoginViewModel`、`LoginRepository`、`LoginUiState`。
- DTO、持久化 Entity、领域模型和 UiModel 不混用；跨模块模型只放 feature api 或稳定的 `core:model`。
- 维护 XML 页面时沿用 XML + ViewBinding；新页面或明确迁移可按模块设计选择 XML 或 Compose，项目不禁止 Compose。
- Activity/Fragment 优先复用现有 Base 类；状态由 ViewModel 持有，UI 不直接组织网络与持久化流程。
- 资源使用所有者前缀：`app_*`、`common_*`、`web_*`、`login_*`、`home_*`、`mine_*`、`ota_*`。
- 优先复用主题属性和 design system，禁止业务 XML 新增硬编码颜色、重复文案或平行通用控件。
- 协程使用结构化作用域，禁止 `GlobalScope`；Flow 在 UI 层按生命周期收集。
- 网络响应经过 `BaseRepository` 或明确领域映射；错误必须保留可诊断原因，禁止空 catch。
- 日志不得输出 token、密码、Cookie、完整请求体或用户敏感信息。

## 测试与完成标准

功能和 Bug 修复先补有业务意义的测试；禁止 `ExampleUnitTest`、`ExampleInstrumentedTest`、`addition_isCorrect` 等模板测试。测试按最小稳定边界选择：纯规则单测、Repository/状态测试、路由/资源契约测试，再到 assemble。

提交前至少完成：

```bash
./gradlew -p build-logic build --offline
./gradlew verifyArchitecture verifyProjectIndex --offline
./gradlew testDebugUnitTest :app:assembleDebug --offline
git diff --check
```

涉及独立组件时还要运行目标模块的 `-Pstandalone=<feature>` 构建；跨组件治理变更运行 login/home/mine/ota 全部 standalone。构建成功不等于设备验收，涉及安装权限、真实下载、WebView/TBS、主题或语言视觉效果时明确说明真机边界。

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
