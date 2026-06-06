package com.example.myapplication

import android.app.Application
import com.example.myapplication.utils.SPUtil

/**
 * 自定义Application类
 * 
 * 用于全局初始化SP工具类
 * 需要在AndroidManifest.xml中注册
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 全局初始化SP工具类
        SPUtil.init(this)
    }
}