# `:core:network`

现代网络模块统一 Retrofit/OkHttp、响应解包、拦截器与 Repository 基线，并暂存待迁移的演示 `CommonApi`。

## 模块职责

提供网络客户端配置、认证头、错误映射、有限日志和通用请求流程。现有 `CommonApi`、Banner/Feed DTO 是骨架演示兼容项，不是新增业务接口的放置范例。

## 模块类型

core（Android network library）。

## 依赖规则

可暴露 Retrofit、OkHttp、Gson 与协程网络契约；禁止依赖 feature、UI 或 legacy 网络实现。

## 目录结构

- `api`、`client`、`config`：基础协议和客户端。
- `interceptor`、`exception`：请求策略和错误。
- `repository`、`di`：通用数据访问基线与装配。

## 公共入口

`RetrofitClient`、`NetworkConfig`、`BaseResponse`、`BaseRepository`、`ApiException`、`NetworkModule`；`CommonApi` 仅为待迁移演示入口。

## 新代码放置

跨业务网络机制才进入本模块；新的业务 endpoint、DTO、Repository 和错误语义留在 feature。演示接口被真实业务采用时，应整体迁移到所属 feature，而不是继续扩展 `CommonApi`。

## 验证命令

`./gradlew :core:network:testDebugUnitTest :core:network:assembleDebug --offline`
