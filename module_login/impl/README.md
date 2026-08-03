# `:module_login:impl`

登录实现模块负责登录页面、认证数据流和会话写入，并支持独立 APK 验证。

## 模块职责

实现账号密码演示登录、状态管理、Repository 和依赖注入；对外入口以登录 api 为准。

## 模块类型

feature impl（支持 `standalone=login`）。

## 依赖规则

允许依赖自身 api、所需 core 及其他 feature api；禁止依赖其他 feature impl。

## 目录结构

- `src/main/java`：页面、ViewModel、Repository、API 与 DI。
- `src/main/res`：`login_*` 业务资源。
- `src/standalone`：独立 Application 与 Launcher Manifest。

## 公共入口

`LoginActivity`（通过 `LoginRoutes` 路由）、`LoginViewModel`；跨模块不得直接引用 impl 类型。

## 新代码放置

登录表单与认证流程留在本模块；跨模块稳定契约先评估是否应加入 api，通用网络机制复用 `core:network`。

## 验证命令

`./gradlew :module_login:impl:testDebugUnitTest :module_login:impl:assembleDebug -Pstandalone=login --offline`
