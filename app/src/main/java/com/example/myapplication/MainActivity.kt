package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.widget.Toast
import android.content.pm.PackageManager
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.utils.SPUtil
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * 主页面 Activity（singleTask启动模式 + ViewPager2滑动 + Fragment切换）
 *
 * 启动模式：singleTask（栈内唯一）
 *   - 栈中存在MainActivity实例→把它上方所有Activity全部出栈销毁
 *   - 自身置顶，回调onNewIntent()
 *   - 适用场景：APP首页，任意页面跳首页，中间页面全部关闭
 *
 * 功能：
 *   1. ViewPager2 + 底部四栏导航联动：首页 / 用户列表 / 我的 / 新闻
 *   2. 左右滑动切换页面，底部Tab同步高亮
 *   3. 点击底部Tab切换页面，ViewPager同步滑动
 *   4. 实现FragCallBack接口，接收Fragment消息
 *   5. 封装replaceFragment/addFragment切换方法（支持回退栈）
 *   6. 保留相册权限申请功能
 *   7. 演示5种Intent传值（跳转DetailActivity）
 *   8. 生命周期日志
 */
class MainActivity : AppCompatActivity(), FragCallBack {

    private val TAG = "life"

    // 四个 Fragment 实例
    private var homeFrag: HomeFragment? = null
    private var userFrag: UserListFragment? = null
    private var mineFrag: MineFragment? = null
    private var newsFrag: NewsFragment? = null

    // 登录账号
    private var account = ""

    companion object {
        private const val REQ_STORAGE = 1001
        private const val ALBUM_CODE = 2001
    }

    private val STORAGE_PERMS: Array<String> by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity → onCreate")

        // 接收登录页传来的账号
        account = intent.getStringExtra("USER_ACCOUNT") ?: ""

        // ========== 方式5：Application全局变量获取登录用户 ==========
        val myApp = application as MyApp
        if (myApp.loginUser != null) {
            Log.d(TAG, "全局用户：${myApp.loginUser!!.account}，字体：${myApp.appFontSize}sp")
        }

        // ========== ViewPager2 + Fragment 滑动四页面 ==========
        // 懒加载创建 Fragment 实例
        homeFrag = getHomeFrag()
        userFrag = getUserFrag()
        mineFrag = getMineFrag()
        newsFrag = getNewsFrag()

        // 创建适配器并绑定ViewPager2
        val frags = listOf(homeFrag!!, userFrag!!, mineFrag!!, newsFrag!!)
        val adapter = FragVPAdapter(this, frags)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = adapter

