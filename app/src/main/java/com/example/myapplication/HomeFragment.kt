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
// TextView：文字显示控件
import android.widget.TextView
// FrameLayout：层叠布局（宠物浮层）
import android.widget.FrameLayout
// ImageView：图片显示控件
import android.widget.ImageView
// Net.Uri：统一资源标识符
import android.net.Uri
// Fragment：碎片基类
import androidx.fragment.app.Fragment

/**
 * 首页 Fragment
 *
 * 功能：
 *   1. 显示欢迎信息（登录账号）
 *   2. 显示当前用户的性别、爱好和城市（卡片样式）
 *   3. 互动小宠物
 *   4. 懒加载：页面第一次可见才加载数据
 *
 * 知识点：
 *   - Fragment完整7大生命周期
 *   - 懒加载：ViewPager预加载相邻页面，配合标志位避免重复请求
 */
class HomeFragment : Fragment() {

    private val TAG = "FragLife"

    // 互动小宠物
    private lateinit var petView: PetView

    // 图片展示 ImageView
    private lateinit var ivShow: ImageView

    // ========== 懒加载标志位 ==========
    private var isLoad = false

    // ========== Fragment完整7大生命周期 ==========
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "HomeFragment → onAttach（绑定宿主Activity）")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "HomeFragment → onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "HomeFragment → onCreateView（加载布局）")

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 绑定控件
        val tvWelcome = view.findViewById<TextView>(R.id.tv_welcome)
        val tvInfo = view.findViewById<TextView>(R.id.tv_info)
        ivShow = view.findViewById(R.id.iv_show)

        // 接收 MainActivity 传来的账号
        val account = arguments?.getString("USER_ACCOUNT") ?: ""
        tvWelcome.text = "欢迎 $account !"

        // 从数据库读取当前用户信息
        val userDao = UserDao(requireContext())
        val currentUser = userDao.getUserByAccount(account)
        if (currentUser != null) {
            tvInfo.text = "性别：${currentUser.sex}  爱好：${currentUser.hobby}  城市：${currentUser.city}"
        }

        // 初始化互动小宠物
        val petContainer = view.findViewById<FrameLayout>(R.id.pet_container)
        petView = PetView(requireContext())
        petContainer.addView(petView)

        petContainer.post {
            val screenHeight = petContainer.height
            petView.layout(
                20,
                screenHeight - petView.height - 20,
                20 + petView.width,
                screenHeight - 20
            )
            petView.showSpeech("喵~你好！")
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "HomeFragment → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "HomeFragment → onResume（页面可见可交互）")

        // ========== 懒加载：只在页面第一次可见时加载 ==========
        if (!isLoad) {
            loadData()
            isLoad = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "HomeFragment → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "HomeFragment → onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "HomeFragment → onDestroyView（视图销毁，实例仍在）")
        if (::petView.isInitialized) {
            petView.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "HomeFragment → onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "HomeFragment → onDetach（与Activity解绑）")
    }

    // ========== 懒加载数据方法 ==========
    private fun loadData() {
        Log.e(TAG, "HomeFragment → loadData（首次加载数据）")
        // 这里可以做数据库查询、网络请求等耗时操作
    }

    // ============================================
    // 显示从相册选择的图片（供 MainActivity 调用）
    // ============================================
    fun showImage(uri: Uri) {
        if (::ivShow.isInitialized) {
            ivShow.setImageURI(uri)
            ivShow.visibility = ImageView.VISIBLE
        }
    }

    // 伴生对象：工厂方法，把账号参数通过 Bundle 传给 Fragment
    companion object {
        fun newInstance(account: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("USER_ACCOUNT", account)
            fragment.arguments = args
            return fragment
        }
    }
}
