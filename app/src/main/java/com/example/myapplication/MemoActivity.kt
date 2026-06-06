package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// AlertDialog：确认删除弹窗
import androidx.appcompat.app.AlertDialog
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
// LinearLayoutManager：RecyclerView的布局管理器
import androidx.recyclerview.widget.LinearLayoutManager
// RecyclerView：列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 备忘录列表页面 Activity
 *
 * 功能：
 *   1. 用RecyclerView展示当前账号的所有备忘录
 *   2. 点击"新建备忘录"跳转到编辑页
 *   3. 点击已有备忘录跳转到编辑页（编辑模式）
 *   4. 点击删除按钮弹出确认弹窗，确认后删除
 *   5. 返回主页
 *
 * 数据来源：
 *   SharedPreferences文件名 = "MEMO_账号"
 *   每条数据：key=ID(时间戳)，value="内容|时间"
 */
class MemoActivity : AppCompatActivity() {

    // 备忘录数据集合
    private val memoList = mutableListOf<MemoItem>()
    // 适配器
    private lateinit var memoAdapter: MemoAdapter
    // 当前登录账号
    private var account: String = ""

    // 请求码：用于标识从编辑页返回
    companion object {
        private const val REQUEST_EDIT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        // ============================================
        // 绑定控件
        // ============================================
        val rvMemoList = findViewById<RecyclerView>(R.id.rv_memo_list)     // 列表容器
        val btnBack = findViewById<Button>(R.id.btn_back)                  // 返回按钮
        val btnAddMemo = findViewById<Button>(R.id.btn_add_memo)           // 新建按钮
        val tvEmpty = findViewById<TextView>(R.id.tv_empty)               // 空数据提示

        // ============================================
        // 接收主页传来的账号
        // 决定读取哪个SP文件中的备忘录
        // ============================================
        account = intent.getStringExtra("MEMO_ACCOUNT") ?: ""

        // ============================================
        // 设置RecyclerView
        // ============================================
        rvMemoList.layoutManager = LinearLayoutManager(this)
        memoAdapter = MemoAdapter(memoList)
        rvMemoList.adapter = memoAdapter

        // ============================================
        // 加载备忘录数据
        // ============================================
        loadMemoList()

        // 根据数据是否为空，显示/隐藏空提示
        updateEmptyView(tvEmpty)

        // ============================================
        // 条目点击事件 → 编辑备忘录
        // ============================================
        memoAdapter.itemClick = { memo ->
            // 跳转到编辑页，传入账号、备忘录ID、原内容
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)              // 账号
            intent.putExtra("MEMO_ID", memo.id)                   // 备忘录ID
            intent.putExtra("MEMO_CONTENT", memo.content)          // 原内容
            startActivityForResult(intent, REQUEST_EDIT)
        }

        // ============================================
        // 删除按钮点击事件 → 弹出确认弹窗
        // ============================================
        memoAdapter.itemDelete = { memo ->
            // AlertDialog：弹出确认对话框，防止误删
            AlertDialog.Builder(this)
                .setTitle("确认删除")                              // 弹窗标题
                .setMessage("确定要删除这条备忘录吗？")            // 弹窗内容
                .setPositiveButton("删除") { _, _ ->              // 确认按钮
                    // 从SP中删除这条数据
                    deleteMemo(memo.id)
                    // 重新加载列表
                    loadMemoList()
                    updateEmptyView(tvEmpty)
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)                   // 取消按钮（不做任何事）
                .show()
        }

        // ============================================
        // 新建备忘录按钮 → 跳转到编辑页（新建模式）
        // ============================================
        btnAddMemo.setOnClickListener {
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)  // 只传账号，不传ID和内容 = 新建模式
            startActivityForResult(intent, REQUEST_EDIT)
        }

        // ============================================
        // 返回主页按钮
        // ============================================
        btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * 从编辑页返回时自动刷新列表
     * onActivityResult：当startActivityForResult打开的页面关闭时自动调用
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 如果是从编辑页返回，刷新列表
        if (requestCode == REQUEST_EDIT) {
            loadMemoList()
            val tvEmpty = findViewById<TextView>(R.id.tv_empty)
            updateEmptyView(tvEmpty)
        }
    }

    /**
     * 从SharedPreferences加载所有备忘录数据
     *
     * 存储格式：SP文件名="MEMO_账号"
     *   key = 备忘录ID（时间戳字符串）
     *   value = "内容|时间"
     *
     * 读取时注意：内容中可能包含 | 符号
     * 所以用 lastIndexOf("|") 从最后一个 | 切割
     * 这样内容中的 | 不会被误切
     */
    private fun loadMemoList() {
        // 清空旧数据
        memoList.clear()

        // 打开该账号专属的SP文件
        val sp = getSharedPreferences("MEMO_$account", MODE_PRIVATE)
        val allEntries = sp.all

        // 遍历所有备忘录条目
        for ((key, value) in allEntries) {
            // value的格式："内容|时间"
            val valueStr = value.toString()

            // 从最后一个 | 切割
            // 为什么用lastIndexOf？因为内容中可能含有 | 符号
            // 例如内容="1|2号方案"，如果用split会切成["1","2号方案","2026-06-06"]
            // 用lastIndexOf保证只切割最后一个 | ，内容部分完整保留
            val lastPipeIndex = valueStr.lastIndexOf("|")

            if (lastPipeIndex > 0) {
                // 切割出内容和时间
                val content = valueStr.substring(0, lastPipeIndex)      // | 前面是内容
                val timestamp = valueStr.substring(lastPipeIndex + 1)   // | 后面是时间

                // 封装成MemoItem对象，加入集合
                // key是ID的字符串形式，toLong()转回数字
                memoList.add(MemoItem(key.toLong(), content, timestamp))
            }
        }

        // ============================================
        // 按时间倒序排列（最新的在最前面）
        // sortedByDescending：按指定字段降序排列
        // id是时间戳，越大越新，所以降序排列
        // ============================================
        val sortedList = memoList.sortedByDescending { it.id }
        memoList.clear()
        memoList.addAll(sortedList)

        // 刷新列表
        memoAdapter.notifyDataSetChanged()
    }

    /**
     * 从SP中删除一条备忘录
     *
     * @param memoId 要删除的备忘录ID
     */
    private fun deleteMemo(memoId: Long) {
        val sp = getSharedPreferences("MEMO_$account", MODE_PRIVATE)
        // remove(key)：删除指定key的键值对
        sp.edit().remove(memoId.toString()).apply()
    }

    /**
     * 根据列表是否为空，显示/隐藏空提示文字
     *
     * @param tvEmpty 空提示TextView
     */
    private fun updateEmptyView(tvEmpty: TextView) {
        if (memoList.isEmpty()) {
            // 列表为空 → 显示提示，隐藏列表
            tvEmpty.visibility = TextView.VISIBLE
        } else {
            // 列表有数据 → 隐藏提示
            tvEmpty.visibility = TextView.GONE
        }
    }
}
