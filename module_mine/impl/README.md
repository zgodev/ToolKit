# `:module_mine:impl`

我的实现模块负责个人中心、主题语言入口、OTA 导航与退出登录，并支持独立 APK 验证。

## 模块职责

组合会话、主题、多语言和 OTA api 形成个人中心页面；底层能力由相应 core/feature 提供。

## 模块类型

feature impl（支持 `standalone=mine`）。

## 依赖规则

常规代码只依赖自身 api、`module_ota:api` 和所需 core；`module_ota:impl` 仅在 mine standalone 组装配置中允许。

## 目录结构

- `src/main/java`：个人中心 Fragment。
- `src/main/res`：`mine_*` 资源。
- `src/standalone`：独立 Launcher；构建时组合 OTA impl。

## 公共入口

`MineFragment` 由 `MineRoutes` 发现；跨模块不得直接引用 impl 类型。

## 新代码放置

个人设置编排放本模块；主题、语言、会话等通用机制扩展对应 core，OTA 行为扩展 OTA feature。

## 验证命令

`./gradlew :module_mine:impl:testDebugUnitTest :module_mine:impl:assembleDebug -Pstandalone=mine --offline`
