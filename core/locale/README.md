# `:core:locale`

多语言模块管理语言选择、持久化和 Context 配置，不保存业务文案资源。

## 模块职责

提供应用级语言切换与恢复能力；具体字符串仍由 app、design system 或所属 feature 管理。

## 模块类型

core（Android locale library）。

## 依赖规则

允许依赖 `core:lifecycle` 和本地存储；禁止依赖 feature、业务资源或网络层。

## 目录结构

- `src/main/java/com/zhangyt/common/language`：语言配置与切换。
- `src/test`：语言选择规则测试。

## 公共入口

`LanguageManager`。

## 新代码放置

通用 Locale 处理扩展本模块；业务文案和页面语言逻辑留在资源与所属 feature。

## 验证命令

`./gradlew :core:locale:testDebugUnitTest :core:locale:assembleDebug --offline`
