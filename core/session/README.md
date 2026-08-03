# `:core:session`

会话模块管理当前用户、持久化登录态和会话事件，不处理登录页面或具体认证接口。

## 模块职责

向各 feature 提供统一的用户状态读取、更新、清除和事件订阅能力。

## 模块类型

core（Android session library）。

## 依赖规则

允许依赖 `core:model`、协程与本地存储；禁止依赖登录 feature、网络接口和 UI 实现。

## 目录结构

- `src/main/java/com/zhangyt/common/user`：会话存储与事件。
- `src/test`：会话状态和序列化契约测试。

## 公共入口

`UserManager`、`SessionEvents`。

## 新代码放置

跨 feature 的会话状态放这里；认证请求、表单状态和页面跳转留在登录 feature。

## 验证命令

`./gradlew :core:session:testDebugUnitTest :core:session:assembleDebug --offline`
