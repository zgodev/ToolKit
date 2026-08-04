# `:module_mine`

“我的”业务模块，包含用户信息、主题、语言、OTA 入口和独立运行能力。

## 模块职责

展示当前用户状态并提供个性化设置、检查更新和退出登录操作。

## 模块类型

feature（支持 `standalone=mine`）。

## 依赖规则

常规构建只依赖所需 `core:*`；`standalone=mine` 时允许构建期组装 `:module_ota`，不构成普通 feature 依赖许可。

## 目录结构

- `src/main`：Mine Fragment 和业务资源。
- `src/standalone`：独立 APK 的 Launcher Activity 与 Manifest。

## 公共入口

`MineFragment`；跨模块路由为 `RouterPath.Mine.FRAGMENT_MINE`。

## 新代码放置

个人中心页面与设置编排放在本模块；用户会话、主题和语言底层能力继续复用相应 core。

## 验证命令

`./gradlew :module_mine:testDebugUnitTest :module_mine:assembleDebug -Pstandalone=mine --offline`
