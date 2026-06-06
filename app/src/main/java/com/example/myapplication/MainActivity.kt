package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// SharedPreferences：本地轻量数据存储工具
import android.content.SharedPreferences
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
 *   2. 显示当前用户的性别和爱好
 *   3. 用RecyclerView列表展示所有注册用户信息
 *   4. 退出登录功能
 *
 * 数据来源：
 *   - 当前用户信息：从Intent获取账号 → 从USER_LIST读取
 *   - 所有用户列表：从USER_LIST的SharedPreferences读取全部key
 */
class MainActivity : AppCompatActivity() {

    // ============================================
    // 定义集合存放所有用户数据
    // MutableList<User>：可变列表，可以增删元素
    // 为什么不用List？因为后续可能需要刷新、清空、添加数据
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
        // intent.getStringExtra("键名")：获取跳转时携带的字符串数据
        // ============================================
        val account = intent.getStringExtra("USER_ACCOUNT") ?: ""
        tvWelcome.text = "欢迎 $account !"

        // ============================================
        // 从本地存储读取当前用户的性别和爱好
        // 存储格式："密码|性别|爱好"
        // 按 | 切割后分别取出
        // ============================================
        val sp = getSharedPreferences("USER_LIST", MODE_PRIVATE)
        val userData = sp.getString(account, "")

        if (!userData.isNullOrEmpty()) {
            // split("\\|")：按 | 切割字符串（|是正则特殊符号，要转义）
            val arr = userData.split("\\|".toRegex())
            val sex = if (arr.size > 1) arr[1] else "未知"
            val hobby = if (arr.size > 2) arr[2] else "无"

            // 显示用户信息
            tvInfo.text = "性别：$sex  爱好：$hobby"
        }

        // ============================================
        // 第1步：设置RecyclerView的布局管理器
        // LinearLayoutManager：线性布局（一条一条竖着排或横着排）
        // 默认是竖直方向，就是常见的上下滚动列表
        // ============================================
        rvUserList.layoutManager = LinearLayoutManager(this)

        // ============================================
        // 第2步：创建适配器并绑定
        // 适配器 = 数据和列表之间的桥梁
        // 把userList传给适配器，适配器才知道要显示什么数据
        // ============================================
        userAdapter = UserAdapter(userList)
        rvUserList.adapter = userAdapter

        // ============================================
        // 第3步：从SharedPreferences读取全部注册用户
        // sp.all：获取该SharedPreferences中的所有键值对
        // .keys：取出所有key（key就是注册的账号名）
        // ============================================
        loadUserList(sp)

        // ============================================
        // 第4步：设置条目点击事件
        // 点击某一条用户，弹出该用户的账号提示
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
            finish() // 关闭主页
        }

        // ============================================
        // 备忘录按钮 → 跳转到备忘录页面
        // 必须把当前账号传过去，这样备忘录才能按账号独立存储
        // ============================================
        val btnMemo = findViewById<Button>(R.id.btn_memo)   // 备忘录按钮
        btnMemo.setOnClickListener {
            // Intent(this, MemoActivity::class.java)：
            //   从主页跳到备忘录页面
            val intent = Intent(this, MemoActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)   // 传账号，备忘录按账号独立
            startActivity(intent)
        }
    }

    /**
     * 从SharedPreferences读取所有注册用户，装载进列表
     *
     * 读取逻辑：
     *   1. sp.all.keys 获取所有注册账号（key）
     *   2. 遍历每个账号，读取其值（格式："密码|性别|爱好"）
     *   3. 按 | 切割，拆分成密码、性别、爱好
     *   4. 封装成User对象，加入集合
     *   5. 调用notifyDataSetChanged()刷新列表
     *
     * @param sp USER_LIST的SharedPreferences实例
     */
    private fun loadUserList(sp: SharedPreferences) {
        // 清空旧数据（防止重复添加）
        userList.clear()

        // sp.all：获取所有键值对（Map类型）
        // .keys：只要所有的key（即所有注册账号）
        val allKeys = sp.all.keys

        // 遍历每个账号，读取并拆分数据
        for (key in allKeys) {
            // 读取该账号对应的值："密码|性别|爱好"
            val infoStr = sp.getString(key, "") ?: continue  // 为空则跳过

            // 按 | 切割字符串
            // "\\|" 是正则表达式，| 是特殊符号需要转义
            val arr = infoStr.split("\\|".toRegex())

            // 安全取值：如果切割后数组够长就取对应位，否则给默认值
            val pwd = if (arr.isNotEmpty()) arr[0] else ""
            val sex = if (arr.size > 1) arr[1] else "未知"
            val hobby = if (arr.size > 2) arr[2] else "无"

            // 封装成User对象，加入集合
            // User(account, pwd, sex, hobby) → 数据类的构造函数
            userList.add(User(key, pwd, sex, hobby))
        }

        // ============================================
        // notifyDataSetChanged()：通知列表"数据变了，重新显示！"
        // 调用后，适配器会重新执行onBindViewHolder，刷新所有条目
        // 不调用这个方法，列表界面不会更新！
        // ============================================
        userAdapter.notifyDataSetChanged()
    }
}
