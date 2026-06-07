package com.example.myapplication.bean

import java.io.Serializable

/**
 * 新闻列表接口响应实体类（Gson解析用）
 *
 * 知识点：
 *   - data class：Kotlin数据类，自动生成getter/setter/equals/hashCode/toString
 *   - Gson解析要求：JSON字段名和Kotlin属性名完全一致
 *   - @SerializedName：当JSON字段名和Kotlin属性名不一致时，用此注解映射
 *
 * 天行数据接口返回JSON示例：
 * {
 *   "code": 200,
 *   "msg": "success",
 *   "newslist": [
 *     {"title":"新闻标题", "source":"来源", "ctime":"2024-01-01"}
 *   ]
 * }
 */
data class NewsResp(
    val code: Int,
    val msg: String,
    val newslist: List<NewsItem>
)

/**
 * 单条新闻实体类
 *
 * @param title 新闻标题
 * @param source 新闻来源
 * @param ctime 发布时间（天行数据字段名为ctime）
 * @param description 新闻描述
 * @param picUrl 新闻图片URL
 * @param url 新闻详情链接
 */
data class NewsItem(
    val title: String,
    val source: String,
    val ctime: String,
    val description: String = "",
    val picUrl: String = "",
    val url: String = ""
) : Serializable
