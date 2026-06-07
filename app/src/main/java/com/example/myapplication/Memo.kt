package com.example.myapplication

/**
 * 备忘录实体类
 * 
 * 包含：id、标题、内容、创建时间、条目类型
 * 用于SQLite存储和RecyclerView多布局展示
 * 
 * itemType说明：
 *   0 = 普通备忘录条目
 *   1 = 广告条目
 */
data class Memo(
    var id: Long = 0,
    var title: String,
    var content: String,
    var createTime: String,
    // 0=普通备忘录条目，1=广告条目
    var itemType: Int = 0
)