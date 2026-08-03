# `:module_login:api`

登录契约模块公开登录页面路由与稳定跨模块协议，不包含认证实现或 UI。

## 模块职责

让 app 和其他 feature 通过稳定路径进入登录能力，隔离 `module_login:impl` 的具体类型。

## 模块类型

feature api。

## 依赖规则

保持最小依赖；禁止依赖自身或其他 feature impl，新增依赖必须是公开契约真正需要的 core/model。

## 目录结构

- `src/main/java/.../api`：路由和跨模块稳定契约。
- `src/test`：路径唯一性与契约测试。

## 公共入口

`LoginRoutes`。

## 新代码放置

仅放其他模块必须编译依赖的稳定契约；表单、Repository、DTO 和页面类型放 impl。

## 验证命令

`./gradlew :module_login:api:testDebugUnitTest :module_login:api:assembleDebug --offline`
