package com.example.myapplication

// Activity：活动基类
import android.app.Activity
// PackageManager：包管理器，判断权限是否已授权
import android.content.pm.PackageManager
// ActivityCompat：兼容类，提供requestPermissions方法
import androidx.core.app.ActivityCompat
// ContextCompat：兼容类，提供checkSelfPermission方法
import androidx.core.content.ContextCompat

/**
 * 权限工具类（全项目通用，永久复用）
 *
 * 作用：封装权限判断和申请逻辑，一行代码搞定
 *
 * 安卓权限分两种：
 *   1. 普通权限（INTERNET网络、VIBRATE震动等）
 *      → 在AndroidManifest.xml声明即可，安装时自动授权
 *   2. 危险权限（存储、相机、定位、电话等）
 *      → 必须在运行时动态申请，用户手动授权才能使用
 *
 * 使用示例：
 *   // 判断是否有权限
 *   if (PermissionUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
 *       // 有权限，执行功能
 *   } else {
 *       // 没权限，发起申请
 *       PermissionUtil.requestPerm(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
 *   }
 */
class PermissionUtil {

    companion object {

        /**
         * 判断单个权限是否已授权
         *
         * 原理：
         *   ContextCompat.checkSelfPermission() 检查权限状态
         *   返回值 == PERMISSION_GRANTED 表示已授权
         *   返回值 == PERMISSION_DENIED 表示未授权
         *
         * @param act       当前Activity
         * @param permission 权限字符串（如 Manifest.permission.READ_EXTERNAL_STORAGE）
         * @return true=已授权，false=未授权
         */
        fun hasPermission(act: Activity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(act, permission) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * 申请权限（可同时申请多个）
         *
         * 原理：
         *   ActivityCompat.requestPermissions() 弹出系统授权弹窗
         *   用户点击"允许"或"拒绝"后，Activity的onRequestPermissionsResult()会收到回调
         *
         * @param act   当前Activity
         * @param perms 要申请的权限数组
         * @param code  请求码（自定义数字，用于在回调中区分是哪次申请）
         *              例如：存储权限用1001，相机权限用1002
         */
        fun requestPerm(act: Activity, perms: Array<String>, code: Int) {
            ActivityCompat.requestPermissions(act, perms, code)
        }
    }
}
