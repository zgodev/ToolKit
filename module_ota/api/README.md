# `:module_ota:api`

OTA 契约模块公开升级页面路由，不包含下载、校验、安装或系统权限实现。

## 模块职责

向 app 与个人中心提供稳定升级入口，使调用方无需编译依赖 OTA impl 类型。

## 模块类型

feature api。

## 依赖规则

保持最小依赖；禁止依赖任意 feature impl，升级内部模型默认留在 impl。

## 目录结构

- `src/main/java/.../api`：升级路由与稳定契约。
- `src/test`：路由契约测试。

## 公共入口

`OtaRoutes`。

## 新代码放置

只有其他模块确实需要编译依赖的升级契约才加入；下载和安装细节放 impl。

## 验证命令

`./gradlew :module_ota:api:testDebugUnitTest :module_ota:api:assembleDebug --offline`
