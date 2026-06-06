package com.example.myapplication

// Intent：用于页面跳转
import android.content.Intent
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
 *
 * 知识点：
 *   - Fragment 中跳转 Activity：Intent(context, XxxActivity::class.java)
 *   - Fragment 调用 Activity 方法：(activity as MainActivity).xxx()
 *   - arguments 获取 Fragment 传参
 */
class MineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        // 备忘录按钮 → 跳转备忘录页面
        // ============================================
        view.findViewById<Button>(R.id.btn_memo).setOnClickListener {
            val intent = Intent(requireContext(), MemoActivity::class.java)
            intent.putExtra("MEMO_ACCOUNT", account)
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
            requireContext().getSharedPreferences("USER_DATA", android.content.Context.MODE_PRIVATE).edit().clear().apply()
            requireContext().getSharedPreferences("SETTING", android.content.Context.MODE_PRIVATE).edit().putBoolean("auto_login", false).apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }

        return view
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
