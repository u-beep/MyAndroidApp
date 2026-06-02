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
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        // 接收登录页传来的账号
        val account = intent.getStringExtra("USER_ACCOUNT")
        tvWelcome.text = "欢迎 $account !"

        // 退出登录 → 跳回登录页
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 关闭主页
        }
    }
}
