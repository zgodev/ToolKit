# `:core:web`

Web 模块封装 TBS WebView 初始化、页面容器和复用池，不实现业务 H5 协议。

## 模块职责

提供统一 Web 页面、引擎初始化和 WebView 生命周期管理；业务 JSBridge 与 URL 规则留在所属 feature。

## 模块类型

core（Android Web capability library）。

## 依赖规则

允许依赖 `core:startup`、`core:ui`、`core:navigation` 和 TBS；禁止依赖 feature。

## 目录结构

- `src/main/java/com/zhangyt/common/web`：Web 容器、初始化与池。
- `src/main/res`：`web_*` 专用页面资源。

## 公共入口

`WebActivity`、`WebEngine`、`WebInitializer`、`WebViewPool`、`WebViewWrapper`。

## 新代码放置

通用 WebView 生命周期或安全策略放这里；特定业务域名、桥接命令与页面状态放在 feature。

## 验证命令

`./gradlew :core:web:testDebugUnitTest :core:web:assembleDebug --offline`
