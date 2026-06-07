package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.bean.NewsItem
import com.example.myapplication.bean.NewsResp
import com.example.myapplication.utils.GsonUtil
import com.example.myapplication.utils.NetUtil

/**
 * 新闻 Fragment（翻页模式：滑到底换页）
 *
 * 流程：
 *   1. 每页固定6条
 *   2. 检测到最后一条可见时，标记"到底了"
 *   3. 用户继续下滑（惯性滚到底）→ 触发下一页请求
 *   4. 数据返回 → 替换 → 回顶
 *   5. 最后一页 → "已经是最后了"
 */
class NewsFragment : Fragment() {

    private val TAG = "FragLife"
    private val NET_TAG = "NetLog"

    private lateinit var rvNews: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var newsList = mutableListOf<NewsItem>()

    private var isLoad = false
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    // 标记是否已经到达底部（避免重复触发）
    private var reachedBottom = false

    override fun onAttach(context: Context) { super.onAttach(context); Log.e(TAG, "NewsFragment → onAttach") }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); Log.e(TAG, "NewsFragment → onCreate") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "NewsFragment → onCreateView")
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        rvNews = view.findViewById(R.id.rv_news)
        adapter = NewsAdapter(newsList)
        rvNews.adapter = adapter
        layoutManager = LinearLayoutManager(context)
        rvNews.layoutManager = layoutManager

        // ViewPager2滑动冲突
        rvNews.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> rvNews.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> rvNews.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        // 条目点击 → 详情页
        adapter.itemClick = { newsItem ->
            val intent = Intent(requireContext(), NewsDetailActivity::class.java)
            intent.putExtra("news_title", newsItem.title)
            intent.putExtra("news_source", newsItem.source)
            intent.putExtra("news_ctime", newsItem.ctime)
            intent.putExtra("news_desc", newsItem.description)
            intent.putExtra("news_pic_url", newsItem.picUrl)
            intent.putExtra("news_url", newsItem.url)
            startActivity(intent)
        }

        // 滑到底 → 请求下一页替换
        rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoading || isLastPage || newsList.isEmpty()) return

                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val lastDataPosition = newsList.size - 1

                // 最后一条数据可见 = 到底了（用findLastVisibleItemPosition，部分可见也算）
                if (lastVisible >= lastDataPosition && !reachedBottom) {
                    reachedBottom = true
                    Log.e(NET_TAG, "滑到底了，加载第${currentPage + 1}页")
                    currentPage++
                    loadNews(currentPage)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 滑动停止后，检查是否一屏就显示完了所有数据（无需滑动的情况）
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !reachedBottom && !isLoading && !isLastPage && newsList.isNotEmpty()) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    if (lastVisible >= newsList.size - 1) {
                        reachedBottom = true
                        Log.e(NET_TAG, "一屏显示完，加载第${currentPage + 1}页")
                        currentPage++
                        loadNews(currentPage)
                    }
                }
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "NewsFragment → onResume")
        if (!isLoad) { loadNews(1); isLoad = true }
    }

    override fun onPause() { super.onPause(); Log.e(TAG, "NewsFragment → onPause") }
    override fun onStop() { super.onStop(); Log.e(TAG, "NewsFragment → onStop") }
    override fun onDestroyView() { super.onDestroyView(); Log.e(TAG, "NewsFragment → onDestroyView") }
    override fun onDestroy() { super.onDestroy(); Log.e(TAG, "NewsFragment → onDestroy") }
    override fun onDetach() { super.onDetach(); Log.e(TAG, "NewsFragment → onDetach") }

    private fun loadNews(page: Int) {
        isLoading = true
        reachedBottom = false  // 重置到底标记
        adapter.setFooterState(adapter.STATE_LOADING)
        val url = ApiConfig.getNewsUrl(page)
        Log.e(NET_TAG, "请求第${page}页")

        NetUtil.get(url, object : NetUtil.NetCallBack {
            override fun onSuccess(json: String?) {
                activity?.runOnUiThread {
                    isLoading = false
                    if (json.isNullOrEmpty()) {
                        adapter.setFooterState(adapter.STATE_MORE)
                        return@runOnUiThread
                    }
                    try {
                        val resp = GsonUtil.jsonToBean(json, NewsResp::class.java)
                        if (resp.code == 200 && resp.newslist.isNotEmpty()) {
                            adapter.refreshData(resp.newslist, page)
                            if (page > 1) rvNews.scrollToPosition(0)

                            if (resp.newslist.size < ApiConfig.NEWS_PAGE_SIZE) {
                                isLastPage = true
                                adapter.setFooterState(adapter.STATE_LAST)
                            }
                            Log.e(NET_TAG, "第${page}页成功（${resp.newslist.size}条）")
                        } else if (resp.code == 200) {
                            isLastPage = true
                            if (page > 1) currentPage--
                            adapter.setFooterState(adapter.STATE_LAST)
                        } else {
                            if (page > 1) currentPage--
                            adapter.setFooterState(adapter.STATE_MORE)
                            Toast.makeText(context, "错误码：${resp.code}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        if (page > 1) currentPage--
                        adapter.setFooterState(adapter.STATE_MORE)
                        Log.e(NET_TAG, "解析失败：${e.message}")
                    }
                }
            }

            override fun onError(msg: String?) {
                Log.e(NET_TAG, "请求失败：$msg")
                activity?.runOnUiThread {
                    isLoading = false
                    reachedBottom = false  // 允许再次触发
                    if (page > 1) currentPage--
                    adapter.setFooterState(adapter.STATE_MORE)
                    Toast.makeText(context, "请求失败，请重试", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun refresh() {
        isLoad = false; isLastPage = false; currentPage = 1; reachedBottom = false
        loadNews(1); isLoad = true
    }

    companion object { fun newInstance(): NewsFragment = NewsFragment() }
}
