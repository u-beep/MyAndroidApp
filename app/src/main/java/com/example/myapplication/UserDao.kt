package com.example.myapplication

// ContentValues：键值对容器，用来存放要插入/更新的数据
import android.content.ContentValues
// Context：上下文
import android.content.Context
// SQLiteDatabase：数据库对象
import android.database.sqlite.SQLiteDatabase

/**
 * 用户数据操作类（DAO = Data Access Object 数据访问对象）
 *
 * 作用：封装所有对 user_table 表的增删改查操作
 *   - Activity 只需要调用 Dao 的方法，不需要写 SQL
 *   - SQL 细节全部封装在这里，代码更清晰
 *
 * 包含5个方法（CRUD + 登录查询）：
 *   1. addUser()     → 增加（注册）
 *   2. login()       → 查询（登录，根据账号查密码）
 *   3. getUserByAccount() → 查询（根据账号获取完整用户信息）
 *   4. getAllUser()   → 查询全部（列表展示）
 *   5. updatePwd()   → 修改（修改密码）
 *   6. deleteUser()  → 删除（删除用户）
 *
 * @param context 上下文，用来创建数据库帮助类
 */
class UserDao(context: Context) {

    // ============================================
    // 数据库对象，整个类共用这一个
    // writableDatabase：可读可写的数据库对象
    // ============================================
    private val db: SQLiteDatabase

    // init块：类实例化时自动执行的初始化代码
    init {
        // 创建数据库帮助类实例
        val helper = MySQLiteHelper(context)
        // 获取可读可写的数据库对象
        // 第一次调用时，如果数据库不存在，会自动调用onCreate建表
        db = helper.writableDatabase
    }

    // ============================================
    // 1. 增加：注册用户
    // ============================================

    /**
     * 添加新用户（注册）
     *
     * 原理：
     *   1. 把数据装进ContentValues（类似Map，键值对）
     *   2. 调用db.insert()插入数据库
     *   3. 返回值 = 新插入行的id，失败返回-1
     *
     * @param account 账号
     * @param pwd     密码
     * @param sex     性别
     * @param hobby   爱好
     * @param city    所在城市
     * @return true=注册成功，false=注册失败（账号重复等）
     */
    fun addUser(account: String, pwd: String, sex: String, hobby: String, city: String): Boolean {
        // ContentValues：键值对容器，key=列名，value=要存的数据
        val cv = ContentValues()
        cv.put("account", account)  // 第1列：账号
        cv.put("pwd", pwd)          // 第2列：密码
        cv.put("sex", sex)          // 第3列：性别
        cv.put("hobby", hobby)      // 第4列：爱好
        cv.put("city", city)        // 第5列：城市

        // db.insert(表名, 空列占位, 数据)
        //   第1个参数："user_table" = 表名
        //   第2个参数：nullColumnHack，一般传null
        //   第3个参数：ContentValues，要插入的数据
        // 返回值：新插入行的id，如果失败返回-1
        val id = db.insert("user_table", null, cv)

        // id != -1L 说明插入成功
        return id != -1L
    }

    // ============================================
    // 2. 查询：登录用（根据账号查密码）
    // ============================================

    /**
     * 登录查询：根据账号查找密码
     *
     * 原理：
     *   1. 用db.query()查询，条件是account=输入的账号
     *   2. 如果查到了，取出pwd列的值返回
     *   3. 如果没查到，返回null（说明账号不存在）
     *
     * db.query()参数说明：
     *   参数1：表名
     *   参数2：要查的列（null=查所有列）
     *   参数3：WHERE条件（"account=?"）
     *   参数4：WHERE条件的值（替换?的数组）
     *   参数5：GROUP BY（分组，传null）
     *   参数6：HAVING（分组条件，传null）
     *   参数7：ORDER BY（排序，传null）
     *
     * @param account 要查询的账号
     * @return 该账号的密码，账号不存在返回null
     */
    fun login(account: String): String? {
        // 执行查询
        val cursor = db.query(
            "user_table",              // 表名
            null,                       // 查所有列
            "account=?",                // WHERE条件：account=?
            arrayOf(account),           // 替换?的值
            null, null, null            // 分组、排序等，不需要
        )

        // cursor.moveToNext()：移动到下一行数据
        // 第一次调用时，从"第-1行"移到第0行（第一行）
        // 返回true=有数据，false=没数据了
        if (cursor.moveToNext()) {
            // getColumnIndexOrThrow("pwd")：获取pwd列的索引号
            // getString(索引号)：取出该列的字符串值
            val pwd = cursor.getString(cursor.getColumnIndexOrThrow("pwd"))
            cursor.close()  // 用完游标必须关闭！释放资源
            return pwd      // 返回查到的密码
        }

        cursor.close()  // 没查到也要关闭游标
        return null     // 账号不存在
    }

