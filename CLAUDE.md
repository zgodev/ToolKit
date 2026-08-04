# CLAUDE.md

本项目的统一 AI 强制规则以 [AGENTS.md](AGENTS.md) 为唯一事实来源。Claude Code 开始修改前必须依次阅读：

1. `AGENTS.md`
2. `docs/PROJECT_INDEX.md`
3. 业务模块任务读取目标模块的 `README.md`；根目录文档、AI 规则、`build-logic`、依赖治理、CI 或全局配置任务改读直接相关的根目录文档或 `build-logic/README.md`

任务实际触达某个模块后必须补读该模块 README。索引无结果或架构设计需要更多上下文时，再阅读 `FRAMEWORK_GUIDE.md` 并扩大源码搜索。禁止手工编辑生成的 `docs/PROJECT_INDEX.md`；规则冲突按 `AGENTS.md` 定义的优先级处理。

Claude 专用技能位于 `.claude/skills/`；Codex 镜像位于 `.agents/skills/`。两边保持同名、同语义，项目特定模块规则不要复制到技能中。

UI 技术选择遵循 `AGENTS.md`：新增页面和可复用 UI 默认使用 Compose；XML 仅限既有页面的有界维护、缺少可靠 Compose 适配的 View，或已验证的兼容性、性能、无障碍阻塞，并记录采用例外的具体原因。
