# ToolKit 组件化开发指南

本文是完整开发参考；强制规则以 `AGENTS.md` 为准，快速定位以生成的 `docs/PROJECT_INDEX.md` 为准，具体模块边界以模块自己的 `README.md` 为准。

## 1. 架构目标与依赖方向

```text
app ───────────────► feature ─────────► core
 │                                      ▲
 └──────────────────────────────────────┘

legacy ─► 必需三方库或其他 legacy
core    -X-► feature / legacy
feature -X-► other feature
```

`app` 只做正式应用组装、启动和顶层导航；业务实现由 feature 持有。`core:*` 按单一职责提供基础能力，不知道任何业务模块。`legacy:*` 是只减不增的兼容隔离区。

每个 feature 使用单一 `module_xxx` Gradle 模块，不默认建立 api/impl 子模块。跨业务页面跳转使用 `RouterManager` 与 `core:navigation` 的 `RouterPath`，不通过类引用或业务模块项目依赖穿透边界。

## 2. AI 与开发者定位流程

开始修改前按以下最小读取路径定位：

1. 在 `docs/PROJECT_INDEX.md` 搜索业务词、类型名、路由或资源名。
2. 阅读目标模块 README，确认职责、依赖和公共入口。
3. 打开索引命中的实现和相邻测试，确认真实行为与项目风格。
4. 索引无结果时先查当前模块，再查职责匹配的 core；只有仍无结果才扩大到仓库搜索。

索引是定位表，不替代源码。命中同名文件后仍要检查调用方、构建变体、Manifest 和生成代码，不能根据名字猜行为。

## 3. 代码归属决策

- 只服务一个页面或流程：放所属 feature。
- 跨业务字符串路由：放 `core:navigation` 的 `RouterPath` 对应分组。
- 两个以上模块稳定复用、与业务无关：放职责匹配的单一 core。
- 应用启动或多个 feature 的组合：放 app；可被 standalone 复用的进程初始化放 `core:startup`。
- 历史接口兼容修复：留在 legacy；现代替代实现不要继续进入 legacy。
- 不确定归属时先保留在最靠近使用方的位置，等第二个真实使用方出现再抽取。

新建文件前必须说明已有实现不能复用的原因、现有文件无法承载的原因和目标模块的唯一归属理由。不要为了“以后可能复用”创建空层、单方法包装器或平行工具类。

## 4. 新增业务模块

以 `module_order` 为例：

1. 创建单一 `module_order`，在 `settings.gradle` 注册；不要默认创建 api/impl 子模块。
2. 使用 `toolkit.android.feature`；路由使用 `toolkit.android.arouter`，注入使用 `toolkit.android.hilt`。
3. 业务实现、内部接口与模型放在模块自己的 `src/main`；真正稳定的跨模块模型评估进入 `core:model`。
4. 跨业务路由添加到 `RouterPath.Order`，feature 默认只依赖所需 core，不依赖其他 feature。
5. 在 `app` 中依赖 `module_order` 完成正式应用组装。
6. 需要独立 APK 时，在 build-logic 的 feature 支持列表登记，提供 `src/standalone/AndroidManifest.xml` 与 Launcher/Application。
7. 为模块编写一个标准 README，补路由契约测试、业务测试和 app/standalone 构建验证。
8. 运行 `generateProjectIndex`，再运行两个治理校验任务。

## 5. Package、文件与类型命名

- package 以模块 namespace 为根，按职责使用 `ui`/页面域、`viewmodel`、`repository`、`api`、`model`、`di` 等已有分层。
- 文件与主要类型同名；一个文件只放一个主要职责，紧密且私有的小类型可同文件定义。
- 页面相关类型保持领域前缀一致：`OrderDetailActivity`、`OrderDetailViewModel`、`OrderRepository`、`OrderUiState`。
- 不新增裸 `Utils`、`Common`、`Helper`、`Manager` 或 `Base`。按能力命名，例如 `DateFormatter`、`KeyboardController`、`OtaDownloadCoordinator`。
- 布尔值使用 `is`、`has`、`can` 前缀；函数名表达动作和结果，不用 `handleData`、`doWork` 等模糊名称。
- DTO 对应传输协议，Entity 对应持久化，Domain Model 表达业务，UiModel/UiState 服务渲染；跨层转换在边界完成。
- 对外可见面最小化：能 `private`/`internal` 就不公开，跨模块入口集中在 core 契约或模块 README 标出的公共能力。

## 6. Android 资源规范

资源采用 `<owner>_<type>_<purpose>`：

- app：`app_*`
- design system：`common_*`
- Web core：`web_*`
- feature：`login_*`、`home_*`、`mine_*`、`ota_*`

类型段固定使用 `activity`、`fragment`、`item`、`dialog`、`view`、`bg`、`ic`、`color`、`dimen`、`style`、`menu`、`file_paths` 等清晰词。例如 `login_activity_login`、`home_item_chat`、`common_color_text_main`。

新增资源前先查项目索引；同语义颜色、尺寸、文案和 drawable 必须复用。跨 feature 的视觉 token 放 design system，业务专属资源留在 feature。Launcher 与少量历史公开资源由架构守卫固定例外冻结，新资源不得加入例外绕过命名。

## 7. UI、状态与交互

