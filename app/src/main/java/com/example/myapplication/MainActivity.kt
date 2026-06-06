package com.example.myapplication

// Intent：用于页面跳转和传值
import android.content.Intent
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Build：获取Android版本号
import android.os.Build
// Button：按钮控件
import android.widget.Button
// ImageView：图片显示控件
import android.widget.ImageView
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// PackageManager：判断权限是否授权
import android.content.pm.PackageManager
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
// LinearLayoutManager：RecyclerView的布局管理器
import androidx.recyclerview.widget.LinearLayoutManager
// RecyclerView：列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 主页面 Activity
 *
 * 功能：
 *   1. 显示欢迎信息（登录账号）
 *   2. 显示当前用户的性别和爱好（从数据库查询）
 *   3. 用RecyclerView列表展示所有注册用户信息
 *   4. 退出登录功能
 *   5. 进入备忘录
 *   6. 打开手机相册图片（动态权限申请）
 *
 * 权限申请流程：
 *   点击按钮 → 检查权限 → 有权限直接打开相册
 *   → 没权限则申请 → 用户授权后打开相册 / 拒绝则提示
 */
class MainActivity : AppCompatActivity() {

    // ============================================
    // 定义集合存放所有用户数据
    // ============================================
    private val userList = mutableListOf<User>()

    // 适配器引用
    private lateinit var userAdapter: UserAdapter

    // ============================================
    // 权限相关常量
    // ============================================
    // 权限请求码：用于在回调中标识是"存储权限"的申请结果
    // 就像给每次申请贴个编号，回调时通过编号区分
    companion object {
        private const val REQ_STORAGE = 1001       // 存储权限请求码
        private const val ALBUM_CODE = 2001        // 相册请求码
    }

    // 需要申请的存储权限数组
    // Android 13+ 用 READ_MEDIA_IMAGES 替代 READ/WRITE_EXTERNAL_STORAGE
    // 根据当前系统版本自动选择合适的权限
    private val STORAGE_PERMS: Array<String> by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+：只需要读图片权限
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // Android 12及以下：读写外部存储权限
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    // 图片展示ImageView，选择图片后显示到这里
    private lateinit var ivShow: ImageView

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ============================================
        // 绑定控件
        // ============================================
        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val tvInfo = findViewById<TextView>(R.id.tv_info)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        val rvUserList = findViewById<RecyclerView>(R.id.rv_user_list)
        val btnMemo = findViewById<Button>(R.id.btn_memo)
        val btnOpenImg = findViewById<Button>(R.id.btn_open_img)   // 打开相册按钮
        val btnSetting = findViewById<Button>(R.id.btn_setting)     // 设置按钮
        ivShow = findViewById(R.id.iv_show)                         // 图片展示区域

        // ============================================
        // 接收登录页传来的账号
        // ============================================
        val account = intent.getStringExtra("USER_ACCOUNT") ?: ""
        tvWelcome.text = "欢迎 $account !"

        // ============================================
        // 创建 UserDao 实例
        // ============================================
        val userDao = UserDao(this)

        // ============================================
        // 从数据库读取当前用户的性别和爱好
        // ============================================
        val currentUser = userDao.getUserByAccount(account)
        if (currentUser != null) {
            tvInfo.text = "性别：${currentUser.sex}  爱好：${currentUser.hobby}"
        }

