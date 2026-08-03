# ToolKit 组件化开发指南

## 1. 依赖方向

```text
app ───────────────► feature impl ─► own api
 │                       │          other feature api
 │                       └────────► core
 └───────────────────────────────► core

legacy ─► 所需三方库或其他 legacy/core
core    -X-► feature
feature -X-► other feature impl
```

`app` 负责跨业务编排；业务页面不能通过类引用跨 feature，统一使用各 `module_xxx:api` 中的路由或接口契约。

## 2. 新增业务模块

以 `module_order` 为例：

1. 创建 `module_order/api` 与 `module_order/impl`，在 `settings.gradle` 注册两个项目。
2. api 使用 `toolkit.android.library`，只放 `OrderRoutes`、跨模块接口和必要的稳定模型。
3. impl 使用 `toolkit.android.feature`；有路由时加 `toolkit.android.arouter`，有注入时加 `toolkit.android.hilt`。
4. impl 依赖自身 api、所需 core 和其他 feature api，不依赖其他 impl。
5. 在 `app` 中同时依赖 api/impl 完成集成。
6. 若需独立 APK，在 `AndroidFeatureConventionPlugin` 的支持列表中注册，并提供 `src/standalone/AndroidManifest.xml` 与独立 Launcher/Application。
7. 补路由契约测试、模块单测和 app/standalone 构建验证。

## 3. 路由

业务路径归所属 feature api：

```kotlin
object OrderRoutes {
    const val ACTIVITY_DETAIL = "/order/activity_detail"
}

@Route(path = OrderRoutes.ACTIVITY_DETAIL)
class OrderDetailActivity : BaseActivity<OrderActivityDetailBinding>()

RouterManager.start(OrderRoutes.ACTIVITY_DETAIL)
```

通用页面路径可放 `core:navigation`。ARouter 的处理器模块名由完整 Gradle path 生成，避免多个 `impl` 项目产生同名类。

## 4. UI 与状态

- 现有 XML 页面默认继续使用 XML + ViewBinding；新模块可按设计选择 XML 或 Compose。
- XML 资源带模块前缀，并优先使用 `core:designsystem` 的主题属性。
- Activity/Fragment 复用 `BaseActivity<VB>` / `BaseFragment<VB>`。
- 简单页面使用 MVVM；复杂状态可复用 `MviViewModel` 契约。
- Flow 在 UI 层按生命周期收集，协程使用 `viewModelScope` / `lifecycleScope`。

## 5. 网络与会话

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

`BaseRepository.request` 将 `code == 0` 的非空 data 解包；业务错误保留后端 code/message，空成功体转换为 `ApiException`。Token、401 回调和缓存目录在应用 Application 中配置一次。

## 6. 基础能力位置

- 主题/通用 View：`core:designsystem`
- 多语言：`core:locale`
- 用户会话：`core:session`
- Web：`core:web`，需要的 Application 显式调用 `WebInitializer.initialize()`
- 网络：`core:network`
- 旧 HTTP/WebSocket：`legacy:network`
- 旧工具和本地二进制：`legacy:utils`

新代码优先进入职责明确的 core 或 feature；不要恢复 `lib_common` 式的全量传递依赖。

## 7. 验证基线

```bash
./gradlew testDebugUnitTest --offline
./gradlew :app:assembleDebug --offline
./gradlew :module_login:impl:assembleDebug -Pstandalone=login --offline
./gradlew :module_home:impl:assembleDebug -Pstandalone=home --offline
./gradlew :module_mine:impl:assembleDebug -Pstandalone=mine --offline
./gradlew :module_ota:impl:assembleDebug -Pstandalone=ota --offline
git diff --check
```

单元测试覆盖可稳定验证的契约；OTA 安装权限、真实下载、WebView/TBS 和主题/语言视觉效果仍需设备验证。
