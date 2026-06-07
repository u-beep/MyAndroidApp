package com.example.myapplication

// Context：上下文环境
import android.content.Context
// Bundle：保存Fragment状态的数据
import android.os.Bundle
// Log：日志输出
import android.util.Log
// LayoutInflater：把XML布局"吹"成View对象
import android.view.LayoutInflater
// View：控件基类
import android.view.View
// ViewGroup：View容器
import android.view.ViewGroup
// Button：按钮控件
import android.widget.Button
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出短暂提示
import android.widget.Toast
// Fragment：碎片基类
import androidx.fragment.app.Fragment
// LinearLayoutManager：RecyclerView布局管理器
import androidx.recyclerview.widget.LinearLayoutManager
// RecyclerView：列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 用户列表 Fragment
 *
 * 功能：
 *   1. 用 RecyclerView 展示所有注册用户
 *   2. 点击条目提示
 *   3. 长按条目弹出删除确认弹窗
 *   4. 懒加载：页面第一次可见才加载用户列表
 *
 * 知识点：
 *   - Fragment完整7大生命周期
 *   - Fragment 中使用 RecyclerView 和 Activity 中完全一样
 *   - requireContext() 代替 this 获取上下文
 *   - 懒加载：ViewPager预加载相邻页面，配合标志位避免重复请求
 */
class UserListFragment : Fragment() {

    private val TAG = "FragLife"

    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    // ========== 懒加载标志位 ==========
    private var isLoad = false

    // ========== Fragment完整7大生命周期 ==========
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "UserListFragment → onAttach（绑定宿主Activity）")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "UserListFragment → onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "UserListFragment → onCreateView（加载布局）")

        val view = inflater.inflate(R.layout.fragment_userlist, container, false)

        // ============================================
        // 设置 RecyclerView
        // ============================================
        val rv = view.findViewById<RecyclerView>(R.id.rv_user)
        rv.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList)
        rv.adapter = userAdapter

        // ============================================
        // 条目点击事件
        // ============================================
        userAdapter.itemClick = { user ->
            Toast.makeText(requireContext(), "选中：${user.account}", Toast.LENGTH_SHORT).show()
        }

        // ============================================
        // 条目长按事件 → 弹出自定义删除确认弹窗
        // ============================================
        userAdapter.longClick = { selectUser ->
            val dialog = android.app.Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_delete)
            dialog.window?.setLayout(550, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
            val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
            val tvMsg = dialog.findViewById<TextView>(R.id.tv_msg)

            tvMsg.text = "确定要删除用户【${selectUser.account}】吗？"
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnConfirm.setOnClickListener {
                val dao = UserDao(requireContext())
                dao.deleteUser(selectUser.account)
                loadUserList()
                dialog.dismiss()
                Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "UserListFragment → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "UserListFragment → onResume（页面可见可交互）")

        // ========== 懒加载：只在页面第一次可见时加载 ==========
        if (!isLoad) {
            loadData()
            isLoad = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "UserListFragment → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "UserListFragment → onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "UserListFragment → onDestroyView（视图销毁，实例仍在）")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "UserListFragment → onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "UserListFragment → onDetach（与Activity解绑）")
    }

    // ========== 懒加载数据方法 ==========
    private fun loadData() {
        Log.e(TAG, "UserListFragment → loadData（首次加载用户列表）")
        loadUserList()
    }

    // 从数据库读取所有注册用户，装载进列表
    private fun loadUserList() {
        val dao = UserDao(requireContext())
        userList.clear()
        userList.addAll(dao.getAllUser())
        if (::userAdapter.isInitialized) {
            userAdapter.notifyDataSetChanged()
        }
    }
}
