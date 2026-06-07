package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * 备忘录列表页面（多布局+下拉刷新+上拉加载版）
 *
 * 功能：
 *   1. 使用RecyclerView多布局展示备忘录（普通条目+广告条目+底部加载脚）
 *   2. SwipeRefreshLayout下拉刷新：清空原有数据，重新加载第一页
 *   3. RecyclerView滚动监听上拉加载：滑到底部自动加载下一页
 *   4. 点击右下角浮动按钮新增备忘录
 *   5. 点击列表条目编辑备忘录
 *   6. 长按列表条目删除备忘录（弹窗确认）
 *   7. 页面返回时自动刷新数据
 *
 * 分页规则：
 *   - PAGE_SIZE = 5，每页5条数据
 *   - page = 1 下拉刷新重置page=1，清空列表
 *   - 上拉page++，查询下一页，数据不足5条则hasMore=false
 */
class MemoListActivity : AppCompatActivity() {

    private lateinit var rvMemo: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private val dao = MemoDao(this)
    private lateinit var adapter: MultiMemoAdapter
    private var layoutManager: LinearLayoutManager? = null

    // 分页参数
    private var page = 1
    private val PAGE_SIZE = 5
    private var isLoading = false  // 防止重复上拉加载

    companion object {
        const val KEY_MEMO_ID = "memo_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_list)

        rvMemo = findViewById(R.id.rv_memo)
        fabAdd = findViewById(R.id.fab_add)
        swipeRefresh = findViewById(R.id.swipe_refresh)

        // 初始化适配器和布局管理器
        adapter = MultiMemoAdapter(mutableListOf())
        layoutManager = LinearLayoutManager(this)
        rvMemo.adapter = adapter
        rvMemo.layoutManager = layoutManager

        // ============================================
        // 1. 下拉刷新监听
        // ============================================
        // SwipeRefreshLayout：手指下拉列表顶部触发
        // setOnRefreshListener → 下拉回调
        // isRefreshing = false → 关闭刷新转圈动画
        swipeRefresh.setOnRefreshListener {
            page = 1
            adapter.hasMore = true
            loadData(true)  // true = 下拉刷新
        }

        // ============================================
        // 2. 上拉加载：RecyclerView滚动监听
        //
        // 原理：
        //   onScrollStateChanged → 列表滚动状态变化时触发
        //   SCROLL_STATE_IDLE → 列表停止滚动（静止状态）
        //   findLastVisibleItemPosition() → 最后一个可见条目的位置
        //   如果最后可见位置 == 总条目-1 → 滑到底部了
        // ============================================
        rvMemo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 静止 + 滑到最后一条 + 不在加载中 + 还有更多 + 不在下拉刷新中
                val lastPos = layoutManager!!.findLastVisibleItemPosition()
                val totalCount = adapter.itemCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastPos == totalCount - 1
                    && !isLoading
                    && adapter.hasMore
                    && !swipeRefresh.isRefreshing
                ) {
                    page++
                    loadData(false)  // false = 上拉加载
                }
            }
        })

        // ============================================
        // 3. 新增备忘录按钮
        // ============================================
        fabAdd.setOnClickListener {
            val intent = Intent(this, EditMemoActivity::class.java).apply {
                putExtra(KEY_MEMO_ID, -1L)
            }
            startActivity(intent)
        }

        // ============================================
        // 4. 条目点击：编辑备忘录
        // ============================================
        adapter.itemClick = { memo ->
            val intent = Intent(this, EditMemoActivity::class.java).apply {
                putExtra(KEY_MEMO_ID, memo.id)
            }
            startActivity(intent)
        }

        // ============================================
        // 5. 长按弹窗删除
        // ============================================
        adapter.longClick = { memo ->
            AlertDialog.Builder(this)
                .setTitle("删除提醒")
                .setMessage("确定删除【${memo.title}】？")
                .setPositiveButton("确定") { _, _ ->
                    dao.deleteMemo(memo.id)
                    page = 1
                    loadData(true)
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // 首次加载数据
        loadData(true)
    }

    // 页面回到当前刷新数据（新增/修改后返回自动刷新）
    override fun onResume() {
        super.onResume()
        page = 1
        adapter.hasMore = true
        loadData(true)
    }

    /**
     * 加载数据
     *
     * @param isRefresh true=下拉刷新（替换全量数据），false=上拉加载（追加数据）
     *
     * 分页逻辑：
     *   1. 查询全部数据，手动分页截取
     *   2. startIndex = (page-1) * PAGE_SIZE
     *   3. endIndex = startIndex + PAGE_SIZE
     *   4. 如果数据不足5条 → hasMore = false
     *   5. 随机在第2条插入广告（itemType=1）
     *   6. 模拟1秒网络延迟
     */
    private fun loadData(isRefresh: Boolean) {
        isLoading = true

        // 模拟网络延迟1秒
        Handler(Looper.getMainLooper()).postDelayed({
            // 分页查询全部数据，手动分页截取
            val allData = dao.getAllMemo()
            val startIndex = (page - 1) * PAGE_SIZE
            val endIndex = startIndex + PAGE_SIZE

            var pageData = mutableListOf<Memo>()
            if (startIndex < allData.size) {
                val sub = if (endIndex > allData.size)
                    allData.subList(startIndex, allData.size)
                else
                    allData.subList(startIndex, endIndex)
                pageData = sub.toMutableList()
            }

            // 随机在第2条插入广告itemType=1
            if (pageData.isNotEmpty() && pageData.size >= 2) {
                pageData[1] = pageData[1].copy(itemType = 1)
            }

            // 判断是否还有下一页
            adapter.hasMore = endIndex < allData.size

            if (isRefresh) {
                adapter.refreshAll(pageData)
            } else {
                adapter.addMoreData(pageData)
            }

            // 关闭刷新转圈动画
            swipeRefresh.isRefreshing = false
            isLoading = false
        }, 1000)
    }
}