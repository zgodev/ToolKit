# `:app`

主应用壳负责全量组件组装、应用启动与顶层导航，不承载具体业务实现。

## 模块职责

提供正式应用的 `Application`、启动页和主导航容器；登录、首页、我的与 OTA 业务分别由对应 feature 实现。

## 模块类型

app（正式 Application 与 feature impl 聚合层）。

## 依赖规则

允许依赖 `core:*`、各 feature api/impl；业务能力不得反向依赖本模块。

## 目录结构

- `src/main/java`：应用启动和顶层页面编排。
- `src/main/res`：仅放应用壳及 Launcher 资源。
- `src/test`：应用组装、路由和资源契约测试。

## 公共入口

`App`、`SplashActivity`、`MainActivity`；本模块不提供供业务模块复用的 API。

## 新代码放置

仅新增跨 feature 的应用级编排；具体页面、状态和数据逻辑放回所属 feature，通用能力放入职责匹配的 core。

## 验证命令

`./gradlew :app:testDebugUnitTest :app:assembleDebug --offline`
