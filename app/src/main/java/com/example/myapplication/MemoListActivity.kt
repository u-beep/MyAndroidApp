package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * 备忘录列表页面
 * 
 * 功能：
 *   1. 使用RecyclerView展示所有备忘录
 *   2. 点击右下角浮动按钮新增备忘录
 *   3. 点击列表条目编辑备忘录
 *   4. 长按列表条目删除备忘录（弹窗确认）
 *   5. 页面返回时自动刷新数据
 */
class MemoListActivity : AppCompatActivity() {
    private lateinit var rvMemo: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private val dao = MemoDao(this)
    private lateinit var adapter: MemoSQLAdapter

    companion object {
        const val KEY_MEMO_ID = "memo_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_list)
        
        rvMemo = findViewById(R.id.rv_memo)
        fabAdd = findViewById(R.id.fab_add)

        // 初始化适配器
        adapter = MemoSQLAdapter(mutableListOf())
        rvMemo.adapter = adapter
        rvMemo.layoutManager = LinearLayoutManager(this)

        // 新增按钮点击事件
        fabAdd.setOnClickListener {
            // id=-1代表新增
            val intent = Intent(this, EditMemoActivity::class.java).apply {
                putExtra(KEY_MEMO_ID, -1L)
            }
            startActivity(intent)
        }

        // 条目点击：编辑备忘录
        adapter.itemClick = { memo ->
            val intent = Intent(this, EditMemoActivity::class.java).apply {
                putExtra(KEY_MEMO_ID, memo.id)
            }
            startActivity(intent)
        }

        // 长按弹窗删除
        adapter.longClick = { memo ->
            AlertDialog.Builder(this)
                .setTitle("删除提醒")
                .setMessage("确定删除【${memo.title}】？")
                .setPositiveButton("确定") { _, _ ->
                    dao.deleteMemo(memo.id)
                    loadData()
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    // 页面回到当前刷新数据（新增/修改后返回自动刷新）
    override fun onResume() {
        super.onResume()
        loadData()
    }

    // 加载全部备忘录数据
    private fun loadData() {
        val all = dao.getAllMemo()
        adapter.refresh(all)
    }
}