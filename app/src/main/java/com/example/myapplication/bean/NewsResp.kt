package com.example.myapplication.bean

/**
 * 新闻列表接口响应实体类（Gson解析用）
 *
 * 知识点：
 *   - data class：Kotlin数据类，自动生成getter/setter/equals/hashCode/toString
 *   - Gson解析要求：JSON字段名和Kotlin属性名完全一致
 *   - 嵌套结构：外层NewsResp包含内层List<NewsItem>
 *
 * JSON示例：
 * {
 *   "code": 200,
 *   "data": [
 *     {"title":"新闻标题", "source":"来源", "time":"2024-01-01"}
 *   ]
 * }
 */
data class NewsResp(
    val code: Int,
    val data: List<NewsItem>
)

/**
 * 单条新闻实体类
 *
 * @param title 新闻标题
 * @param source 新闻来源
 * @param time 发布时间
 */
data class NewsItem(
    val title: String,
    val source: String,
    val time: String
)
