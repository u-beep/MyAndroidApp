package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 备忘录数据库帮助类
 * 
 * 功能：
 *   1. 创建memo_db数据库
 *   2. 创建memo数据表
 *   3. 处理数据库版本升级
 * 
 * 数据库表结构：
 *   _id: 自增主键
 *   title: 备忘录标题
 *   content: 备忘录内容
 *   create_time: 创建时间
 */
class MemoDBHelper(context: Context) : SQLiteOpenHelper(context, "memo_db", null, 1) {
    // 表名和字段常量
    companion object {
        const val TABLE_MEMO = "memo"
        const val ID = "_id"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val TIME = "create_time"
    }

    // 创建数据库表
    override fun onCreate(db: SQLiteDatabase) {
        val createSql = """
            CREATE TABLE $TABLE_MEMO(
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TITLE TEXT,
                $CONTENT TEXT,
                $TIME TEXT
            )
        """.trimIndent()
        db.execSQL(createSql)
    }

    // 数据库版本升级处理
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 删除旧表并重新创建（简单处理，实际项目中可能需要数据迁移）
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MEMO")
        onCreate(db!!)
    }
}