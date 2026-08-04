# 单 Feature 模块架构设计

## 背景

当前 `module_login`、`module_home`、`module_mine`、`module_ota` 均拆分为 `api` 与 `impl` 两个 Gradle 模块，但四个 `api` 模块实际只包含一个路由常量文件。该边界没有承载稳定接口、共享模型或可替换实现，维护成本高于隔离收益。

本次将四个业务域收敛为单一 Gradle 模块，同时保留独立 APK 构建能力和统一的 Gradle Convention Plugin。

## 目标结构

```text
:app
:core:*
:legacy:*
:module_login
:module_home
:module_mine
:module_ota
build-logic
```

每个 `module_xxx` 同时持有该业务域的 UI、数据访问、资源、DI 和 standalone 入口，不再创建 `api`、`impl` 子模块。

## 路由契约

跨 feature 仅共享字符串路由，不值得为每个业务域建立独立 API 模块。现有 Login、Home、Mine、OTA 路由合并到 `core:navigation` 的 `RouterPath`，按业务域使用嵌套对象分组：

```kotlin
RouterPath.Login.ACTIVITY_LOGIN
RouterPath.Home.FRAGMENT_CHAT
RouterPath.Mine.FRAGMENT_MINE
RouterPath.Ota.ACTIVITY_OTA
```

业务模块通过 `core:navigation` 使用这些契约，不直接引用其他 feature 的 Activity、Fragment 或实现类型。

## 依赖规则

- `app` 可以依赖 `core:*`、`legacy:*` 和 `module_*`，负责完整产品组装。
- `core:*` 只能依赖其他 `core:*`。
- `legacy:*` 只能依赖 `core:*` 或其他 `legacy:*`。
- `module_*` 默认只能依赖 `core:*`，不直接依赖其他业务模块。
- 唯一构建期例外：`-Pstandalone=mine` 时 `module_mine` 可以依赖 `module_ota`，使 Mine 独立 APK 的 OTA 路由存在真实实现。该依赖不写入项目索引的常规依赖列表，但架构校验仍会检查其上下文。

## Convention Plugin 去留

保留 `build-logic` 和 Convention Plugin，因为它们负责统一：

- JDK 17、compileSdk 34、minSdk 21、targetSdk 34；
- Kotlin、ViewBinding、BuildConfig 和测试依赖；
- Hilt/KSP 与 ARouter/KAPT 配置；
- feature 在 Android Library 与 standalone Android Application 之间切换；
- 项目索引生成、索引一致性及架构规则校验。

`toolkit.android.feature` 改为识别 `:module_<name>`，不再识别 `:module_<name>:impl`。删除 Convention Plugin 会导致上述配置散落到各模块，因此不在本次范围内。

## 文件迁移

- `module_xxx/impl/src` 移到 `module_xxx/src`。
- `module_xxx/impl/build.gradle` 合并为 `module_xxx/build.gradle`，删除自身 API 依赖和常规跨 feature 依赖。
- `consumer-rules.pro` 移到业务模块根目录。
- 四个 `api` 模块及其 Manifest、构建脚本、README、Routes 文件删除。
- 每个业务模块只保留一个 README，并同步根文档、AI 规则和项目索引。

源码包名与 Android namespace 保持不变，避免无业务价值的包级重命名。旧 `com.zhangyt.module.<name>.api.XxxRoutes` 引用全部迁移到 `com.zhangyt.common.router.RouterPath`。

## 构建与验证

常规验证：

```bash
./gradlew -p build-logic clean build --offline
./gradlew verifyArchitecture verifyProjectIndex --offline
./gradlew testDebugUnitTest :app:assembleDebug --offline
```

独立组件验证：

```bash
./gradlew :module_login:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:assembleDebug -Pstandalone=ota --offline
```

完成标准是 settings 中不再存在 feature 子模块、源码和文档不再使用 `feature api/impl` 规则、架构守卫能拒绝常规 feature-to-feature 依赖、四个业务模块均可独立构建。
