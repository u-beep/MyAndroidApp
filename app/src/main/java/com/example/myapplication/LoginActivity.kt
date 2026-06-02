package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 绑定控件
        val etAccount = findViewById<EditText>(R.id.et_account)
        val etPwd = findViewById<EditText>(R.id.et_pwd)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // 登录点击事件
        btnLogin.setOnClickListener {
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()

            // 判断账号是否为空
            if (account.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 判断密码是否为空
            if (pwd.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 判断账号密码是否正确
            if (account == "admin" && pwd == "123456") {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

                // 跳转 + 传账号到主页
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ACCOUNT", account)
                startActivity(intent)
                finish() // 关掉登录页
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