        // ============================================
        // 设置RecyclerView
        // ============================================
        rvUserList.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList)
        rvUserList.adapter = userAdapter

        // 从数据库加载用户列表
        loadUserList(userDao)

        // ============================================
        // 条目点击事件
        // ============================================
        userAdapter.itemClick = { user ->
            Toast.makeText(this, "选中：${user.account}", Toast.LENGTH_SHORT).show()
        }

        // ============================================
        // 条目长按事件 → 弹出自定义删除确认弹窗
        // ============================================
        userAdapter.longClick = { selectUser ->
            val dialog = android.app.Dialog(this)
            dialog.setContentView(R.layout.dialog_delete)
            dialog.window?.setLayout(550, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
            val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
            val tvMsg = dialog.findViewById<TextView>(R.id.tv_msg)

            tvMsg.text = "确定要删除用户【${selectUser.account}】吗？"
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                val dao = UserDao(this)
                dao.deleteUser(selectUser.account)
                loadUserList(dao)
                dialog.dismiss()
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        // ============================================
        // 退出登录 → 跳回登录页
        // ============================================
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ============================================
        // 备忘录按钮 → 跳转到备忘录页面
        // ============================================
        btnMemo.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)
            startActivity(intent)
        }

        // ============================================
        // 设置按钮 → 跳转到设置页面
        // ============================================
        btnSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        // ============================================
        // 打开相册按钮 → 动态权限申请 + 打开相册
        //
        // 流程：
        //   1. 点击按钮
        //   2. 检查是否已有存储权限
        //   3. 有权限 → 直接打开相册
        //   4. 没权限 → 发起权限申请 → 回调中处理结果
        // ============================================
        btnOpenImg.setOnClickListener {
            // 检查第一个权限是否已授权
            if (PermissionUtil.hasPermission(this, STORAGE_PERMS[0])) {
                // 已有权限，直接打开相册
                openAlbum()
            } else {
                // 没有权限，发起申请
                // 系统会弹出授权弹窗，用户点击"允许"或"拒绝"
                PermissionUtil.requestPerm(this, STORAGE_PERMS, REQ_STORAGE)
            }
        }
    }

    // ============================================
    // 权限申请结果回调
    //
    // 当用户在系统授权弹窗中点击"允许"或"拒绝"后，
    // 系统自动调用这个方法，告诉APP用户的选择
    //
    // @param requestCode  请求码（和申请时传的一致）
    // @param permissions  申请的权限数组
    // @param grantResults 每个权限的授权结果
    //   PERMISSION_GRANTED = 已授权
    //   PERMISSION_DENIED = 被拒绝
    // ============================================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 判断是不是存储权限的申请结果
        if (requestCode == REQ_STORAGE) {
            // 判断第一个权限是否授权
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户点击了"允许" → 打开相册
                openAlbum()
            } else {
                // 用户点击了"拒绝" → 提示无法使用
                Toast.makeText(this, "需要存储权限才能打开相册", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ============================================
    // 打开系统相册
    //
    // 使用隐式Intent跳转到系统图库
    // ACTION_PICK：让用户从现有数据中选一个
    // type="image/*"：只显示图片类型的文件
    // startActivityForResult：启动并等待返回结果
    // ============================================
    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"     // 只选图片
        startActivityForResult(intent, ALBUM_CODE)
    }

    // ============================================
    // 接收相册返回的图片
    //
    // 当用户在相册中选择了一张图片后，系统回调这个方法
    // data?.data 就是选中图片的URI（统一资源标识符）
    // 可以直接用setImageURI显示到ImageView
    //
    // @param requestCode  请求码（和打开相册时传的一致）
    // @param resultCode   结果码（RESULT_OK=用户正常选择，RESULT_CANCELED=用户取消）
    // @param data         返回的数据（包含图片URI）
    // ============================================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ALBUM_CODE && resultCode == RESULT_OK) {
            // 获取选中图片的URI
            val uri = data?.data
            if (uri != null) {
                // 显示图片到ImageView
                ivShow.setImageURI(uri)
                // 让ImageView可见（默认是gone隐藏的）
                ivShow.visibility = ImageView.VISIBLE
            }
        }
    }

    /**
     * 从数据库读取所有注册用户，装载进列表
     */
    private fun loadUserList(userDao: UserDao) {
        userList.clear()
        userList.addAll(userDao.getAllUser())
        userAdapter.notifyDataSetChanged()
    }
}
