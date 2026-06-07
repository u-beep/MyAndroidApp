package com.example.myapplication

// Context：上下文环境
import android.content.Context
// Intent：用于页面跳转
import android.content.Intent
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
// Fragment：碎片基类
import androidx.fragment.app.Fragment

/**
 * 我的 Fragment
 *
 * 功能：
 *   1. 打开备忘录
 *   2. 跳转设置页面
 *   3. 打开相册（调用 MainActivity 的方法）
 *   4. 退出登录
 *   5. 懒加载：页面第一次可见才加载用户信息
 *
 * 知识点：
 *   - Fragment完整7大生命周期
 *   - Fragment 中跳转 Activity：Intent(context, XxxActivity::class.java)
 *   - Fragment 调用 Activity 方法：(activity as MainActivity).xxx()
 *   - arguments 获取 Fragment 传参
 *   - 懒加载：ViewPager预加载相邻页面，配合标志位避免重复请求
 */
class MineFragment : Fragment() {

    private val TAG = "FragLife"

    // ========== 懒加载标志位 ==========
    private var isLoad = false

    // ========== Fragment完整7大生命周期 ==========
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "MineFragment → onAttach（绑定宿主Activity）")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "MineFragment → onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "MineFragment → onCreateView（加载布局）")

        val view = inflater.inflate(R.layout.fragment_mine, container, false)

        // 接收账号
        val account = arguments?.getString("USER_ACCOUNT") ?: ""

        // 显示用户信息
        val tvMineInfo = view.findViewById<TextView>(R.id.tv_mine_info)
        val userDao = UserDao(requireContext())
        val currentUser = userDao.getUserByAccount(account)
        if (currentUser != null) {
            tvMineInfo.text = "当前账号：$account\n性别：${currentUser.sex}  城市：${currentUser.city}"
        } else {
            tvMineInfo.text = "当前账号：$account"
        }

        // ============================================
        // 备忘录按钮 → 跳转备忘录页面（新版SQLite备忘录）
        // ============================================
        view.findViewById<Button>(R.id.btn_memo).setOnClickListener {
            val intent = Intent(requireContext(), MemoListActivity::class.java)
            startActivity(intent)
        }

        // ============================================
        // 设置按钮 → 跳转设置页面
        // ============================================
        view.findViewById<Button>(R.id.btn_setting).setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        // ============================================
        // 打开相册按钮 → 调用 MainActivity 的方法
        // 因为权限申请必须在 Activity 中进行
        // (activity as MainActivity) 把 activity 转成 MainActivity
        // ============================================
        view.findViewById<Button>(R.id.btn_open_img).setOnClickListener {
            (activity as? MainActivity)?.openAlbumFunc()
        }

        // ============================================
        // 退出登录 → 跳回登录页
        // ============================================
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            // 清除用户登录信息
            com.example.myapplication.utils.SPUtil.remove(LoginActivity.KEY_ACCOUNT)
            com.example.myapplication.utils.SPUtil.remove(LoginActivity.KEY_PWD)
            // 关闭自动登录
            com.example.myapplication.utils.SPUtil.putBoolean(LoginActivity.KEY_AUTO, false)
            // 清空全局用户
            val myApp = requireActivity().application as MyApp
            myApp.loginUser = null
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "MineFragment → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "MineFragment → onResume（页面可见可交互）")

        // ========== 懒加载：只在页面第一次可见时加载 ==========
        if (!isLoad) {
            loadData()
            isLoad = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "MineFragment → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "MineFragment → onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "MineFragment → onDestroyView（视图销毁，实例仍在）")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "MineFragment → onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "MineFragment → onDetach（与Activity解绑）")
    }

    // ========== 懒加载数据方法 ==========
    private fun loadData() {
        Log.e(TAG, "MineFragment → loadData（首次加载用户信息）")
        // 这里可以做数据库查询、网络请求等耗时操作
        // 用户信息已在onCreateView中从数据库加载
    }

    // 伴生对象：工厂方法
    companion object {
        fun newInstance(account: String): MineFragment {
            val fragment = MineFragment()
            val args = Bundle()
            args.putString("USER_ACCOUNT", account)
            fragment.arguments = args
            return fragment
        }
    }
}
