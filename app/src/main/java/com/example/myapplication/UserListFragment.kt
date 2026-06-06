package com.example.myapplication

// Bundle：保存Fragment状态的数据
import android.os.Bundle
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
 *
 * 知识点：
 *   - Fragment 中使用 RecyclerView 和 Activity 中完全一样
 *   - requireContext() 代替 this 获取上下文
 */
class UserListFragment : Fragment() {

    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_userlist, container, false)

        // ============================================
        // 设置 RecyclerView
        // ============================================
        val rv = view.findViewById<RecyclerView>(R.id.rv_user)
        rv.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList)
        rv.adapter = userAdapter

        // 从数据库加载用户列表
        val dao = UserDao(requireContext())
        loadUserList(dao)

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
                dao.deleteUser(selectUser.account)
                loadUserList(dao)
                dialog.dismiss()
                Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        return view
    }

    // 从数据库读取所有注册用户，装载进列表
    private fun loadUserList(userDao: UserDao) {
        userList.clear()
        userList.addAll(userDao.getAllUser())
        userAdapter.notifyDataSetChanged()
    }
}
