# `:legacy:network`

遗留网络模块隔离旧 Java HTTP 与 WebSocket 实现，仅用于兼容既有调用，不接收新的常规网络代码。

## 模块职责

维持历史通信接口和迁移期间的二进制兼容；新 HTTP 数据流统一使用 `core:network`。

## 模块类型

legacy（Android compatibility library）。

## 依赖规则

允许依赖 `legacy:utils` 和历史协议必需的三方库；禁止被 core 依赖，也不得依赖 feature。

## 目录结构

- `httputil`：旧 HTTP 封装。
- `websocket`：旧 WebSocket 接口与实现。

## 公共入口

`HttpHelper`、`HttpCallback`、`WebSocketManager` 及现有兼容接口。

## 新代码放置

只修复已有兼容行为；新增 HTTP 能力放 `core:network`，业务 WebSocket 协议应放所属 feature 并抽取稳定传输层。

## 验证命令

`./gradlew :legacy:network:testDebugUnitTest :legacy:network:assembleDebug --offline`
