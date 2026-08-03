# `:module_mine:api`

我的契约模块公开个人中心路由，不包含用户设置页面或会话实现。

## 模块职责

向 app 和其他调用方提供个人中心的稳定导航契约，隔离我的实现模块。

## 模块类型

feature api。

## 依赖规则

保持最小依赖；禁止依赖任意 feature impl，用户状态协议复用 `core:session` 的公开能力。

## 目录结构

- `src/main/java/.../api`：个人中心路由与稳定契约。
- `src/test`：路由契约测试。

## 公共入口

`MineRoutes`。

## 新代码放置

只放外部模块必须知道的契约；设置项、点击行为和页面状态放 impl。

## 验证命令

`./gradlew :module_mine:api:testDebugUnitTest :module_mine:api:assembleDebug --offline`
