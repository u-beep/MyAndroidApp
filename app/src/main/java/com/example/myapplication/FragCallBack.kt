package com.example.myapplication

/**
 * Fragment ↔ Activity 通信回调接口（标准企业写法）
 *
 * 使用场景：Fragment需要通知Activity执行某些逻辑
 * 例如：Fragment点击按钮，通知Activity弹窗/跳转/更新数据
 *
 * 使用步骤：
 *   1. Activity实现此接口
 *   2. Fragment在onAttach中获取接口实例
 *   3. Fragment通过接口发送消息
 */
interface FragCallBack {
    /**
     * Fragment向Activity发送消息
     * @param msg 消息内容
     */
    fun sendMsg(msg: String)
}
