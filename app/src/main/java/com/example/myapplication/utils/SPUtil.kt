package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences工具类（单例模式）
 * 
 * 全局统一管理SP，项目只创建一个SP文件`user_config`
 * 提供字符串、布尔值、整数的存取方法，以及删除和清空功能
 */
object SPUtil {
    // SP文件名
    private const val SP_NAME = "user_config"
    private lateinit var sp: SharedPreferences

    /**
     * 初始化（Application中调用一次即可）
     * @param context 应用上下文
     */
    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    // ============== 字符串类型 ==============
    /**
     * 保存字符串：账号、密码等
     * @param key 键名
     * @param value 值
     */
    fun putString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    /**
     * 获取字符串
     * @param key 键名
     * @param def 默认值
     * @return 字符串值
     */
    fun getString(key: String, def: String = ""): String {
        return sp.getString(key, def) ?: def
    }

    // ============== 布尔类型 ==============
    /**
     * 保存布尔值：记住密码、自动登录、夜间模式等
     * @param key 键名
     * @param value 值
     */
    fun putBoolean(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * 获取布尔值
     * @param key 键名
     * @param def 默认值
     * @return 布尔值
     */
    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return sp.getBoolean(key, def)
    }

    // ============== 整数类型 ==============
    /**
     * 保存整数值：字体大小等
     * @param key 键名
     * @param value 值
     */
    fun putInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    /**
     * 获取整数值
     * @param key 键名
     * @param def 默认值
     * @return 整数值
     */
    fun getInt(key: String, def: Int = 16): Int {
        return sp.getInt(key, def)
    }

    // ============== 其他类型 ==============
    /**
     * 保存浮点数
     * @param key 键名
     * @param value 值
     */
    fun putFloat(key: String, value: Float) {
        sp.edit().putFloat(key, value).apply()
    }

    /**
     * 获取浮点数
     * @param key 键名
     * @param def 默认值
     * @return 浮点数值
     */
    fun getFloat(key: String, def: Float = 0f): Float {
        return sp.getFloat(key, def)
    }

    /**
     * 保存长整型
     * @param key 键名
     * @param value 值
     */
    fun putLong(key: String, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    /**
     * 获取长整型
     * @param key 键名
     * @param def 默认值
     * @return 长整型值
     */
    fun getLong(key: String, def: Long = 0L): Long {
        return sp.getLong(key, def)
    }

    // ============== 删除操作 ==============
    /**
     * 删除单个key
     * @param key 要删除的键名
     */
    fun remove(key: String) {
        sp.edit().remove(key).apply()
    }

    /**
     * 清空全部SP数据
     */
    fun clearAll() {
        sp.edit().clear().apply()
    }
}