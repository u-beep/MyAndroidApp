package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// CheckBox：复选框控件（可勾选/取消）
import android.widget.CheckBox
// EditText：输入框控件
import android.widget.EditText
// ProgressBar：加载进度条控件
import android.widget.ProgressBar
// Toast：弹出的短暂提示消息
import android.widget.Toast
// View：控件基类，用于控制显示/隐藏
import android.view.View
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.SPUtil

/**
 * 登录页面 Activity
 * 功能：输入账号密码登录 + 记住密码（SharedPreferences本地存储）
 *
 * 注意：
 *   - 登录验证改用 SQLite 数据库（UserDao）
 *   - 记住密码和自动登录使用统一的SP工具类
 */
class LoginActivity : AppCompatActivity() {

    // SP常量key（统一命名，防止写错）
    companion object {
        const val KEY_ACCOUNT = "save_account"
        const val KEY_PWD = "save_pwd"
        const val KEY_REMEMBER = "is_remember"
        const val KEY_AUTO = "is_auto_login"
    }

    // onCreate：Activity创建时自动调用的方法，整个生命周期只调用一次
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_login)

        // 绑定控件
        val etAccount = findViewById<EditText>(R.id.et_account)       // 账号输入框
        val etPwd = findViewById<EditText>(R.id.et_pwd)               // 密码输入框
        val btnLogin = findViewById<Button>(R.id.btn_login)           // 登录按钮
        val cbRemember = findViewById<CheckBox>(R.id.cb_remember)     // 记住密码复选框
        val cbAuto = findViewById<CheckBox>(R.id.cb_auto)             // 自动登录复选框
        val loading = findViewById<ProgressBar>(R.id.loading)         // 加载进度条

        // ============================================
        // 页面初始化：读取SP，回填账号密码、勾选状态
        // ============================================
        val isRemember = SPUtil.getBoolean(KEY_REMEMBER)
        val isAuto = SPUtil.getBoolean(KEY_AUTO)
        cbRemember.isChecked = isRemember
        cbAuto.isChecked = isAuto

        // 记住密码开启→填充账号密码
        if (isRemember) {
            etAccount.setText(SPUtil.getString(KEY_ACCOUNT))
            etPwd.setText(SPUtil.getString(KEY_PWD))
        }

        // 自动登录开启且记住密码→直接跳转主页
        if (isAuto && isRemember) {
            val savedAccount = SPUtil.getString(KEY_ACCOUNT)
            if (savedAccount.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ACCOUNT", savedAccount)
                startActivity(intent)
                finish()
                return  // 直接返回，不执行下面的登录页逻辑
            }
        }

        // ============================================
        // 登录按钮点击事件
        // ============================================
        btnLogin.setOnClickListener {

            // ============================================
            // 第一步：显示加载动画 + 禁止重复点击
            // ============================================
            loading.visibility = View.VISIBLE     // 显示转圈动画
            btnLogin.isEnabled = false            // 禁止重复点击按钮

            // .text.toString()：获取输入框中的文字并转成字符串
            // .trim()：去掉文字前后的空格（防止用户多打了空格）
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()

            // --------------------------------------------
            // 判断账号是否为空
            // --------------------------------------------
            if (account.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                // 隐藏加载 + 恢复按钮
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 判断密码是否为空
            // --------------------------------------------
            if (pwd.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 判断账号密码是否正确
            // 改用数据库查询（UserDao）
            // userDao.login(account)：根据账号查密码
            //   返回null = 账号不存在
            //   返回密码字符串 = 查到了，再和用户输入的对比
            // --------------------------------------------
            val userDao = UserDao(this)
            val realPwd = userDao.login(account)

            if (realPwd == null) {
                // 账号不存在
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            if (realPwd != pwd) {
                // 密码不对
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 登录成功！
            // --------------------------------------------
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

            // 登录成功：保存勾选状态+账号密码
            val remember = cbRemember.isChecked
            val auto = cbAuto.isChecked
            SPUtil.putBoolean(KEY_REMEMBER, remember)
            SPUtil.putBoolean(KEY_AUTO, auto)
            
            // 勾选记住密码才存储账号密码，否则清空
            if (remember) {
                SPUtil.putString(KEY_ACCOUNT, account)
                SPUtil.putString(KEY_PWD, pwd)
            } else {
                SPUtil.remove(KEY_ACCOUNT)
                SPUtil.remove(KEY_PWD)
                // 如果取消记住密码，也取消自动登录
                SPUtil.putBoolean(KEY_AUTO, false)
            }

            // --------------------------------------------
            // 页面跳转：从登录页跳到主页
            // --------------------------------------------
            // Intent(this, MainActivity::class.java)：
            //   第1个参数 this = 从哪个页面跳（当前Activity）
            //   第2个参数 MainActivity::class.java = 跳到哪个页面
            val intent = Intent(this, MainActivity::class.java)

            // putExtra：跳转时携带数据
            //   "USER_ACCOUNT" = 数据的键名
            //   account = 要传递的值（用户输入的账号）
            intent.putExtra("USER_ACCOUNT", account)

            // startActivity(intent)：执行跳转
            startActivity(intent)

            // finish()：关闭当前页面（登录页）
            // 这样用户按返回键就不会回到登录页了
            finish()
        }

        // ============================================
        // "去注册"按钮点击事件
        // ============================================
        findViewById<Button>(R.id.btn_to_register).setOnClickListener {
            // Intent(this, RegisterActivity::class.java)：
            //   从登录页跳到注册页
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // ============================================
        // "修改密码"按钮点击事件
        // ============================================
        findViewById<Button>(R.id.btn_to_change_pwd).setOnClickListener {
            // 先获取用户输入的账号
            val account = etAccount.text.toString().trim()

            // 必须先输入账号，才能修改密码
            // 因为修改密码需要知道"改谁的密码"
            if (account.isEmpty()) {
                Toast.makeText(this, "请先输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 检查这个账号是否已经注册过
            // 没注册的账号不能修改密码
            // 改用数据库查询：login()返回null说明账号不存在
            val userDao = UserDao(this)
            if (userDao.login(account) == null) {
                Toast.makeText(this, "该账号未注册", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 跳转到修改密码页面，把账号带过去
            val intent = Intent(this, ChangePwdActivity::class.java)
            intent.putExtra("LOGIN_ACCOUNT", account)    // 传账号
            startActivity(intent)
        }
    }
}