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

/**
 * 登录页面 Activity
 * 功能：输入账号密码登录 + 记住密码（SharedPreferences本地存储）
 *
 * 注意：
 *   - 登录验证改用 SQLite 数据库（UserDao）
 *   - 记住密码仍用 SharedPreferences（因为只存一条键值对，SP更方便）
 */
class LoginActivity : AppCompatActivity() {

    // onCreate：Activity创建时自动调用的方法，整个生命周期只调用一次
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_login)

        // ============================================
        // 自动登录判断（核心功能）
        //
        // 流程：
        //   1. 读取设置中的“自动登录”开关状态
        //   2. 读取保存的用户账号
        //   3. 如果开关打开 && 有保存的账号 → 直接跳主页，不用输入密码
        //   4. 否则正常显示登录页
        // ============================================
        val spSetting = getSharedPreferences("SETTING", MODE_PRIVATE)
        val autoLogin = spSetting.getBoolean("auto_login", false)

        val spUser = getSharedPreferences("USER_DATA", MODE_PRIVATE)
        val savedAccount = spUser.getString("account", "")

        // 如果开启了自动登录，并且有保存的账号，直接跳主页
        if (autoLogin && !savedAccount.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ACCOUNT", savedAccount)
            startActivity(intent)
            finish()
            return  // 直接返回，不执行下面的登录页逻辑
        }

        // ============================================
        // 绑定控件：通过findViewById找到XML中定义的控件
        // R.id.xxx 就是XML中 android:id="@+id/xxx" 的对应
        // ============================================
        val etAccount = findViewById<EditText>(R.id.et_account)       // 账号输入框
        val etPwd = findViewById<EditText>(R.id.et_pwd)               // 密码输入框
        val btnLogin = findViewById<Button>(R.id.btn_login)           // 登录按钮
        val cbRemember = findViewById<CheckBox>(R.id.cb_remember)     // 记住密码复选框
        val loading = findViewById<ProgressBar>(R.id.loading)         // 加载进度条

        // ============================================
        // 打开页面时，自动读取保存的账号密码
        // ============================================

        // getSharedPreferences：获取本地存储工具
        //   第1个参数 "USER_DATA" = 存储文件名（可以自定义）
        //   第2个参数 MODE_PRIVATE = 只有本APP能读写这个数据
        val sp = getSharedPreferences("USER_DATA", MODE_PRIVATE)

        // getBoolean：读取布尔值
        //   第1个参数 "remember" = 键名（保存时用的名字）
        //   第2个参数 false = 默认值（如果没有保存过，就返回false）
        val remember = sp.getBoolean("remember", false)

        // 如果之前勾选了"记住密码"，就自动填写
        if (remember) {
            // getString：读取字符串，第2个参数是默认值
            val savedAccount = sp.getString("account", "")    // 读取保存的账号
            val savedPwd = sp.getString("pwd", "")            // 读取保存的密码

            // setText：把读取到的数据填写到输入框
            etAccount.setText(savedAccount)
            etPwd.setText(savedPwd)

            // isChecked = true：让复选框变成选中状态
            cbRemember.isChecked = true
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

            // 如果勾选了"记住密码"，就保存到本地
            if (cbRemember.isChecked) {
                // sp.edit()：获取编辑器，用于写入数据
                val editor = sp.edit()

                // putString：保存字符串键值对
                //   第1个参数 = 键名（读取时用这个名字找）
                //   第2个参数 = 要保存的值
                editor.putString("account", account)       // 保存账号
                editor.putString("pwd", pwd)               // 保存密码
                editor.putBoolean("remember", true)        // 保存"已记住"标记

                // apply()：提交保存（异步执行，不会卡住界面）
                // 也可以用 commit()，但那是同步的，可能卡顿
                editor.apply()
            } else {
                // 没勾选"记住密码" → 清除之前保存的所有数据
                // .clear()：清除该SharedPreferences中的所有键值对
                // .apply()：提交清除操作
                sp.edit().clear().apply()
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
