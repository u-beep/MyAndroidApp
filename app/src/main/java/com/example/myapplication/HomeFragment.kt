package com.example.myapplication

// Bundle：保存Fragment状态的数据
import android.os.Bundle
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
 *   2. 显示当前用户的性别、爱好和城市
 *   3. 互动小宠物
 *
 * 知识点：
 *   - Fragment 是内嵌在 Activity 里的子页面
 *   - onCreateView：Fragment 创建视图时调用（类似 Activity 的 setContentView）
 *   - inflater.inflate：把 XML 布局文件变成 View 对象
 */
class HomeFragment : Fragment() {

    // 互动小宠物
    private lateinit var petView: PetView

    // 图片展示 ImageView
    private lateinit var ivShow: ImageView

    // onCreateView：Fragment 创建界面时自动调用
    // 作用：告诉系统这个 Fragment 要显示什么布局
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    // ============================================
    // 显示从相册选择的图片（供 MainActivity 调用）
    // ============================================
    fun showImage(uri: Uri) {
        if (::ivShow.isInitialized) {
            ivShow.setImageURI(uri)
            ivShow.visibility = ImageView.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::petView.isInitialized) {
            petView.release()
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
