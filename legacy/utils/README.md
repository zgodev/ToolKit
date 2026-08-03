# `:legacy:utils`

遗留工具模块隔离历史 Java 工具、本地库和打包产物，仅用于兼容迁移，不作为新功能入口。

## 模块职责

保留尚未拆分的文件、图片、Excel、权限和 JNI 等历史能力，降低现代模块的传递依赖污染。

## 模块类型

legacy（Android compatibility library）。

## 依赖规则

允许依赖历史实现必需的 AndroidX、JAR/AAR 与 JNI；禁止被 core 依赖，禁止新增 feature 专属逻辑。

## 目录结构

- `src/main/java`：历史 Java 工具。
- `src/main/jniLibs`、`libs`：兼容本地库与封装包。

## 公共入口

现有 `FileUtils`、`BitmapUtil`、`ExcelUtils` 等仅作为迁移期兼容入口，不推荐新调用。

## 新代码放置

优先在职责明确的 core 或 feature 实现；只有保持既有兼容所必需的修改才进入本模块。

## 验证命令

`./gradlew :legacy:utils:testDebugUnitTest :legacy:utils:assembleDebug --offline`