    // ============================================
    // 3. 查询：根据账号获取完整用户信息
    // ============================================

    /**
     * 根据账号获取用户完整信息
     *
     * 用途：修改密码时需要验证旧密码、主页显示当前用户性别爱好
     *
     * @param account 要查询的账号
     * @return User对象，账号不存在返回null
     */
    fun getUserByAccount(account: String): User? {
        val cursor = db.query(
            "user_table",
            null,
            "account=?",
            arrayOf(account),
            null, null, null
        )

        if (cursor.moveToNext()) {
            // 按列名取出各字段
            val acc = cursor.getString(cursor.getColumnIndexOrThrow("account"))
            val pwd = cursor.getString(cursor.getColumnIndexOrThrow("pwd"))
            val sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"))
            val hobby = cursor.getString(cursor.getColumnIndexOrThrow("hobby"))
            val city = cursor.getString(cursor.getColumnIndexOrThrow("city"))
            cursor.close()
            return User(acc, pwd, sex, hobby, city)
        }

        cursor.close()
        return null
    }

    // ============================================
    // 4. 查询全部：获取所有用户（列表展示用）
    // ============================================

    /**
     * 查询所有注册用户
     *
     * 用途：主页RecyclerView列表展示所有用户
     *
     * @return 所有用户的列表
     */
    fun getAllUser(): MutableList<User> {
        val list = mutableListOf<User>()

        // 查询全部数据，没有WHERE条件
        val cursor = db.query("user_table", null, null, null, null, null, null)

        // while循环遍历所有行
        while (cursor.moveToNext()) {
            // 按列索引取出各字段
            // 索引对应建表时的顺序：0=id, 1=account, 2=pwd, 3=sex, 4=hobby, 5=city
            val account = cursor.getString(1)   // 第1列：account
            val pwd = cursor.getString(2)       // 第2列：pwd
            val sex = cursor.getString(3)       // 第3列：sex
            val hobby = cursor.getString(4)     // 第4列：hobby
            val city = cursor.getString(5)      // 第5列：city

            // 封装成User对象，加入列表
            list.add(User(account, pwd, sex, hobby, city))
        }

        cursor.close()
        return list
    }

    // ============================================
    // 5. 修改：更新密码
    // ============================================

    /**
     * 修改密码
     *
     * 原理：
     *   1. 把新密码装进ContentValues
     *   2. 调用db.update()，条件是account=指定账号
     *   3. 只更新pwd列，其他列不变
     *
     * db.update()参数说明：
     *   参数1：表名
     *   参数2：要更新的数据（ContentValues）
     *   参数3：WHERE条件
     *   参数4：WHERE条件的值
     *
     * @param account 要修改密码的账号
     * @param newPwd  新密码
     * @return 受影响的行数（0=没找到该账号，1=修改成功）
     */
    fun updatePwd(account: String, newPwd: String): Int {
        val cv = ContentValues()
        cv.put("pwd", newPwd)  // 只更新密码列

        return db.update("user_table", cv, "account=?", arrayOf(account))
    }

    // ============================================
    // 6. 删除：删除用户
    // ============================================

    /**
     * 删除用户
     *
     * 原理：
     *   调用db.delete()，条件是account=指定账号
     *
     * @param account 要删除的账号
     * @return 受影响的行数（0=没找到，1=删除成功）
     */
    fun deleteUser(account: String): Int {
        return db.delete("user_table", "account=?", arrayOf(account))
    }
}
