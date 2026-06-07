package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 详情页 Activity（singleTop启动模式演示）
 *
 * 启动模式：singleTop（栈顶复用）
 *   - 如果DetailActivity已在栈顶，再次打开不会新建实例
 *   - 而是回调onNewIntent()接收新数据，不走onCreate
 *   - 适用场景：通知跳转页、搜索页
 *
 * 演示5种Intent传值方式：
 *   1. putExtra基础类型（String/Int/Boolean）
 *   2. Bundle打包传参
 *   3. Serializable传对象
 *   4. Parcelable传对象（推荐）
 *   5. Application全局变量
 */
class DetailActivity : AppCompatActivity() {

    private val TAG = "life"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Log.d(TAG, "DetailActivity → onCreate")

        val btnBack = findViewById<Button>(R.id.btn_back)
        val btnOpenAgain = findViewById<Button>(R.id.btn_open_again)

        // 返回按钮
        btnBack.setOnClickListener { finish() }

        // singleTop演示：再次打开自己
        btnOpenAgain.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("name", "再次打开_${System.currentTimeMillis() % 1000}")
            startActivity(intent)
        }

        // 接收并展示5种传值数据
        displayIntentData(intent)
    }

    /**
     * singleTop栈顶复用时只走这个方法，不走onCreate
     * 需要调用setIntent(intent)更新当前Activity的intent
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)  // 更新intent，否则getIntent()还是旧数据
        Log.d(TAG, "DetailActivity → onNewIntent")
        Toast.makeText(this, "栈顶复用！走了onNewIntent，没有新建页面", Toast.LENGTH_SHORT).show()

        // 重新接收新数据
        displayIntentData(intent)
    }

    /**
     * 接收并展示5种Intent传值数据
     */
    private fun displayIntentData(intent: Intent?) {
        if (intent == null) return

        // ========== 方式1：putExtra基础类型 ==========
        val name = intent.getStringExtra("name") ?: ""
        val age = intent.getIntExtra("age", 0)
        findViewById<TextView>(R.id.tv_extra).text = "姓名：$name，年龄：$age"

        // ========== 方式2：Bundle打包传参 ==========
        val bundle = intent.extras
        val bundleName = bundle?.getString("bundle_name") ?: ""
        val bundleScore = bundle?.getInt("bundle_score", 0) ?: 0
        findViewById<TextView>(R.id.tv_bundle).text = "姓名：$bundleName，分数：$bundleScore"

        // ========== 方式3：Serializable传对象 ==========
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user_info", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("user_info") as? User
        }
        findViewById<TextView>(R.id.tv_serializable).text = if (user != null)
            "账号：${user.account}，性别：${user.sex}，城市：${user.city}"
        else
            "未接收到Serializable对象"

        // ========== 方式4：Parcelable传对象（推荐） ==========
        val userP = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user_p", UserParcel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("user_p")
        }
        findViewById<TextView>(R.id.tv_parcelable).text = if (userP != null)
            "账号：${userP.account}，性别：${userP.sex}，城市：${userP.city}"
        else
            "未接收到Parcelable对象"

        // ========== 方式5：Application全局变量 ==========
        val myApp = application as MyApp
        val globalUser = myApp.loginUser
        val globalFontSize = myApp.appFontSize
        findViewById<TextView>(R.id.tv_app_global).text = if (globalUser != null)
            "全局用户：${globalUser.account}，全局字体：${globalFontSize}sp"
        else
            "全局用户为空（未登录）"
    }

    // ========== Activity完整7大生命周期 ==========
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "DetailActivity → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "DetailActivity → onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "DetailActivity → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "DetailActivity → onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DetailActivity → onDestroy")
    }
}