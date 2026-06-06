package com.example.myapplication

// Context：上下文，安卓四大组件都需要，这里用来打开数据库
import android.content.Context
// SQLiteDatabase：数据库对象，用来执行SQL语句
import android.database.sqlite.SQLiteDatabase
// SQLiteOpenHelper：数据库帮助类，帮我们管理数据库的创建和升级
import android.database.sqlite.SQLiteOpenHelper

/**
 * 数据库帮助类
 *
 * 作用：管理数据库的创建和版本升级
 *   - 第一次安装APP时，自动调用onCreate()建表
 *   - 数据库版本号变化时，自动调用onUpgrade()升级
 *
 * 就像一个"数据库管理员"：
 *   - 你告诉它数据库名、版本号
 *   - 它帮你建库、建表、升级
 *   - 你不用关心底层细节
 *
 * SQLiteOpenHelper构造函数参数说明：
 *   第1个参数 context：上下文（Activity传this就行）
 *   第2个参数 name：数据库名字（如"user_db.db"）
 *   第3个参数 factory：游标工厂（一般传null，用默认的）
 *   第4个参数 version：数据库版本号（从1开始，升级时+1）
 *
 * @param context 上下文
 */
class MySQLiteHelper(context: Context) : SQLiteOpenHelper(
    context,
    "user_db.db",   // 数据库文件名
    null,            // 游标工厂，传null用默认
    2                // 数据库版本号，从1升到2，触发onUpgrade升级
) {

    /**
     * onCreate：第一次创建数据库时自动调用
     *
     * 什么时候调用？
     *   - APP第一次安装后，第一次访问数据库时
     *   - 只调用一次！以后不会再调用
     *
     * 做什么？
     *   - 在这里写CREATE TABLE语句，创建所有需要的表
     *
     * @param db 数据库对象，用来执行SQL语句
     */
    override fun onCreate(db: SQLiteDatabase) {
        // ============================================
        // 创建用户表 user_table
        //
        // SQL语句说明：
        //   CREATE TABLE user_table  → 创建一个叫user_table的表
        //   id INTEGER PRIMARY KEY AUTOINCREMENT
        //     → id列：整数类型，主键，自动递增（1,2,3...）
        //   account TEXT UNIQUE
        //     → account列：文本类型，唯一（不能重复，注册时用来防重复）
        //   pwd TEXT
        //     → pwd列：文本类型，存密码
        //   sex TEXT
        //     → sex列：文本类型，存性别
        //   hobby TEXT
        //     → hobby列：文本类型，存爱好
        //
        // trimIndent()：去掉字符串前面的缩进空格，让SQL更好看
        // ============================================
        val sql = """
            CREATE TABLE user_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account TEXT UNIQUE,
                pwd TEXT,
                sex TEXT,
                hobby TEXT,
                city TEXT
            )
        """.trimIndent()

        // execSQL：执行SQL语句（不需要返回结果的语句用这个）
        db.execSQL(sql)
    }

    /**
     * onUpgrade：数据库版本升级时自动调用
     *
     * 什么时候调用？
     *   - 当构造函数中的version比上次大时
     *   - 例如：从1升到2，onUpgrade会被调用
     *
     * 做什么？
     *   - 在这里写升级逻辑（添加新表、添加新列等）
     *   - 目前版本1，不需要升级，留空即可
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // ============================================
        // 数据库版本升级逻辑
        //
        // 当版本号从1升到2时，给已有表新增city列
        // ALTER TABLE = 修改表结构
        // ADD COLUMN = 新增一列
        //
        // 为什么用if判断oldVersion？
        //   因为用户可能从版本1直接升到版本3
        //   每个版本的升级逻辑都要依次执行
        // ============================================
        if (oldVersion == 1) {
            db?.execSQL("ALTER TABLE user_table ADD COLUMN city TEXT")
        }
    }
}
