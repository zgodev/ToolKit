---
name: xml-ui
description: Use when creating or modifying Android XML layouts, ViewBinding screens, drawable and resource assets, ConstraintLayout structure, dp and sp sizing, Figma handoff, or XML-based responsive UI.
---

# XML UI 规范

- 优先用 ConstraintLayout 减少无意义嵌套，但先保证约束完整和可读。
- 资源先在当前 feature 查找，再查 `core:designsystem`；没有合适的共享语义时才新增。
- 布局尺寸使用 dp，字体使用 sp；避免 px 和无依据的固定屏幕坐标。
- 资源名使用所属模块前缀，颜色优先走 `?attr/common_color_*` 或 design system。
- 使用 ViewBinding；不要用 `findViewById` 重建已有绑定流程。
- Figma 图标在仓库不存在时放到所属模块合适的 drawable/mipmap 密度目录，并核对授权和分辨率。
- 当设计稿明确以 1920 宽设计、目标画布明确为 1334 宽时，尺寸按 `1334 / 1920 ≈ 0.694` 换算取整；不要把该比例泛化到其他页面或设备。
- 检查小屏、长文本、横屏、字体缩放、深色模式和最小点击区域。