- 维护现有 XML 页面时继续使用 XML + ViewBinding，不在普通需求中顺手迁移技术栈。
- 新页面或明确迁移可以使用 Compose；同一页面保持一种主渲染体系，不做无意义混搭。
- XML 页面复用 `BaseActivity<VB>`、`BaseFragment<VB>` 和现有扩展；Compose 遵循 state hoisting 和项目 Material 主题。
- View 只渲染状态和发送意图，不直接发网络请求、访问存储或持有跨页面全局状态。
- 简单页面使用 MVVM；存在并发加载、重试和复杂事件时复用 `MviViewModel`/`MviContract`，不要为静态页面强行引入 MVI。
- 一次性事件与持久 UI 状态分离；旋转或重建后应恢复状态且不重复导航、Toast 或请求。
- 提交前检查空态、加载态、错误态、重复点击、返回键、无障碍描述和多语言长度。

## 8. 数据、网络与错误处理

业务 API、DTO 和 Repository 放所属 feature；客户端、拦截器和通用响应规则放 `core:network`。`core:network` 现存的 `CommonApi` 与 Banner/Feed DTO 是待迁移演示兼容项，不得继续向其中添加新业务接口：

```kotlin
interface OrderApi {
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): BaseResponse<OrderDto>
}

class OrderRepository @Inject constructor(
    private val api: OrderApi,
) : BaseRepository() {
    fun getOrder(id: String): Flow<OrderDto> = request { api.getOrder(id) }
}
```

- Repository 通过构造注入依赖，不在方法内部创建 Retrofit、数据库或全局单例。
- 通用网络错误由 `ApiException` 等统一类型保留 code/message/cause，再在 feature 边界映射为领域错误。
- 禁止空 catch、把所有错误改成 `null`、静默吞掉取消异常或只返回“失败”而丢失诊断信息。
- 重试必须限定次数、退避和可重试错误；写操作默认不自动重试，除非服务端具备幂等契约。
- Token、401 回调和缓存目录由 Application 组装一次，业务页面不直接改全局客户端。

## 9. 协程、线程与生命周期

- 使用 `viewModelScope`、`lifecycleScope` 或注入的受控应用作用域；禁止 `GlobalScope` 和无所有者线程。
- UI 层按生命周期收集 Flow，避免后台页面继续更新 View。
- 调度器在数据边界注入或集中切换，调用方不层层重复 `withContext`。
- 正确传播 `CancellationException`；并发子任务根据是否需要共同失败选择 `coroutineScope` 或 `supervisorScope`。
- 不在主线程执行磁盘、网络、解码或大 JSON 操作；共享可变状态使用 StateFlow、Mutex 或明确线程约束。

## 10. 日志、安全与配置

- 日志带稳定 tag 和必要上下文，但不得记录密码、token、Cookie、完整 Authorization、个人信息或完整响应体。
- 网络日志使用已有截断与脱敏机制，Release 不开启无界明文日志。
- 密钥、签名、服务账号和环境地址不写入源码或版本库；通过本地配置、环境变量或发布系统注入。
- WebView/TBS、FileProvider、安装权限和网络安全配置按最小权限设计；新增 exported 组件必须说明外部调用契约。

## 11. 测试策略

按风险选择最小稳定测试层级：

- 纯 Kotlin：状态转换、格式化、验证器、架构规则和索引渲染。
- Repository/ViewModel：成功、业务错误、异常、取消和重复请求。
- 契约测试：路由常量、资源名称、Manifest 与 standalone 组装。
- 构建测试：受影响模块 assemble、app assemble、相关 standalone。
- 真机验收：OTA 权限与安装、真实下载、WebView/TBS、主题语言视觉和系统交互。

测试名描述行为和条件，不保留框架模板样例，不为了覆盖率测试第三方库实现。Bug 修复应先写能稳定复现根因的失败测试，再实现修复。

## 12. 独立组件运行

`-Pstandalone=<feature>` 将目标 feature 切换为 Application，使用独立 Manifest、Launcher 和 applicationId。支持值为 `login`、`home`、`mine`、`ota`；未知值配置阶段失败。`mine` standalone 因页面可进入 OTA，构建分支会额外组装 OTA，这不是普通 feature→feature 依赖许可。

```bash
./gradlew :module_login:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:assembleDebug -Pstandalone=ota --offline
```

独立构建通过只证明组件可单独编译打包；有外部系统能力时仍需安装运行验证。

## 13. 文档与项目索引

每个模块 README 固定包含：模块职责、模块类型、依赖规则、目录结构、公共入口、新代码放置、验证命令。README 描述边界和决策，不复制每个源码文件。

模块、依赖、公开源码入口、路由或 Android 资源变化后运行：

```bash
./gradlew generateProjectIndex --offline
./gradlew verifyProjectIndex --offline
```

索引必须由任务生成并提交，不手工修补。模块职责改变时先更新模块 README，再重新生成索引。项目级规则改变时同步 `AGENTS.md` 与本文，避免多份冲突规范。

## 14. 完成与代码审查清单

提交前逐项确认：

- 修改归属是否唯一正确，是否复用了现有类型和资源。
- 是否引入同义类、同义资源、重复状态源或无调用方抽象。
- core/feature/app 依赖方向是否正确，是否通过统一路由跨 feature。
- UI 状态、错误、协程取消、生命周期和敏感日志是否安全。
- 新公共入口和资源是否已进入生成索引，模块 README 是否仍真实。
- 测试是否验证行为而非实现细节，编译验证与真机验收边界是否说明。
- 暂存区是否只含当前任务，是否保留用户无关改动。

完整治理基线：

```bash
./gradlew -p build-logic build --offline
./gradlew verifyArchitecture verifyProjectIndex --offline
./gradlew testDebugUnitTest :app:assembleDebug --offline
./gradlew :module_login:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:assembleDebug -Pstandalone=ota --offline
git diff --check
```
