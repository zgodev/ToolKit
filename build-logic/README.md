# `build-logic`

构建逻辑模块集中维护 Android 约定插件、独立组件装配和项目治理任务，不包含运行时业务代码。

## 模块职责

统一编译配置、插件组合、standalone 规则、架构边界与项目索引生成，避免各模块复制 Gradle 配置。

## 模块类型

build logic（Gradle included build）。

## 依赖规则

可依赖 Gradle、AGP 与 Kotlin Gradle API；不得依赖任何 Android 业务模块或运行时实现。

## 目录结构

- `src/main/kotlin`：约定插件、纯规则和索引渲染器。
- `src/test/kotlin`：插件注册、规则诊断和确定性输出测试。

## 公共入口

`toolkit.android.application`、`toolkit.android.library`、`toolkit.android.feature`、`toolkit.kotlin.library`、`toolkit.android.hilt`、`toolkit.android.arouter`、`toolkit.architecture-guard`。

## 新代码放置

跨多个模块重复的构建配置才进入约定插件；可测试的规则优先写成纯 Kotlin，再由 Gradle task 采集数据调用。

## 验证命令

`./gradlew -p build-logic build --offline`
