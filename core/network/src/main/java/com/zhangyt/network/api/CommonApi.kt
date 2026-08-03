package com.zhangyt.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 通用 API，包含一些示例接口，业务模块按需新建自己的 Api 接口。
 */
interface CommonApi {

    /** 示例：首页 Banner。 */
    @GET("api/v1/home/banner")
    suspend fun getBanner(): BaseResponse<List<BannerBean>>

    /** 示例：分页列表。 */
    @GET("api/v1/feed/list")
    suspend fun getFeedList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 20
    ): BaseResponse<FeedPage>
}

data class BannerBean(
    val id: String = "",
    val title: String = "",
    val image: String = "",
    val targetUrl: String = ""
)

data class FeedPage(
    val total: Int = 0,
    val list: List<FeedItem> = emptyList()
)

data class FeedItem(
    val id: String = "",
    val title: String = "",
    val cover: String = "",
    val description: String = ""
)
