package com.example.myapplication

/**
 * 备忘录实体类
 * 
 * 包含：id、标题、内容、创建时间
 * 用于SQLite存储和RecyclerView展示
 */
data class Memo(
    var id: Long = 0,
    var title: String,
    var content: String,
    var createTime: String
)