package com.example.myapplication

/**
 * 备忘录条目数据实体类
 * 作用：封装单条备忘录的数据，方便在列表中传递和显示
 *
 * data class：Kotlin的数据类，自动生成equals、hashCode、toString等方法
 *
 * @param id        备忘录唯一ID（用时间戳生成，区分不同备忘录）
 * @param content   备忘录文字内容
 * @param timestamp 保存时间（格式化后的字符串，如"2026-06-06 14:30"）
 */
data class MemoItem(
    val id: Long,           // 唯一ID（时间戳）
    val content: String,    // 内容
    val timestamp: String   // 保存时间
)
