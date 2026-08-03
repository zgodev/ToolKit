# `:module_home:api`

首页契约模块公开首页各页签路由，不包含列表数据、UI 或页面实现。

## 模块职责

为 app 和登录等调用方提供稳定导航契约，避免跨模块直接引用首页 Fragment。

## 模块类型

feature api。

## 依赖规则

保持最小依赖；禁止依赖任意 feature impl，稳定共享模型优先评估所属领域或 `core:model`。

## 目录结构

- `src/main/java/.../api`：首页路由与稳定契约。
- `src/test`：路由契约测试。

## 公共入口

`HomeRoutes`。

## 新代码放置

仅添加其他模块确实需要的首页契约；列表模型、适配器和 ViewModel 放 impl。

## 验证命令

`./gradlew :module_home:api:testDebugUnitTest :module_home:api:assembleDebug --offline`
