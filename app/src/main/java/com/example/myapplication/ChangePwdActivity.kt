package com.example.myapplication

// Intent：用于页面跳转
import android.content.Intent
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
 * 修改密码页面 Activity
 * 功能：验证旧密码 → 确认新密码 → 更新数据库 → 强制重新登录
 *
 * 核心原理：
 *   1. 从 Intent 拿到当前登录的账号名
 *   2. 用 UserDao 从数据库查询该用户的密码
 *   3. 对比用户输入的旧密码对不对
 *   4. 对的话，用 UserDao 更新密码
 *   5. 清除"记住密码"缓存，强制重新登录
 */
class ChangePwdActivity : AppCompatActivity() {

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_change_pwd)

        // ============================================
        // 获取从登录页传过来的【当前登录账号】
        // 这是修改密码的关键！没有账号名就不知道改谁的密码
        // ============================================
        val currentAccount = intent.getStringExtra("LOGIN_ACCOUNT")

        // ============================================
        // 绑定控件
        // ============================================
        val etOldPwd = findViewById<EditText>(R.id.et_old_pwd)       // 旧密码输入框
        val etNewPwd = findViewById<EditText>(R.id.et_new_pwd)       // 新密码输入框
        val etNewPwd2 = findViewById<EditText>(R.id.et_new_pwd2)     // 确认新密码输入框
        val btnConfirm = findViewById<Button>(R.id.btn_confirm)      // 确认修改按钮

        // ============================================
        // 点击确认修改按钮
        // ============================================
        btnConfirm.setOnClickListener {

            val oldPwd = etOldPwd.text.toString().trim()
            val newPwd = etNewPwd.text.toString().trim()
            val newPwd2 = etNewPwd2.text.toString().trim()

            // --------------------------------------------
            // 第1步：判断输入是否为空
            // --------------------------------------------
            if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwd2.isEmpty()) {
                Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第2步：判断两次新密码是否一致
            // --------------------------------------------
            if (newPwd != newPwd2) {
                Toast.makeText(this, "两次新密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第3步：验证旧密码是否正确（核心！安全关键）
            // 改用数据库查询：userDao.login() 返回该账号的密码
            // 比以前SP的"密码|性别|爱好"切割方便多了！
            // --------------------------------------------
            val userDao = UserDao(this)
            val realOldPwd = userDao.login(currentAccount ?: "")

            if (realOldPwd == null) {
                Toast.makeText(this, "账号异常！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (oldPwd != realOldPwd) {
                Toast.makeText(this, "旧密码错误！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第4步：保存新密码（核心！）
            // userDao.updatePwd(账号, 新密码)：只更新密码列
            // 其他列（性别、爱好）不受影响，不需要重新拼接字符串了！
            // 对比SP：以前要手动拼接"新密码|性别|爱好"再覆盖，麻烦且容易出错
            // --------------------------------------------
            val rows = userDao.updatePwd(currentAccount ?: "", newPwd)

            if (rows > 0) {
                // 更新成功
                // --------------------------------------------
                // 第5步：清除"记住密码"缓存（重要！）
                // 因为密码改了，登录页自动填写的旧密码已经失效
                // --------------------------------------------
                getSharedPreferences("USER_DATA", MODE_PRIVATE).edit().clear().apply()

                Toast.makeText(this, "密码修改成功！请重新登录", Toast.LENGTH_SHORT).show()

                // --------------------------------------------
                // 第6步：跳回登录页，强制重新登录
                // --------------------------------------------
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
