package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val tvInfo = findViewById<TextView>(R.id.tv_info)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        // 接收登录页传来的账号
        val account = intent.getStringExtra("USER_ACCOUNT")
        tvWelcome.text = "欢迎 $account !"

        // ============================================
        // 从本地存储读取用户的性别和爱好
        // 存储格式："密码|性别|爱好"
        // 按 | 切割后分别取出
        // ============================================
        val sp = getSharedPreferences("USER_LIST", MODE_PRIVATE)
        val userData = sp.getString(account, "")

        if (!userData.isNullOrEmpty()) {
            // split("\\|")：按 | 切割字符串
            val arr = userData.split("\\|".toRegex())
            val sex = if (arr.size > 1) arr[1] else "未知"
            val hobby = if (arr.size > 2) arr[2] else "无"

            // 显示用户信息
            tvInfo.text = "性别：$sex  爱好：$hobby"
        }

        // 退出登录 → 跳回登录页
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 关闭主页
        }
    }
}
