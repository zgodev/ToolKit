# `:core:model`

模型模块保存跨功能稳定共享的纯 Kotlin 数据契约，不包含 Android、网络或 UI 行为。

## 模块职责

提供会话等多个模块共同使用的稳定模型；feature 私有 DTO、领域模型和 UI 模型不放在这里。

## 模块类型

core（纯 Kotlin library）。

## 依赖规则

只允许依赖 Kotlin/JDK 级能力，不依赖 Android、其他 core、feature 或 legacy。

## 目录结构

- `src/main/java`：稳定共享模型。
- `src/test`：模型转换或值语义测试。

## 公共入口

`UserInfo`。

## 新代码放置

模型存在至少两个真实跨模块使用方且语义稳定时才加入；业务私有模型留在所属 feature。

## 验证命令

`./gradlew :core:model:test --offline`
