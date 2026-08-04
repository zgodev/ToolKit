# `:core:navigation`

导航模块封装 ARouter 调用和全局通用路径，不拥有任何 feature 页面实现。

## 模块职责

提供统一跳转入口和按业务域分组的字符串路由契约，避免业务模块互相依赖实现类型。

## 模块类型

core（Android navigation library）。

## 依赖规则

可暴露 ARouter 与 Fragment 导航所需 API；禁止依赖任何业务模块。

## 目录结构

- `src/main/java/com/zhangyt/common/router`：路由封装与通用路径。
- `src/test`：路径和参数契约测试。

## 公共入口

`RouterManager`、`RouterPath`。

## 新代码放置

跨模块业务路径放在 `RouterPath` 对应业务分组；业务模块内部、不参与跨模块跳转的路径不必公开。

## 验证命令

`./gradlew :core:navigation:testDebugUnitTest :core:navigation:assembleDebug --offline`
