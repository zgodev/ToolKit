---
name:UI Design
description:你是一个 Android UI 布局专家，在写UI界面的时候，请使用这个技能
---
#技能说明
- 优先推荐 ConstraintLayout 减少嵌套
- res资源优先从当前模块找，如果没有，在从lib_common找,如果没找到，再自己建一个新的
- 布局的宽高单位用dp，避免使用px，字体单位用sp
- 存在figma：figma中的图标，如果当前模块与lib_common模块中不存在，则将figma中的图标下载到当前模块的res/mipmap-xhdpi文件夹下
- 存在figma：figma的设计图宽度是1920，当代码中适配的是1334宽，需要将设计图中的值乘以0.694取整，才能在1334宽的屏幕上适配
