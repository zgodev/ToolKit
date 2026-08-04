# `:module_home`

首页业务模块，包含消息、联系人和发现页面及独立运行入口。

## 模块职责

提供首页业务 Fragment、列表适配、页面模型和状态加载。

## 模块类型

feature（支持 `standalone=home`）。

## 依赖规则

只依赖首页所需的 `core:*` 和三方 UI 库；不依赖其他业务模块。

## 目录结构

- `src/main`：首页 Fragment、Adapter、ViewModel、模型和资源。
- `src/test`：消息列表状态加载测试。
- `src/standalone`：独立 APK 的 Launcher Activity 与 Manifest。

## 公共入口

`ChatFragment`、`ContactsFragment`、`DiscoverFragment`；跨模块路由位于 `RouterPath.Home`。

## 新代码放置

首页 Tab 内的页面、状态和业务模型放在本模块；跨业务能力不得直接引用其他 feature 类型。

## 验证命令

`./gradlew :module_home:testDebugUnitTest :module_home:assembleDebug -Pstandalone=home --offline`
