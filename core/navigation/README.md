# `:core:navigation`

导航模块封装 ARouter 调用和全局通用路径，不拥有任何 feature 页面实现。

## 模块职责

提供统一跳转入口；业务路由常量由各 feature api 声明，避免跨模块引用 impl 类型。

## 模块类型

core（Android navigation library）。

## 依赖规则

可暴露 ARouter 与 Fragment 导航所需 API；禁止依赖 feature impl，通常也不依赖 feature api。

## 目录结构

- `src/main/java/com/zhangyt/common/router`：路由封装与通用路径。
- `src/test`：路径和参数契约测试。

## 公共入口

`RouterManager`、`RouterPath`。

## 新代码放置

业务路径放在所属 `module_xxx:api` 的 `XxxRoutes`；只有真正跨业务的导航能力才扩展本模块。

## 验证命令

`./gradlew :core:navigation:testDebugUnitTest :core:navigation:assembleDebug --offline`
