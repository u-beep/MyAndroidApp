package com.example.myapplication

import android.app.Application
import com.example.myapplication.utils.SPUtil

/**
 * 自定义Application类
 *
 * 作用：
 *   1. 全局初始化SP工具类
 *   2. 全局存储登录用户信息（跨页面共享，无需Intent传参）
 *   3. 全局APP配置（字体大小等）
 *
 * 生命周期：
 *   - 整个APP只有一个Application实例
 *   - APP启动时最先创建，APP退出时最后销毁
 *   - 任何Activity都可以通过 application as MyApp 获取实例
 *
 * 使用方式：
 *   保存：val myApp = application as MyApp; myApp.loginUser = user
 *   获取：val myApp = application as MyApp; val user = myApp.loginUser
 *   退出登录时置空：myApp.loginUser = null
 */
class MyApp : Application() {

    // ============================================
    // 全局登录用户（全APP任意页面获取，无需Intent传参）
    // ============================================
    var loginUser: User? = null

    // ============================================
    // 全局字体配置
    // ============================================
    var appFontSize: Int = 16

    override fun onCreate() {
        super.onCreate()
        // 全局初始化SP工具类
        SPUtil.init(this)

        // 从SP恢复全局配置
        appFontSize = SPUtil.getInt("font_size", 16)
    }
}