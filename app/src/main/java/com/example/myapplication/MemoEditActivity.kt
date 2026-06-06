package com.example.myapplication

// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// EditText：输入框控件
import android.widget.EditText
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
// SimpleDateFormat：日期格式化工具
import java.text.SimpleDateFormat
// Date：日期类，获取当前时间
import java.util.Date
// Locale：地区设置，影响日期格式
import java.util.Locale

/**
 * 备忘录编辑页面 Activity
 *
 * 功能：
 *   1. 新建备忘录：输入内容 → 保存到SharedPreferences
 *   2. 编辑备忘录：加载已有内容 → 修改后保存
 *
 * 数据存储方式：
 *   使用SharedPreferences，文件名 = "MEMO_账号"
 *   key = 备忘录ID（时间戳）
 *   value = "内容|时间"
 *   例如：MEMO_zhangsan 中 -> key="1692000000000" value="今天天气不错|2026-06-06 14:30"
 *
 * Intent传参：
 *   - "MEMO_ACCOUNT"：当前登录账号（必须）
 *   - "MEMO_ID"：备忘录ID（编辑时传入，新建时不传）
 *   - "MEMO_CONTENT"：备忘录原内容（编辑时传入）
 */
class MemoEditActivity : AppCompatActivity() {

    // ============================================
    // onCreate：Activity创建时自动调用
    // ============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_edit)

        // ============================================
        // 绑定控件
        // ============================================
        val etContent = findViewById<EditText>(R.id.et_memo_content)   // 内容输入框
        val btnSave = findViewById<Button>(R.id.btn_save_memo)         // 保存按钮
        val btnBack = findViewById<Button>(R.id.btn_back)             // 返回按钮
        val tvTitle = findViewById<TextView>(R.id.tv_title)           // 标题文字

        // ============================================
        // 接收Intent传来的数据
        // ============================================
        // 账号（必须传，决定存到哪个SP文件）
        val account = intent.getStringExtra("MEMO_ACCOUNT") ?: ""
        // 备忘录ID（编辑时有值，新建时为-1）
        val memoId = intent.getLongExtra("MEMO_ID", -1L)
        // 备忘录原内容（编辑时有值，新建时为空）
        val memoContent = intent.getStringExtra("MEMO_CONTENT") ?: ""

        // ============================================
        // 判断是新建还是编辑
        // memoId != -1L 说明是编辑已有备忘录
        // ============================================
        val isEdit = memoId != -1L

        if (isEdit) {
            // 编辑模式：标题改为"编辑备忘录"，输入框填入原内容
            tvTitle.text = "编辑备忘录"
            etContent.setText(memoContent)
        } else {
            // 新建模式：标题为"新建备忘录"
            tvTitle.text = "新建备忘录"
        }

        // ============================================
        // 保存按钮点击事件
        // ============================================
        btnSave.setOnClickListener {
            // 获取输入内容，去掉前后空格
            val content = etContent.text.toString().trim()

            // 判断内容是否为空
            if (content.isEmpty()) {
                Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --------------------------------------------
            // 保存到SharedPreferences
            // SP文件名："MEMO_账号"（每个账号独立存储）
            // --------------------------------------------
            val sp = getSharedPreferences("MEMO_$account", MODE_PRIVATE)

            // 生成当前时间字符串（格式：2026-06-06 14:30）
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            val timestamp = sdf.format(Date())

            // 确定备忘录ID：
            //   编辑模式 → 用原来的ID（覆盖更新）
            //   新建模式 → 用当前时间戳作为新ID
            val finalId = if (isEdit) memoId else System.currentTimeMillis()

            // 存储格式："内容|时间"
            // 注意：内容中可能包含 | 符号，所以时间放最后
            // 读取时从最后一个 | 切割，避免内容中的 | 被误切
            val saveStr = "$content|$timestamp"

            // 写入SP
            sp.edit()
                .putString(finalId.toString(), saveStr)
                .apply()

            // 提示保存成功
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()

            // 关闭编辑页，回到列表页
            finish()
        }

        // ============================================
        // 返回按钮点击事件
        // ============================================
        btnBack.setOnClickListener {
            finish()
        }
    }
}
