package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
// LinearLayoutManager：RecyclerView的竖直/横向布局管理器
import androidx.recyclerview.widget.LinearLayoutManager
// RecyclerView：安卓最核心的列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 主页面 Activity
 *
 * 功能：
 *   1. 显示欢迎信息（登录账号）
 *   2. 显示当前用户的性别和爱好（从数据库查询）
 *   3. 用RecyclerView列表展示所有注册用户信息（从数据库读取）
 *   4. 退出登录功能
 *   5. 进入备忘录
 *
 * 数据来源：
 *   所有数据从 SQLite 数据库读取（UserDao）
 *   不再使用 SharedPreferences 的 USER_LIST
 */
class MainActivity : AppCompatActivity() {

    // ============================================
    // 定义集合存放所有用户数据
    // MutableList<User>：可变列表，可以增删元素
    // ============================================
    private val userList = mutableListOf<User>()

    // 适配器引用，lateinit表示"稍后初始化"（onCreate中才初始化）
    private lateinit var userAdapter: UserAdapter

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_main)

        // ============================================
        // 绑定控件
        // ============================================
        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)         // 欢迎文字
        val tvInfo = findViewById<TextView>(R.id.tv_info)               // 用户信息文字
        val btnLogout = findViewById<Button>(R.id.btn_logout)           // 退出按钮
        val rvUserList = findViewById<RecyclerView>(R.id.rv_user_list)  // 列表容器

        // ============================================
        // 接收登录页传来的账号
        // ============================================
        val account = intent.getStringExtra("USER_ACCOUNT") ?: ""
        tvWelcome.text = "欢迎 $account !"

        // ============================================
        // 创建 UserDao 实例（整个页面共用）
        // ============================================
        val userDao = UserDao(this)

        // ============================================
        // 从数据库读取当前用户的性别和爱好
        // getUserByAccount(account)：根据账号查完整用户信息
        // 比以前SP的"密码|性别|爱好"切割方便多了！
        // ============================================
        val currentUser = userDao.getUserByAccount(account)
        if (currentUser != null) {
            tvInfo.text = "性别：${currentUser.sex}  爱好：${currentUser.hobby}"
        }

        // ============================================
        // 设置RecyclerView
        // ============================================
        rvUserList.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList)
        rvUserList.adapter = userAdapter

        // ============================================
        // 从数据库读取全部注册用户，装载进列表
        // getAllUser()：查询所有用户，返回List<User>
        // 比以前遍历SP的all.keys再切割字符串简洁多了！
        // ============================================
        loadUserList(userDao)

        // ============================================
        // 设置条目点击事件
        // ============================================
        userAdapter.itemClick = { user ->
            Toast.makeText(this, "选中：${user.account}", Toast.LENGTH_SHORT).show()
        }

        // ============================================
        // 退出登录 → 跳回登录页
        // ============================================
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ============================================
        // 备忘录按钮 → 跳转到备忘录页面
        // ============================================
        val btnMemo = findViewById<Button>(R.id.btn_memo)
        btnMemo.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)   // 传账号
            startActivity(intent)
        }
    }

    /**
     * 从数据库读取所有注册用户，装载进列表
     *
     * 对比以前SP的方式：
     *   SP：遍历all.keys → getString → split("\\|") → 逐段解析
     *   数据库：userDao.getAllUser() 一行搞定，直接返回List<User>
     *
     * 这就是数据库的优势：结构化存储，查询方便！
     *
     * @param userDao 用户数据操作类实例
     */
    private fun loadUserList(userDao: UserDao) {
        // 清空旧数据
        userList.clear()

        // 从数据库查询所有用户，加入集合
        userList.addAll(userDao.getAllUser())

        // 刷新列表
        userAdapter.notifyDataSetChanged()
    }
}
