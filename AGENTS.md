# ToolKit 项目协作规则

## 项目定位

ToolKit 是面向中大型 Android 项目的严格组件化骨架。Kotlin 为主，遗留 Java 被隔离在 `legacy/`；主应用包名为 `com.zhangyt.toolkit`。

## 工具链

- JDK 17、Gradle 8.7、AGP 8.2.2、Kotlin 1.9.22。
- 版本与依赖统一维护在 `gradle/libs.versions.toml`。
- 通用 Gradle 配置维护在 `build-logic`，模块优先使用 `toolkit.*` 约定插件。
- 不主动升级 Kotlin 2、KSP、AGP 或核心依赖；兼容性修复确需升级时先说明影响。

## 模块边界

- `app`：应用壳、导航编排、全局初始化；允许聚合多个 feature impl。
- `core:*`：单一职责基础能力，禁止依赖业务模块。
- `legacy:*`：尚未现代化的历史实现；新代码不要继续向这里堆放。
- `module_xxx:api`：跨模块可见的路由、契约和必要模型，不依赖 impl。
- `module_xxx:impl`：业务实现，只依赖自身 api、其他 feature api 和所需 core。
- feature 之间禁止直接依赖对方 impl；只有应用壳或 standalone 组装层可以组合 impl。
- 跨模块页面跳转统一使用 `RouterManager` 和对应 feature api 中的 routes。

## UI 与业务代码

- 维护现有 XML 页面时默认沿用 XML + ViewBinding；不要在普通修改中顺手整体迁移技术栈。
- 新页面或明确的迁移任务可以按模块设计选择 XML 或 Compose，项目不全局禁止 Compose。
- XML 资源遵循模块前缀；优先使用主题属性和 design system，避免硬编码颜色。
- Activity/Fragment 优先继承现有 Base 类；参与 Hilt 的 Android 组件添加正确入口注解。
- 协程使用结构化作用域；禁止业务代码使用 `GlobalScope`。
- Repository 依赖通过构造注入，网络响应统一经过 `BaseRepository` 或明确的领域映射。

## 构建与验证

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew :module_login:impl:assembleDebug -Pstandalone=login
./gradlew :module_home:impl:assembleDebug -Pstandalone=home
./gradlew :module_mine:impl:assembleDebug -Pstandalone=mine
./gradlew :module_ota:impl:assembleDebug -Pstandalone=ota
```

- 新功能和 Bug 修复补有业务意义的测试，不保留 `2 + 2` 模板测试。
- 构建成功不等于设备验收；涉及系统权限、安装 APK、WebView/TBS 时明确区分编译验证与真机验证。
- 提交前至少运行受影响模块测试、对应 assemble 和 `git diff --check`。

## 修改原则

- 先阅读真实实现和有效配置，不根据文件名猜行为。
- 保持改动范围与任务一致；不要覆盖无关的本地修改。
- 新增模块同步更新 `settings.gradle`、本文件、README/架构指南及测试命令。
- 默认不提交、不推送；仅在用户明确要求时执行 Git 提交或远端操作。

## 项目技能

- Android 代码审查：`.agents/skills/android-code-review/SKILL.md`
- Compose UI：`.agents/skills/compose-ui/SKILL.md`
- XML UI：`.agents/skills/xml-ui/SKILL.md`
