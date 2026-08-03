---
name: compose-ui
description: Use when creating or modifying Android Compose screens, @Composable components, Material 3 themes, design tokens, state hoisting, previews, accessibility, performance, or responsive layouts.
---

# Compose UI 规范

## 组件签名

- 名称使用 PascalCase 名词；Screen 级组件命名为 `XxxScreen`。
- 必需数据参数在前，`modifier: Modifier = Modifier` 是第一个带默认值的参数。
- 回调命名 `onXxx`，Composable 返回 `Unit`。
- 优先无状态组件；Screen 收集 ViewModel 状态并委托给纯展示 Content。

```kotlin
@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        // content
    }
}
```

## 状态与生命周期

- Flow 使用 `collectAsStateWithLifecycle()`。
- 副作用放在正确的 Effect 中，并提供与依赖一致的 key。
- 状态提升到最小共同拥有者；业务状态由 ViewModel 持有。
- Composable 中不创建 Repository、不直接调用网络、不使用 `GlobalScope`。

## 主题与设计系统

- 颜色、字体、形状使用 `MaterialTheme`；间距使用项目 design token。
- 若模块尚无 Compose token，先在 design system 建立可复用 token，避免页面内散落魔法值。
- 现有 XML 与 Compose 共存时复用相同颜色语义和深色模式策略。

## 性能

- Lazy 列表提供稳定 `key`。
- 昂贵计算使用 `remember(key)` 或 `derivedStateOf`。
- 稳定建模集合与 UI state，避免不必要重组。
- 检查 Modifier 顺序对点击区域、背景、裁剪和 padding 的实际影响。

## 预览与无障碍

- 可复用组件提供至少 Light/Dark 预览并包在项目 Theme 中。
- 图标提供 `contentDescription`；纯装饰图标使用 `null`。
- 可点击区域至少 48dp，自定义交互补充 semantics。
- 文字与背景对比度至少 4.5:1。

## 响应式布局

- 根据 WindowSizeClass 设计 compact/medium/expanded，而不是固定屏宽。
- 平板优先约束、权重或自适应 pane；避免把手机固定高度简单放大。
