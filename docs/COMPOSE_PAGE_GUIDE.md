# Compose 页面工程模板

本文把“新页面优先 Compose”落实为可复用的工程结构。可运行参考是 `:module_mine`；复制前先按 `docs/PROJECT_INDEX.md` 检查已有状态、组件和资源，复用结构，不复制 Mine 的业务语义。

## 1. 标准职责拆分

中大型页面使用以下结构，页面名必须使用统一领域前缀：

```text
module_xxx/src/main/java/<namespace>/
├── fragment/XxxFragment.kt       Android 容器、路由和平台副作用
├── ui/XxxScreen.kt               Route Composable、纯 Content、私有组件、Preview
└── viewmodel/
    ├── XxxContract.kt             XxxUiState 与 XxxUiEffect
    └── XxxViewModel.kt            状态转换和业务意图
```

只有当页面很小且类型始终共同变化时，才可把 `UiState`、`UiEffect` 与 ViewModel 合并到同一文件；不得为了套模板创建空 Contract、空 UseCase 或单方法包装层。新增文件仍须满足 `AGENTS.md` 的复用检查和类注释规则。

参考实现：

- `module_mine/src/main/java/com/zhangyt/module/mine/fragment/MineFragment.kt`：ComposeView 生命周期与平台副作用。
- `module_mine/src/main/java/com/zhangyt/module/mine/ui/MineScreen.kt`：状态收集、响应式 Content、语义标签和明暗 Preview。
- `module_mine/src/main/java/com/zhangyt/module/mine/viewmodel/MineContract.kt`：可恢复状态与一次性事件分离。
- `module_mine/src/main/java/com/zhangyt/module/mine/viewmodel/MineViewModel.kt`：单向状态更新。
- `module_mine/src/test/java/com/zhangyt/module/mine/viewmodel/MineViewModelTest.kt`：不依赖 Android UI 的状态/事件测试。

## 2. 单向数据流

```text
用户操作 -> Screen 回调 -> ViewModel -> UiState -> Content 渲染
                              └-------> UiEffect -> Fragment/Activity 执行
```

- `UiState` 只保存重建后仍应存在的页面事实，如数据、选择、加载和错误状态。
- `UiEffect` 只表示导航、Toast、权限请求、主题/语言切换等一次性动作，不能放入 `UiState` 反复消费。
- Fragment/Activity 不组织网络、缓存或状态转换；Content Composable 不访问 Router、Manager、Repository 或全局单例。
- 一个页面只保留一个状态源，不同时维护 LiveData、StateFlow 和 `remember` 的同义状态。

## 3. Composable 结构

每个页面优先提供两层入口：

```kotlin
@Composable
fun XxxScreen(viewModel: XxxViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ToolKitTheme {
        XxxContent(
            state = state,
            onRetry = viewModel::retry,
        )
    }
}

@Composable
internal fun XxxContent(
    state: XxxUiState,
    onRetry: () -> Unit,
) {
    // 只根据参数渲染并上抛用户意图
}
```

`XxxScreen` 负责接入 ViewModel 与项目主题，`XxxContent` 必须能直接 Preview 和 UI 测试。列表提供稳定 key；状态和回调参数保持不可变；不要把 Context、NavController 或 ViewModel 继续向私有叶子组件层层传递。

## 4. 视觉、适配与无障碍

- 使用 `ToolKitTheme`、`ToolKitSpacing`、Material typography/colorScheme 和项目已有 Composable；业务页面不得创建平行主题。
- 颜色、文案和图标先查项目索引。业务专属 string 使用模块前缀；跨 feature 的稳定视觉 token 才进入 `core:designsystem`。
- 页面至少提供 Light/Dark Preview；宽度敏感页面追加手机与 `>=600dp` Preview，并限制大屏内容最大宽度。
- 点击目标、图标和动态状态提供必要 `contentDescription`/semantics；关键交互使用稳定 `testTag`，标签表达语义，不绑定布局层级。
- 只有第三方/系统 View 确实没有可靠 Compose 接口时使用 `AndroidView`，并把它限制在单一适配组件中。

## 5. 测试与完成标准

最低测试顺序：

1. ViewModel 状态转换：初始、成功、错误、重试、重复点击与取消。
2. 一次性事件：导航或 Toast 不因重组/重建重复触发。
3. Content UI：关键状态可见、交互回调和无障碍语义；复杂视觉再增加截图或真机验收。
4. 模块测试与 assemble；涉及通用 Compose 能力时执行 L2/L3 验证。

页面提交前确认：没有新增 XML；没有硬编码颜色或重复文案；Preview 可独立渲染；状态不藏在叶子组件；副作用具备生命周期边界；模块 README 与项目索引仍真实。

## 6. 依赖与版本

业务 feature 通过 `toolkit.android.feature` 自动获得 Compose 编译与基础依赖，禁止各 feature 重复声明 BOM 或编译器版本。纯 core library 默认不启用 Compose；只有真实提供 Compose 能力的模块才显式开启。Compose BOM 与 Compiler 版本分别在 `gradle/libs.versions.toml` 管理，升级时必须核对 Kotlin 兼容矩阵并执行完整 L3 验证。
