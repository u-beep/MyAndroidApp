package com.example.myapplication

import java.io.Serializable

/**
 * 用户数据实体类
 *
 * 作用：封装单条用户的数据，方便在列表中传递和显示
 *
 * data class：Kotlin的数据类，自动生成equals、hashCode、toString等方法
 * 用法：User("zhangsan', '123456', '男', '篮球 看书', '北京')
 *
 * 实现Serializable接口：
 *   - 让User对象可以通过Intent传递（方式3：Serializable传对象）
 *   - 简单，只需实现接口，无需额外方法
 *   - 性能一般（使用反射），适合简单场景
 *
 * @param account 账号（也是SharedPreferences的key）
 * @param pwd     密码
 * @param sex     性别（男/女）
 * @param hobby   爱好（如"篮球 看书"）
 * @param city    所在城市（如"北京"）
 */
data class User(
    val account: String,   // 账号
    val pwd: String,       // 密码
    val sex: String,       // 性别
    val hobby: String,     // 爱好
    val city: String       // 所在城市
) : Serializable