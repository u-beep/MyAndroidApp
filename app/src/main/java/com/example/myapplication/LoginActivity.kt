package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.SPUtil

/**
 * 登录页面 Activity
 *
 * 功能：
 *   - 输入账号密码登录 + 记住密码（SharedPreferences本地存储）
 *   - 登录验证使用 SQLite 数据库（UserDao）
 *   - 记住密码和自动登录使用统一的SP工具类
 *   - 登录成功保存User到MyApp全局变量
 *
 * 生命周期日志：演示Activity完整7大生命周期
 */
class LoginActivity : AppCompatActivity() {

    private val TAG = "life"

    // SP常量key（统一命名，防止写错）
    companion object {
        const val KEY_ACCOUNT = "save_account"
        const val KEY_PWD = "save_pwd"
        const val KEY_REMEMBER = "is_remember"
        const val KEY_AUTO = "is_auto_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "LoginActivity → onCreate")

        // 绑定控件
        val etAccount = findViewById<EditText>(R.id.et_account)
        val etPwd = findViewById<EditText>(R.id.et_pwd)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val cbRemember = findViewById<CheckBox>(R.id.cb_remember)
        val cbAuto = findViewById<CheckBox>(R.id.cb_auto)
        val loading = findViewById<ProgressBar>(R.id.loading)

        // ============================================
        // 页面初始化：读取SP，回填账号密码、勾选状态
        // ============================================
        val isRemember = SPUtil.getBoolean(KEY_REMEMBER)
        val isAuto = SPUtil.getBoolean(KEY_AUTO)
        cbRemember.isChecked = isRemember
        cbAuto.isChecked = isAuto

        if (isRemember) {
            etAccount.setText(SPUtil.getString(KEY_ACCOUNT))
            etPwd.setText(SPUtil.getString(KEY_PWD))
        }

        // 自动登录开启且记住密码→直接跳转主页
        if (isAuto && isRemember) {
            val savedAccount = SPUtil.getString(KEY_ACCOUNT)
            if (savedAccount.isNotEmpty()) {
                // ========== 方式5：Application全局变量 ==========
                // 自动登录时也需要恢复全局用户信息
                val userDao = UserDao(this)
                val user = userDao.getUserByAccount(savedAccount)
                val myApp = application as MyApp
                myApp.loginUser = user

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ACCOUNT", savedAccount)
                startActivity(intent)
                finish()
                return
            }
        }

        // ============================================
        // 登录按钮点击事件
        // ============================================
        btnLogin.setOnClickListener {
            loading.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()

            if (account.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            if (pwd.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            // SQLite校验账号密码
            val userDao = UserDao(this)
            val realPwd = userDao.login(account)

            if (realPwd == null) {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            if (realPwd != pwd) {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnLogin.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 登录成功！
            // --------------------------------------------
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

            // 保存勾选状态+账号密码
            val remember = cbRemember.isChecked
            val auto = cbAuto.isChecked
            SPUtil.putBoolean(KEY_REMEMBER, remember)
            SPUtil.putBoolean(KEY_AUTO, auto)

            if (remember) {
                SPUtil.putString(KEY_ACCOUNT, account)
                SPUtil.putString(KEY_PWD, pwd)
            } else {
                SPUtil.remove(KEY_ACCOUNT)
                SPUtil.remove(KEY_PWD)
                SPUtil.putBoolean(KEY_AUTO, false)
            }

            // ========== 方式5：Application全局变量 ==========
            // 登录成功后保存User到MyApp全局，全APP任意页面获取
            val user = userDao.getUserByAccount(account)
            val myApp = application as MyApp
            myApp.loginUser = user

            // 跳转主页
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ACCOUNT", account)
            startActivity(intent)
            finish()
        }

        // 去注册
        findViewById<Button>(R.id.btn_to_register).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 修改密码
        findViewById<Button>(R.id.btn_to_change_pwd).setOnClickListener {
            val account = etAccount.text.toString().trim()
            if (account.isEmpty()) {
                Toast.makeText(this, "请先输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userDao = UserDao(this)
            if (userDao.login(account) == null) {
                Toast.makeText(this, "该账号未注册", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ChangePwdActivity::class.java)
            intent.putExtra("LOGIN_ACCOUNT", account)
            startActivity(intent)
        }
    }

    // ========== Activity完整7大生命周期 ==========
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginActivity → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "LoginActivity → onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "LoginActivity → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "LoginActivity → onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "LoginActivity → onDestroy")
    }
}