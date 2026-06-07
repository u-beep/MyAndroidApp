package com.example.myapplication

/**
 * API配置文件（密钥、接口地址统一管理）
 *
 * 知识点：
 *   - 将密钥和URL抽离到单独文件，方便统一管理
 *   - 修改接口地址或密钥时只需改这一个文件
 *   - 上传Git时可以将此文件加入.gitignore，防止密钥泄露
 *
 * 使用方式：
 *   val url = ApiConfig.NEWS_URL
 */
object ApiConfig {

    // ========== 天行数据API Key ==========
    // 注册地址：https://www.tianapi.com/
    // 控制台 → 我的API → 复制Key
    const val TIAN_API_KEY = "1596f48ac3d93c7c663d5fc8312ac1b4"

    // ========== 新闻接口地址（分页加载） ==========
    // 综合新闻列表（免费，每天有额度限制）
    // page参数：翻页，第1页、第2页...
    // num参数：每页返回数量，最大50
    const val NEWS_BASE_URL = "http://api.tianapi.com/generalnews/index"
    const val NEWS_PAGE_SIZE = 6

    /**
     * 生成分页请求URL
     * @param page 页码（从1开始）
     */
    fun getNewsUrl(page: Int): String {
        return "${NEWS_BASE_URL}?key=${TIAN_API_KEY}&num=${NEWS_PAGE_SIZE}&page=${page}"
    }

    // 兼容旧代码：首页URL
    val NEWS_URL: String
        get() = getNewsUrl(1)
}
