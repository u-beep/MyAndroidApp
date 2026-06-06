package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 新增/编辑备忘录页面
 * 
 * 功能：
 *   1. 新增备忘录：标题和内容为空
 *   2. 编辑备忘录：从intent获取memo_id，查询并回填数据
 *   3. 保存按钮：根据是否有id判断是新增还是编辑
 *   4. 保存成功后自动返回列表页
 */
class EditMemoActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button
    private val dao = MemoDao(this)
    private var editId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)
        
        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
        btnSave = findViewById(R.id.btn_save)

        // 获取传递的备忘录ID，-1代表新增
        editId = intent.getLongExtra(MemoListActivity.KEY_MEMO_ID, -1L)
        
        // 编辑模式：回填旧数据
        if (editId != -1L) {
            val memo = dao.getMemoById(editId)
            memo?.let {
                etTitle.setText(it.title)
                etContent.setText(it.content)
            }
        }

        // 保存按钮点击事件
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            
            if (title.isEmpty()) {
                Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 获取当前时间
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
            val nowTime = sdf.format(Date())

            if (editId == -1L) {
                // 新增备忘录
                val newMemo = Memo(title = title, content = content, createTime = nowTime)
                dao.addMemo(newMemo)
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show()
            } else {
                // 修改备忘录
                val updateMemo = Memo(id = editId, title = title, content = content, createTime = "")
                dao.updateMemo(updateMemo)
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
            }
            
            finish() // 关闭页面，返回列表页自动刷新
        }
    }
}