package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
 * 新闻 Fragment（第30天：OkHttp联网 + Gson解析JSON）
 *
 * 功能：
 *   1. 通过OkHttp请求网络新闻数据
 *   2. 通过Gson解析JSON为Kotlin对象
 *   3. RecyclerView展示新闻列表
 *   4. 懒加载：页面第一次可见才请求网络
 *
 * 知识点：
 *   - OkHttp异步GET请求：NetUtil.get(url, callback)
 *   - Gson解析JSON：GsonUtil.jsonToBean(json, Class)
 *   - 子线程不能更新UI，必须 runOnUiThread 切换到主线程
 *   - Fragment完整7大生命周期
 *   - 懒加载优化
 */
class NewsFragment : Fragment() {

    private val TAG = "FragLife"

    private lateinit var rvNews: RecyclerView
    private lateinit var adapter: NewsAdapter
    private var newsList = mutableListOf<NewsItem>()

    // ========== 懒加载标志位 ==========
    private var isLoad = false

    // ========== 免费测试新闻接口 ==========
    // 使用天行数据免费API（需替换为你自己的key）
    // 注册地址：https://www.tianapi.com/
    // 也可以使用其他免费新闻API
    private val newsUrl = "http://api.tianapi.com/generalnews/index?key=YOUR_KEY&num=20"

    // ========== Fragment完整7大生命周期 ==========
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "NewsFragment → onAttach（绑定宿主Activity）")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "NewsFragment → onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "NewsFragment → onCreateView（加载布局）")

        val view = inflater.inflate(R.layout.fragment_news, container, false)

        // 设置RecyclerView
        rvNews = view.findViewById(R.id.rv_news)
        adapter = NewsAdapter(newsList)
        rvNews.adapter = adapter
        rvNews.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "NewsFragment → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "NewsFragment → onResume（页面可见可交互）")

        // ========== 懒加载：只在页面第一次可见时请求网络 ==========
        if (!isLoad) {
            loadNews()
            isLoad = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "NewsFragment → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "NewsFragment → onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "NewsFragment → onDestroyView（视图销毁，实例仍在）")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "NewsFragment → onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "NewsFragment → onDetach（与Activity解绑）")
    }

    /**
     * 网络请求新闻数据（OkHttp + Gson核心流程）
     *
     * 流程：
     *   1. NetUtil.get() 发送异步GET请求
     *   2. 成功回调拿到JSON字符串
     *   3. GsonUtil.jsonToBean() 解析JSON为NewsResp对象
     *   4. runOnUiThread 切换到主线程更新UI
     *   5. adapter.refreshData() 刷新列表
     */
    private fun loadNews() {
        Log.e(TAG, "NewsFragment → loadNews（首次请求网络数据）")

        NetUtil.get(newsUrl, object : NetUtil.NetCallBack {
            override fun onSuccess(json: String?) {
                // ⚠️ 注意：此回调在子线程执行，不能直接更新UI！
                // 必须用 runOnUiThread 切换到主线程
                activity?.runOnUiThread {
                    if (json.isNullOrEmpty()) return@runOnUiThread
                    try {
                        val resp = GsonUtil.jsonToBean(json, NewsResp::class.java)
                        if (resp.code == 200) {
                            adapter.refreshData(resp.data)
                        } else {
                            Toast.makeText(context, "接口返回错误码：${resp.code}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "JSON解析失败：${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(msg: String?) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "请求失败：$msg", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "NewsFragment → 网络请求失败：$msg")
                }
            }
        })
    }

    /**
     * 提供给外部手动刷新的方法（如下拉刷新）
     */
    fun refresh() {
        isLoad = false
        loadNews()
        isLoad = true
    }

    companion object {
        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }
}
