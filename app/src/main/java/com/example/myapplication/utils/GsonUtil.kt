package com.example.myapplication.utils

import com.google.gson.Gson

/**
 * Gson解析工具类（全局统一解析）
 *
 * 知识点：
 *   - Gson：Google的JSON解析库，最常用
 *   - fromJson(json, Class)：将JSON字符串转成Kotlin对象
 *   - toJson(obj)：将Kotlin对象转成JSON字符串
 *
 * 使用方式：
 *   val resp = GsonUtil.jsonToBean(json, NewsResp::class.java)
 *   val json = GsonUtil.beanToJson(user)
 */
object GsonUtil {

    private val gson = Gson()

    /**
     * JSON字符串 → Kotlin对象
     *
     * @param json JSON字符串
     * @param clazz 目标类的Class对象
     * @return 解析后的对象
     */
    fun <T> jsonToBean(json: String, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }

    /**
     * Kotlin对象 → JSON字符串
     *
     * @param obj 要序列化的对象
     * @return JSON字符串
     */
    fun beanToJson(obj: Any): String {
        return gson.toJson(obj)
    }
}
