package com.example.myapplication

// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// EditText：输入框控件
import android.widget.EditText
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity

/**
 * 注册页面 Activity
 * 功能：注册新账号 + 密码确认 + 账号查重 + 保存到本地
 *
 * 核心思路：
 *   用 SharedPreferences 存所有注册的账号密码
 *   key = 账号名，value = 密码
 *   这样一个 sp 文件就能存无数个用户！
 */
class RegisterActivity : AppCompatActivity() {

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_register)

        // ============================================
        // 绑定控件：通过findViewById找到XML中定义的控件
        // ============================================
        val etAccount = findViewById<EditText>(R.id.et_account)       // 注册账号输入框
        val etPwd = findViewById<EditText>(R.id.et_pwd)               // 密码输入框
        val etPwd2 = findViewById<EditText>(R.id.et_pwd2)             // 确认密码输入框
        val btnRegister = findViewById<Button>(R.id.btn_register)     // 注册按钮

        // ============================================
        // 点击注册按钮
        // ============================================
        btnRegister.setOnClickListener {

            // .text.toString()：获取输入框中的文字并转成字符串
            // .trim()：去掉文字前后的空格（防止用户多打了空格）
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwd2 = etPwd2.text.toString().trim()

            // --------------------------------------------
            // 第1步：判断不能为空
            // isEmpty()：字符串长度为0就是空
            // ||：或者（三个只要有一个为空就不行）
            // --------------------------------------------
            if (account.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
                Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show()
                // return@setOnClickListener：提前结束点击事件，不往下执行
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第2步：判断两次密码一致
            // !=：不等于，如果两次输入的密码不一样
            // --------------------------------------------
            if (pwd != pwd2) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第3步：判断账号是否已存在
            // getSharedPreferences：获取本地存储工具
            //   "USER_LIST" = 专门存所有注册账号的文件名
            //   MODE_PRIVATE = 只有本APP能读写
            // sp.contains(account)：检查这个账号名是否已经保存过
            // --------------------------------------------
            val sp = getSharedPreferences("USER_LIST", MODE_PRIVATE)
            if (sp.contains(account)) {
                Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第4步：保存账号密码（核心！）
            // 账号当key，密码当value
            // 这样每个账号名对应一个密码，查起来也方便
            // --------------------------------------------
            val editor = sp.edit()
            editor.putString(account, pwd)      // 把"账号→密码"存进去
            editor.apply()                      // apply()：异步保存，不会卡界面

            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()

            // --------------------------------------------
            // 第5步：跳回登录页
            // finish()：关闭当前注册页，自动回到登录页
            // 不需要用Intent跳转，因为登录页还在后台等着
            // --------------------------------------------
            finish()
        }
    }
}
