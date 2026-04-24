---
name: compose-ui
description: Enforces Compose UI conventions, Material 3 theming, and design system usage. Use when creating or modifying @Composable functions, screens, components, themes, colors, typography, or when the user asks about UI styling, accessibility, previews, or responsive layouts.
---

# Compose UI 规范

## Composable 函数规范

### 命名与签名
- 名字用 **PascalCase 名词**：`OrderCard`、`PrimaryButton`
- 第一个参数是必需的数据参数，`modifier: Modifier = Modifier` **永远是第一个带默认值的参数**
- 回调参数命名 `onXxx`：`onClick`、`onDismiss`
- 返回 `Unit`，禁止返回任何值

### 正确示例

```kotlin
@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        // ...
    }
}
```

## 状态提升（State Hoisting）

- **无状态（stateless）组件优先**：组件不持有状态，通过参数接收
- 需要状态时提供两个版本：`StatefulXxx`（持有 ViewModel）和 `XxxContent`（纯展示）
- Screen 级别的 composable 命名为 `XxxScreen`，接收 ViewModel 并调用 `XxxContent`

```kotlin
@Composable
fun OrderListScreen(viewModel: OrderListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    OrderListContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OrderListContent(
    state: OrderListUiState,
    onIntent: (OrderListIntent) -> Unit,
    modifier: Modifier = Modifier,
) { ... }
```

## 主题与设计 Token

**禁止硬编码颜色、字号、圆角、间距。** 全部走 MaterialTheme：

| 场景       | 用这个                                          | 不要用             |
|-----------|--------------------------------------------------|--------------------|
| 颜色       | `MaterialTheme.colorScheme.primary`              | `Color(0xFF6200EE)` |
| 字体       | `MaterialTheme.typography.bodyLarge`             | `fontSize = 16.sp`  |
| 形状       | `MaterialTheme.shapes.medium`                    | `RoundedCornerShape(8.dp)` |
| 间距       | `Spacing.md`（自定义 token，见 Theme.kt）        | 散落的 `.dp`        |

项目统一间距规范：`Spacing.xs=4dp`, `sm=8dp`, `md=16dp`, `lg=24dp`, `xl=32dp`。

## 性能规则

1. **lambda 参数加 `remember`** 或提升到 ViewModel，避免每次重组新建
2. **`LazyColumn` 必须提供 `key`**：`items(list, key = { it.id })`
3. **避免在 Composable 里做计算**：用 `remember(key) { ... }` 或 `derivedStateOf`
4. **不稳定类型（List、Map）需要 `@Immutable` 或用 `ImmutableList`（kotlinx.collections.immutable）**
5. **Modifier 链顺序敏感**：`clickable` 应在 `padding` 之前，`background` 之后

## 预览（Preview）规范

每个可复用组件至少提供两个预览：

```kotlin
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreview

@ThemePreview
@Composable
private fun OrderCardPreview() {
    AppTheme {
        OrderCard(order = sampleOrder)
    }
}
```

Preview 函数用 `private`，命名以 `Preview` 结尾，必须包在 `AppTheme { ... }` 里。

## 可访问性（Accessibility）

- 所有图标必须有 `contentDescription`，装饰性图标用 `null`
- 可点击区域最小 `48.dp`（`Modifier.minimumInteractiveComponentSize()`）
- 使用 `semantics { }` 描述自定义交互
- 对比度检查：文字/背景对比度至少 4.5:1

## 响应式布局

- 使用 `WindowSizeClass`（compact / medium / expanded）
- 平板横屏布局必须用 `Row` + 权重，而不是固定宽度
- 避免 `Modifier.fillMaxWidth().height(固定 dp)` 这种组合