        // 页面滑动 → 修改底部选中Tab
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val rgTab = findViewById<RadioGroup>(R.id.rg_tab)
                when (position) {
                    0 -> rgTab.check(R.id.rb_home)
                    1 -> rgTab.check(R.id.rb_user)
                    2 -> rgTab.check(R.id.rb_mine)
                    3 -> rgTab.check(R.id.rb_news)
                }
            }
        })

        // 底部按钮 → 切换ViewPager页面
        val rgTab = findViewById<RadioGroup>(R.id.rg_tab)
        rgTab.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_home -> viewPager.currentItem = 0
                R.id.rb_user -> viewPager.currentItem = 1
                R.id.rb_mine -> viewPager.currentItem = 2
                R.id.rb_news -> viewPager.currentItem = 3
            }
        }

        // ========== 解决ViewPager2嵌套RecyclerView滑动冲突 ==========
        // 在RecyclerView上设置触摸监听，优先处理上下滑动
        // 通过post延迟执行，确保Fragment视图已创建
        viewPager.post {
            setupRecyclerViewTouch(viewPager)
        }
    }

    /**
     * 解决ViewPager2嵌套RecyclerView滑动冲突
     *
     * 原因：ViewPager2内部是RecyclerView，手指稍微左右偏移就会被拦截
     * 导致新闻页面的RecyclerView上下滑不动
     *
     * 方案：找到ViewPager2内部的RecyclerView（第0个子View），
     * 遍历其可见的子条目，如果条目中包含新闻RecyclerView，
     * 就在该RecyclerView上添加触摸监听，按下时禁止ViewPager2拦截触摸事件
     */
    private fun setupRecyclerViewTouch(viewPager: ViewPager2) {
        // ViewPager2内部第0个子View就是它的RecyclerView
        val vpRecyclerView = viewPager.getChildAt(0) as? RecyclerView ?: return

        // 监听ViewPager2的页面滚动，滚动停止后重新绑定触摸监听
        vpRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bindInnerRecyclerViewTouch(vpRecyclerView, viewPager)
                }
            }
        })

        // 初始绑定一次
        bindInnerRecyclerViewTouch(vpRecyclerView, viewPager)
    }

    /**
     * 遍历ViewPager2内部RecyclerView的可见子条目，
     * 找到新闻RecyclerView并绑定触摸事件
     */
    private fun bindInnerRecyclerViewTouch(vpRecyclerView: RecyclerView, viewPager: ViewPager2) {
        for (i in 0 until vpRecyclerView.childCount) {
            val pageView = vpRecyclerView.getChildAt(i)
            val innerRv = pageView?.findViewById<RecyclerView>(R.id.rv_news) ?: continue

            // 在新闻RecyclerView上设置触摸监听
            // 按下时通知父View不要拦截触摸事件，让RecyclerView自己处理上下滑动
            innerRv.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // 告诉ViewPager2：别拦截我！我要自己处理滑动
                        viewPager.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // 手指抬起，恢复ViewPager2的拦截能力
                        viewPager.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false  // 返回false，不消费事件，让RecyclerView正常处理
            }
        }
    }

    // ========== 实现FragCallBack接口：接收Fragment发来的消息 ==========
    override fun sendMsg(msg: String) {
        Toast.makeText(this, "收到：$msg", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "MainActivity → 收到Fragment消息：$msg")
    }

    /**
     * singleTask栈内复用时回调
     * 其他页面跳转MainActivity，上方Activity全部出栈
     * 需要调用setIntent(intent)更新当前Activity的intent
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d(TAG, "MainActivity → onNewIntent（singleTask复用）")

        // 可以从新Intent获取数据
        val newAccount = intent.getStringExtra("USER_ACCOUNT") ?: ""
        if (newAccount.isNotEmpty()) {
            account = newAccount
        }

        Toast.makeText(this, "首页singleTask复用，上层页面已清除", Toast.LENGTH_SHORT).show()
    }

    // 懒加载创建 Fragment 实例
    private fun getHomeFrag(): HomeFragment {
        if (homeFrag == null) {
            homeFrag = HomeFragment.newInstance(account)
        }
        return homeFrag!!
    }

    private fun getUserFrag(): UserListFragment {
        if (userFrag == null) {
            userFrag = UserListFragment()
        }
        return userFrag!!
    }

    private fun getMineFrag(): MineFragment {
        if (mineFrag == null) {
            mineFrag = MineFragment.newInstance(account)
        }
        return mineFrag!!
    }

    private fun getNewsFrag(): NewsFragment {
        if (newsFrag == null) {
            newsFrag = NewsFragment.newInstance()
        }
        return newsFrag!!
    }

    // ========== Fragment事务切换方法（支持回退栈） ==========

    /**
     * 替换Fragment（replace方式）
     * - 清空容器内原有Fragment，替换新的
     * - 旧Fragment销毁
     *
     * @param frag 要显示的Fragment
     * @param addStack 是否加入回退栈（true：按返回键可回退上一页）
     */
    fun replaceFragment(frag: Fragment, addStack: Boolean = false) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.view_pager, frag)
        if (addStack) {
            ft.addToBackStack(frag.javaClass.name)
        }
        ft.commit()
    }

    /**
     * 添加Fragment（add方式）
     * - 新增碎片，覆盖在上层
     * - 原有页面保留在底层不销毁
     *
     * @param frag 要添加的Fragment
     * @param addStack 是否加入回退栈（true：按返回键可回退上一页）
     */
    fun addFragment(frag: Fragment, addStack: Boolean = false) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(R.id.view_pager, frag)
        if (addStack) {
            ft.addToBackStack(null)
        }
        ft.commit()
    }

    /**
     * 打开详情页（演示5种Intent传值方式）
     * 供HomeFragment等Fragment调用
     */
    fun openDetailPage() {
        val myApp = application as MyApp

        val intent = Intent(this, DetailActivity::class.java)

        // ========== 方式1：putExtra基础类型 ==========
        intent.putExtra("name", myApp.loginUser?.account ?: "游客")
        intent.putExtra("age", 22)

        // ========== 方式2：Bundle打包传参 ==========
        val bundle = Bundle().apply {
            putString("bundle_name", myApp.loginUser?.account ?: "游客")
            putInt("bundle_score", 95)
        }
        intent.putExtras(bundle)

        // ========== 方式3：Serializable传对象 ==========
        myApp.loginUser?.let {
            intent.putExtra("user_info", it)
        }

        // ========== 方式4：Parcelable传对象（推荐） ==========
        myApp.loginUser?.let {
            val userParcel = UserParcel(
                account = it.account,
                pwd = it.pwd,
                sex = it.sex,
                hobby = it.hobby,
                city = it.city
            )
            intent.putExtra("user_p", userParcel)
        }

        // 方式5：Application全局变量，在DetailActivity中直接获取，无需Intent传参

        startActivity(intent)
    }

    // 打开相册方法（供 MineFragment 调用）
    fun openAlbumFunc() {
        if (PermissionUtil.hasPermission(this, STORAGE_PERMS[0])) {
            openAlbum()
        } else {
            PermissionUtil.requestPerm(this, STORAGE_PERMS, REQ_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum()
            } else {
                Toast.makeText(this, "需要存储权限才能打开相册", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, ALBUM_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ALBUM_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                homeFrag?.showImage(uri)
            }
        }
    }

    // ========== Activity完整7大生命周期 ==========
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity → onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity → onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity → onDestroy")
    }
}
