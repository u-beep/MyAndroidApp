package com.example.myapplication

// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// CheckBox：复选框控件（可勾选/取消，多个可同时勾选）
import android.widget.CheckBox
// EditText：输入框控件
import android.widget.EditText
// RadioGroup：单选分组容器
import android.widget.RadioGroup
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity

/**
 * 注册页面 Activity
 * 功能：注册新账号 + 密码确认 + 性别单选 + 爱好多选 + 保存到本地
 *
 * 核心思路：
 *   用 SharedPreferences 存所有注册的用户信息
 *   key = 账号名
 *   value = "密码|性别|爱好"（用 | 拼接，读取时按 | 切割）
 */
class RegisterActivity : AppCompatActivity() {

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_register)

        // ============================================
        // 绑定控件
        // ============================================
        val etAccount = findViewById<EditText>(R.id.et_account)       // 注册账号输入框
        val etPwd = findViewById<EditText>(R.id.et_pwd)               // 密码输入框
        val etPwd2 = findViewById<EditText>(R.id.et_pwd2)             // 确认密码输入框
        val btnRegister = findViewById<Button>(R.id.btn_register)     // 注册按钮
        val rgSex = findViewById<RadioGroup>(R.id.rg_sex)             // 性别单选组
        val cbBall = findViewById<CheckBox>(R.id.cb_ball)             // 爱好：打篮球
        val cbBook = findViewById<CheckBox>(R.id.cb_book)             // 爱好：看书
        val cbGame = findViewById<CheckBox>(R.id.cb_game)             // 爱好：玩游戏

        // ============================================
        // 点击注册按钮
        // ============================================
        btnRegister.setOnClickListener {

            // .text.toString()：获取输入框中的文字并转成字符串
            // .trim()：去掉文字前后的空格
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwd2 = etPwd2.text.toString().trim()

            // --------------------------------------------
            // 第1步：判断不能为空
            // --------------------------------------------
            if (account.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
                Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第2步：判断两次密码一致
            // --------------------------------------------
            if (pwd != pwd2) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第3步：获取性别（RadioGroup单选）
            // checkedRadioButtonId：获取当前选中的RadioButton的id
            // 如果选中的是 rb_woman，就是女，否则默认男
            // --------------------------------------------
            var sex = "男"
            if (rgSex.checkedRadioButtonId == R.id.rb_woman) {
                sex = "女"
            }

            // --------------------------------------------
            // 第4步：获取爱好（CheckBox多选）
            // isChecked：判断复选框是否被勾选
            // 可以同时勾选多个，所以每个都要单独判断
            // --------------------------------------------
            var hobby = ""
            if (cbBall.isChecked) hobby += "篮球 "
            if (cbBook.isChecked) hobby += "看书 "
            if (cbGame.isChecked) hobby += "游戏 "
            if (hobby.isEmpty()) hobby = "无"

            // --------------------------------------------
            // 第5步：判断账号是否已存在
            // --------------------------------------------
            val sp = getSharedPreferences("USER_LIST", MODE_PRIVATE)
            if (sp.contains(account)) {
                Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第6步：保存用户信息（核心！）
            // 格式："密码|性别|爱好"，用 | 拼接
            // 读取时按 | 切割就能分别拿到密码、性别、爱好
            // 例如："123456|男|篮球 看书"
            // --------------------------------------------
            val saveStr = "$pwd|$sex|$hobby"
            val editor = sp.edit()
            editor.putString(account, saveStr)      // 把"账号→密码|性别|爱好"存进去
            editor.apply()                          // apply()：异步保存，不会卡界面

            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()

            // --------------------------------------------
            // 第7步：跳回登录页
            // finish()：关闭当前注册页，自动回到登录页
            // --------------------------------------------
            finish()
        }
    }
}
