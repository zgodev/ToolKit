# CLAUDE.md

本项目的统一 AI 强制规则以 [AGENTS.md](AGENTS.md) 为唯一事实来源。Claude Code 开始修改前必须依次阅读：

1. `AGENTS.md`
2. `docs/PROJECT_INDEX.md`
3. 目标模块的 `README.md`

索引无结果或架构设计需要更多上下文时，再阅读 `FRAMEWORK_GUIDE.md` 并扩大源码搜索。禁止手工编辑生成的 `docs/PROJECT_INDEX.md`。

Claude 专用技能位于 `.claude/skills/`；Codex 镜像位于 `.agents/skills/`。两边保持同名、同语义，项目特定模块规则不要复制到技能中。

UI 不做全局技术禁用：维护现有 XML 页面时默认延续 XML + ViewBinding；新页面或明确迁移任务可根据模块设计选择 XML 或 Compose。
