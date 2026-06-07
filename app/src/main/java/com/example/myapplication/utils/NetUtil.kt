package com.example.myapplication.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp网络请求工具类（全局统一请求）
 *
 * 知识点：
 *   - OkHttp：Android最流行的网络请求库
 *   - OkHttpClient：网络请求客户端（单例复用）
 *   - Request.Builder：构建请求对象（URL、请求方法、请求头）
 *   - enqueue：异步请求（子线程执行，不阻塞UI）
 *   - execute：同步请求（阻塞当前线程，一般不用）
 *   - Callback：请求结果回调（onFailure失败 / onResponse成功）
 *
 * 使用方式：
 *   NetUtil.get("https://xxx", object : NetUtil.NetCallBack {
 *       override fun onSuccess(json: String?) { ... }
 *       override fun onError(msg: String?) { ... }
 *   })
 */
object NetUtil {

    // OkHttpClient单例（全局复用，避免重复创建）
    private val client = OkHttpClient()

    /**
     * GET请求
     *
     * @param url 请求地址
     * @param callback 网络回调（onSuccess / onError）
     *
     * 注意：回调在子线程执行，更新UI需要切换到主线程：
     *   activity?.runOnUiThread { ... }
     */
    fun get(url: String, callback: NetCallBack) {
        val req = Request.Builder().url(url).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                callback.onSuccess(json)
            }
        })
    }

    /**
     * 网络请求回调接口
     */
    interface NetCallBack {
        /** 请求成功，返回JSON字符串 */
        fun onSuccess(json: String?)

        /** 请求失败，返回错误信息 */
        fun onError(msg: String?)
    }
}
