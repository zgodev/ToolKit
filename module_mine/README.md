# `:module_mine`

“我的”业务模块，包含用户信息、主题、语言、OTA 入口和独立运行能力。

## 模块职责

以 Compose + MVVM 展示当前用户状态，并提供主题、语言、检查更新和退出登录操作；同时作为项目 Compose 页面工程模板的可运行参考。

## 模块类型

feature（支持 `standalone=mine`）。

## 依赖规则

常规构建只依赖所需 `core:*`；`standalone=mine` 时允许构建期组装 `:module_ota`，不构成普通 feature 依赖许可。

## 目录结构

- `src/main/.../fragment`：Compose 容器与 Android 平台副作用。
- `src/main/.../ui`：纯状态驱动的 Compose 页面、响应式布局和 Preview。
- `src/main/.../viewmodel`：`MineUiState`、`MineUiEffect` 与 `MineViewModel`。
- `src/test`：状态初始化、设置选择与退出登录测试。
- `src/standalone`：独立 APK 的 Launcher Activity 与 Manifest。

## 公共入口

`MineFragment`；跨模块路由为 `RouterPath.Mine.FRAGMENT_MINE`。

## 新代码放置

个人中心页面与设置编排放在本模块；用户会话、主题和语言底层能力继续复用相应 core。新 Compose 页面参考 `docs/COMPOSE_PAGE_GUIDE.md` 的职责拆分，不直接复制 Mine 业务实现。

## 验证命令

`./gradlew :module_mine:testDebugUnitTest :module_mine:assembleDebug -Pstandalone=mine --offline`
