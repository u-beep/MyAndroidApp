package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

/**
 * 备忘录数据库操作类
 * 
 * 封装全套CRUD操作：
 *   1. 新增备忘录
 *   2. 修改备忘录
 *   3. 删除备忘录
 *   4. 查询单条备忘录
 *   5. 查询所有备忘录（按时间倒序排列）
 */
class MemoDao(context: Context) {
    private val helper = MemoDBHelper(context)

    /**
     * 1. 新增备忘录
     * @param memo 备忘录对象
     * @return 插入的行ID，失败返回-1
     */
    fun addMemo(memo: Memo): Long {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(MemoDBHelper.TITLE, memo.title)
            put(MemoDBHelper.CONTENT, memo.content)
            put(MemoDBHelper.TIME, memo.createTime)
        }
        val id = db.insert(MemoDBHelper.TABLE_MEMO, null, cv)
        db.close()
        return id
    }

    /**
     * 2. 修改备忘录
     * @param memo 备忘录对象（包含要修改的id）
     * @return 受影响的行数
     */
    fun updateMemo(memo: Memo): Int {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(MemoDBHelper.TITLE, memo.title)
            put(MemoDBHelper.CONTENT, memo.content)
        }
        val count = db.update(
            MemoDBHelper.TABLE_MEMO,
            cv,
            "${MemoDBHelper.ID}=?",
            arrayOf(memo.id.toString())
        )
        db.close()
        return count
    }

    /**
     * 3. 删除备忘录
     * @param id 备忘录ID
     * @return 受影响的行数
     */
    fun deleteMemo(id: Long): Int {
        val db = helper.writableDatabase
        val res = db.delete(
            MemoDBHelper.TABLE_MEMO,
            "${MemoDBHelper.ID}=?",
            arrayOf(id.toString())
        )
        db.close()
        return res
    }

    /**
     * 4. 根据ID查询单条备忘录
     * @param id 备忘录ID
     * @return 备忘录对象，如果不存在返回null
     */
    fun getMemoById(id: Long): Memo? {
        val db = helper.readableDatabase
        val cursor: Cursor = db.query(
            MemoDBHelper.TABLE_MEMO,
            null,
            "${MemoDBHelper.ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )
        var memo: Memo? = null
        if (cursor.moveToFirst()) {
            memo = Memo(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MemoDBHelper.ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.TITLE)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.CONTENT)),
                createTime = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.TIME))
            )
        }
        cursor.close()
        db.close()
        return memo
    }

    /**
     * 5. 查询所有备忘录（按ID倒序排列，最新的在最前面）
     * @return 备忘录列表
     */
    fun getAllMemo(): MutableList<Memo> {
        val list = mutableListOf<Memo>()
        val db = helper.readableDatabase
        val cursor: Cursor = db.query(
            MemoDBHelper.TABLE_MEMO,
            null,
            null,
            null,
            null,
            null,
            "${MemoDBHelper.ID} DESC" // 按ID倒序排列，最新添加的显示在最前面
        )
        while (cursor.moveToNext()) {
            val memo = Memo(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MemoDBHelper.ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.TITLE)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.CONTENT)),
                createTime = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.TIME))
            )
            list.add(memo)
        }
        cursor.close()
        db.close()
        return list
    }
}