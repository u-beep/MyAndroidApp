package com.example.myapplication

// Intent：用于页面跳转
import android.content.Intent
// SharedPreferences：本地轻量级存储（键值对）
import android.content.SharedPreferences
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// Switch：开关控件（像手机设置里的开关）
import android.widget.Switch
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity

/**
 * 设置页面 Activity
 *
 * 功能：
 *   1. 自动登录开关（Switch控件）
 *   2. 用 SharedPreferences 保存开关状态（永久保存）
 *   3. 退出登录（清除用户信息，回到登录页）
 *
 * 知识点：
 *   - Switch = 开关控件，isChecked 属性获取/设置开关状态
 *   - setOnCheckedChangeListener = 开关状态改变时的监听器
 *   - SP 保存开关状态 → 下次打开页面时恢复开关状态
 */
class SettingActivity : AppCompatActivity() {

    // SharedPreferences 引用，用于保存设置
    lateinit var sp: SharedPreferences

    // Switch 开关控件引用
    lateinit var switchAuto: Switch

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // ============================================
        // 绑定控件
        // ============================================
        switchAuto = findViewById(R.id.switch_auto)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        val btnBack = findViewById<Button>(R.id.btn_back)

        // ============================================
        // 返回按钮 → 关闭当前页面，回到主页
        // finish()：关闭当前Activity，自动回到上一个页面
        // ============================================
        btnBack.setOnClickListener {
            finish()
        }

        // ============================================
        // 获取 SharedPreferences
        // "SETTING" = 存储文件名（和登录页的"USER_DATA"分开）
        // MODE_PRIVATE = 只有本APP能读写
        // ============================================
        sp = getSharedPreferences("SETTING", MODE_PRIVATE)

        // ============================================
        // 读取保存的开关状态，恢复开关显示
        //
        // getBoolean("auto_login", false)：
        //   第1个参数 = 键名（保存时用的名字）
        //   第2个参数 = 默认值（如果没保存过，返回false）
        // ============================================
        switchAuto.isChecked = sp.getBoolean("auto_login", false)

        // ============================================
        // 开关状态改变时的监听
        //
        // setOnCheckedChangeListener：开关状态改变时自动触发
        //   button = 触发事件的Switch控件
        //   isChecked = 新的开关状态（true=开，false=关）
        // ============================================
        switchAuto.setOnCheckedChangeListener { button, isChecked ->
            // 把开关状态保存到 SharedPreferences
            // putBoolean("auto_login", isChecked)：保存键值对
            // apply()：异步提交（不会卡住界面）
            sp.edit().putBoolean("auto_login", isChecked).apply()

            // 根据开关状态显示不同的提示
            if (isChecked) {
                Toast.makeText(this, "已开启自动登录", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "已关闭自动登录", Toast.LENGTH_SHORT).show()
            }
        }

        // ============================================
        // 退出登录按钮
        //
        // 流程：
        //   1. 清除用户数据（账号、密码、记住密码标记）
        //   2. 清除自动登录开关（否则退出后还会自动登录！）
        //   3. 跳转回登录页
        //   4. 关闭设置页
        // ============================================
        btnLogout.setOnClickListener {
            // 清除用户登录信息
            getSharedPreferences("USER_DATA", MODE_PRIVATE).edit().clear().apply()
            // 同时关闭自动登录（否则退出后还会自动登录）
            getSharedPreferences("SETTING", MODE_PRIVATE).edit().putBoolean("auto_login", false).apply()

            Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show()

            // 跳转到登录页
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
