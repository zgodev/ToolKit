# CLAUDE.md

本项目的统一 AI 协作规则以 [AGENTS.md](AGENTS.md) 为唯一事实来源。Claude Code 在开始修改前必须先阅读该文件。

Claude 专用技能位于 `.claude/skills/`；Codex 镜像位于 `.agents/skills/`。两边保持同名、同语义。

UI 不做全局技术禁用：维护现有 XML 页面时默认延续 XML + ViewBinding；新页面或明确迁移任务可根据模块设计选择 XML 或 Compose。
