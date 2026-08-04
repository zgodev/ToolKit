# `build-logic`

构建逻辑模块集中维护 Android 约定插件、独立组件装配和项目治理任务，不包含运行时业务代码。

## 模块职责

统一编译配置、Compose 工程能力、插件组合、standalone 规则、架构边界、类注释校验与项目索引生成，避免各模块复制 Gradle 配置。

## 模块类型

build logic（Gradle included build）。

## 依赖规则

可依赖 Gradle、AGP 与 Kotlin Gradle API；不得依赖任何 Android 业务模块或运行时实现。

## 目录结构

- `src/main/kotlin`：约定插件、纯规则和索引渲染器。
- `src/test/kotlin`：插件注册、规则诊断和确定性输出测试。

## 公共入口

`toolkit.android.application`、`toolkit.android.library`、`toolkit.android.feature`、`toolkit.kotlin.library`、`toolkit.android.hilt`、`toolkit.android.arouter`、`toolkit.architecture-guard`。

其中 `toolkit.android.feature` 应用于单层 `:module_<name>`，根据 `-Pstandalone=<name>` 在 Android Library 与独立 Application 之间切换。

`toolkit.android.application` 与 `toolkit.android.feature` 自动启用 Compose 编译器和统一 BOM/运行时/预览测试依赖；纯 core library 不默认携带 Compose，确需使用时由模块显式启用。

`toolkit.architecture-guard` 校验模块依赖、feature namespace 包归属、README、feature 测试底线、资源前缀、模板测试、项目索引和新建顶层类型的类级注释。`config/architecture/class-header-baseline.txt` 仅冻结规则启用前的历史欠账，不接受新类型例外。

## 新代码放置

跨多个模块重复的构建配置才进入约定插件；可测试的规则优先写成纯 Kotlin，再由 Gradle task 采集数据调用。

## 验证命令

`./gradlew -p build-logic build --offline`
