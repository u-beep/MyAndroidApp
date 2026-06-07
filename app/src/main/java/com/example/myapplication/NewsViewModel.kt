package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.bean.NewsItem
import com.example.myapplication.bean.NewsResp
import com.example.myapplication.utils.GsonUtil
import com.example.myapplication.utils.NetUtil

/**
 * 新闻 ViewModel（MVVM入门Demo）
 *
 * 知识点：
 *   - MVVM架构：M(Model数据) V(View页面) VM(ViewModel桥梁)
 *   - ViewModel：独立于Activity/Fragment的生命周期，数据不会因屏幕旋转丢失
 *   - MutableLiveData：可变LiveData，数据变化自动通知观察者更新UI
 *   - 企业主流架构：View不直接请求网络，通过ViewModel中转
 *
 * MVVM数据流：
 *   View(NewsFragment) → 调用ViewModel.loadNews()
 *   → ViewModel调用Model(NetUtil)请求网络
 *   → 数据返回ViewModel → 设置LiveData值
 *   → LiveData自动通知View更新UI
 *
 * 优势：
 *   1. 页面与数据逻辑彻底解耦
 *   2. ViewModel比Activity/Fragment生命周期长，数据不丢失
 *   3. 方便单元测试
 */
class NewsViewModel : ViewModel() {

    // 新闻列表数据（LiveData：数据变化自动通知UI更新）
    val newsData = MutableLiveData<List<NewsItem>>()

    // 错误信息
    val errorMsg = MutableLiveData<String>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    /**
     * 加载新闻数据
     * View调用此方法 → ViewModel负责请求网络 → 数据通过LiveData自动回调View
     */
    fun loadNews(url: String) {
        isLoading.value = true
        NetUtil.get(url, object : NetUtil.NetCallBack {
            override fun onSuccess(json: String?) {
                isLoading.value = false
                if (json.isNullOrEmpty()) {
                    errorMsg.value = "返回数据为空"
                    return
                }
                try {
                    val resp = GsonUtil.jsonToBean(json, NewsResp::class.java)
                    if (resp.code == 200) {
                        newsData.value = resp.data
                    } else {
                        errorMsg.value = "接口返回错误码：${resp.code}"
                    }
                } catch (e: Exception) {
                    errorMsg.value = "JSON解析失败：${e.message}"
                }
            }

            override fun onError(msg: String?) {
                isLoading.value = false
                errorMsg.value = "请求失败：$msg"
            }
        })
    }
}
