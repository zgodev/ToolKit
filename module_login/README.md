# `:module_login`

登录业务模块，包含登录页面、网络协议、Repository、ViewModel、DI 与独立运行入口。

## 模块职责

完成登录输入校验、接口调用、用户会话写入和登录后导航。

## 模块类型

feature（支持 `standalone=login`）。

## 依赖规则

只依赖登录所需的 `core:*` 和三方库；不依赖其他业务模块，登录后的首页跳转使用 `RouterPath.Home`。

## 目录结构

- `src/main`：登录业务源码、资源和 Manifest。
- `src/test`：演示登录输入约束与用户映射测试。
- `src/standalone`：独立 APK 的 Application 与 Manifest。

## 公共入口

`LoginActivity`、`LoginApi`；跨模块路由为 `RouterPath.Login.ACTIVITY_LOGIN`。

## 新代码放置

登录 UI、请求模型、Repository 和状态逻辑留在本模块；稳定通用能力放入职责匹配的 core。

## 验证命令

`./gradlew :module_login:testDebugUnitTest :module_login:assembleDebug -Pstandalone=login --offline`
