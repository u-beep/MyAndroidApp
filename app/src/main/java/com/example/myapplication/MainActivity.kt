package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Build：获取Android版本号
import android.os.Build
// Toast：弹出的短暂提示消息
import android.widget.Toast
// PackageManager：判断权限是否授权
import android.content.pm.PackageManager
// RadioGroup：单选分组容器（底部导航）
import android.widget.RadioGroup
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
// FragmentTransaction：Fragment事务，用于替换Fragment
import androidx.fragment.app.FragmentTransaction

/**
 * 主页面 Activity（重构版：底部Tab导航 + Fragment切换）
 *
 * 功能：
 *   1. 底部三栏导航：首页 / 用户列表 / 我的
 *   2. 点击底部Tab切换Fragment（同一页面内切换，不跳转）
 *   3. 保留相册权限申请功能（供 MineFragment 调用）
 *
 * 架构说明：
 *   - Activity = 整体容器（只管底部导航和Fragment切换）
 *   - Fragment = 子页面（首页、用户列表、我的，各自独立）
 *   - 仿微信底部菜单：点哪个Tab，上方就显示对应的Fragment
 */
class MainActivity : AppCompatActivity() {

    // ============================================
    // 三个 Fragment 实例
    // ============================================
    private var homeFrag: HomeFragment? = null
    private var userFrag: UserListFragment? = null
    private var mineFrag: MineFragment? = null

    // 当前显示的 Fragment 引用
    private var currentFrag: androidx.fragment.app.Fragment? = null

    // 登录账号（从 LoginActivity 传过来）
    private var account = ""

    // ============================================
    // 权限相关常量
    // ============================================
    companion object {
        private const val REQ_STORAGE = 1001       // 存储权限请求码
        private const val ALBUM_CODE = 2001        // 相册请求码
    }

    // 需要申请的存储权限数组
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

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ============================================
        // 接收登录页传来的账号
        // ============================================
        account = intent.getStringExtra("USER_ACCOUNT") ?: ""

        // ============================================
        // 绑定底部导航 RadioGroup
        // ============================================
        val rgTab = findViewById<RadioGroup>(R.id.rg_tab)

        // 默认选中首页
        switchFragment(getHomeFrag())
        rgTab.check(R.id.rb_home)

        // ============================================
        // 底部Tab切换监听
        //
        // setOnCheckedChangeListener：选中项改变时触发
        // checkedId = 当前选中的 RadioButton 的 id
        // when(checkedId)：根据不同id切换不同Fragment
        // ============================================
        rgTab.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_home -> switchFragment(getHomeFrag())
                R.id.rb_user -> switchFragment(getUserFrag())
                R.id.rb_mine -> switchFragment(getMineFrag())
            }
        }
    }

    // ============================================
    // 懒加载创建 Fragment 实例
    // 避免重复创建，也保证账号参数能正确传入
    // ============================================
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

    // ============================================
    // Fragment 替换通用方法
    //
    // supportFragmentManager：Fragment管理器
    // beginTransaction()：开启一个事务
    // replace(R.id.fl_content, frag)：把frag替换到fl_content容器中
    // commit()：提交事务
    //
    // 就像换卡片：把旧卡片拿走，放入新卡片
    // ============================================
    private fun switchFragment(frag: androidx.fragment.app.Fragment) {
        if (frag === currentFrag) return  // 避免重复替换

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fl_content, frag)
        ft.commit()
        currentFrag = frag
    }

    // ============================================
    // 打开相册方法（供 MineFragment 调用）
    //
    // 为什么放在 Activity 里？
    //   因为权限申请只能在 Activity 中进行
    //   Fragment 通过 (activity as MainActivity).openAlbumFunc() 调用
    // ============================================
    fun openAlbumFunc() {
        if (PermissionUtil.hasPermission(this, STORAGE_PERMS[0])) {
            openAlbum()
        } else {
            PermissionUtil.requestPerm(this, STORAGE_PERMS, REQ_STORAGE)
        }
    }

    // ============================================
    // 权限申请结果回调
    // ============================================
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

    // 打开系统相册
    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, ALBUM_CODE)
    }

    // 接收相册返回的图片
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ALBUM_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                // 把图片URI传给 HomeFragment 显示
                homeFrag?.showImage(uri)
            }
        }
    }
}
