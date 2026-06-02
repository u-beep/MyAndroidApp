package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 绑定控件
        val etAccount = findViewById<EditText>(R.id.et_account)
        val etPwd = findViewById<EditText>(R.id.et_pwd)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // 登录点击事件
        btnLogin.setOnClickListener {
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()

            // 简单判断
            if (account == "admin" && pwd == "123456") {
                // 跳转到主页（假设叫 MainActivity）
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 关掉登录页
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
            }
        }
    }
}