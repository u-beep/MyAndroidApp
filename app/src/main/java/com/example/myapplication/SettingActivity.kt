package com.example.myapplication

// Intent：用于页面跳转
import android.content.Intent
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// CheckBox：复选框控件
import android.widget.CheckBox
// SeekBar：拖动条控件（像手机音量/亮度滑块）
import android.widget.SeekBar
// Switch：开关控件（像手机设置里的开关）
import android.widget.Switch
// TextView：文字显示控件
import android.widget.TextView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.utils.SPUtil

/**
 * 设置页面 Activity
 *
 * 功能：
 *   1. 自动登录开关（Switch控件）
 *   2. 亮度调节（SeekBar拖动条）
 *   3. 音量调节（SeekBar拖动条）
 *   4. 字体大小调节（SeekBar拖动条）
 *   5. 夜间模式开关（CheckBox）
 *   6. 用 SPUtil 工具类保存所有设置（永久保存）
 *   7. 退出登录（清除用户信息，回到登录页）
 *
 * 知识点：
 *   - Switch = 开关控件
 *   - SeekBar = 拖动条控件，max设置最大值，progress获取当前进度
 *   - OnSeekBarChangeListener = 拖动监听（三个回调方法）
 *   - SP 保存设置 → 下次打开页面时恢复
 */
class SettingActivity : AppCompatActivity() {

    // 字体大小和夜间模式的key
    private val KEY_FONT = "font_size"
    private val KEY_NIGHT = "night_mode"

    // Switch 开关控件引用
    private lateinit var switchAuto: Switch

    // 亮度 SeekBar + 百分比文字
    private lateinit var seekBrightness: SeekBar
    private lateinit var tvBrightValue: TextView

    // 音量 SeekBar + 百分比文字
    private lateinit var seekVolume: SeekBar
    private lateinit var tvVolumeValue: TextView
    
    // 字体大小控件
    private lateinit var seekFont: SeekBar
    private lateinit var tvFont: TextView
    
    // 夜间模式开关
    private lateinit var cbNight: CheckBox

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // ============================================
        // 绑定控件
        // ============================================
        switchAuto = findViewById(R.id.switch_auto)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        val btnBack = findViewById<Button>(R.id.btn_back)
        seekBrightness = findViewById(R.id.seek_brightness)
        tvBrightValue = findViewById(R.id.tv_bright_value)
        seekVolume = findViewById(R.id.seek_volume)
        tvVolumeValue = findViewById(R.id.tv_volume_value)
        
        // 绑定新增控件
        seekFont = findViewById(R.id.sb_font)
        tvFont = findViewById(R.id.tv_font)
        cbNight = findViewById(R.id.cb_night)

        // ============================================
        // 返回按钮 → 关闭当前页面，回到主页
        // ============================================
        btnBack.setOnClickListener {
            finish()
        }

        // ============================================
        // 1. 自动登录开关
        // ============================================

        // 读取保存的开关状态，恢复开关显示
        switchAuto.isChecked = SPUtil.getBoolean("auto_login", false)

        // 开关状态改变时的监听
        switchAuto.setOnCheckedChangeListener { button, isChecked ->
            // 保存开关状态
            SPUtil.putBoolean("auto_login", isChecked)

            // 根据开关状态显示不同的提示
            if (isChecked) {
                Toast.makeText(this, "已开启自动登录", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "已关闭自动登录", Toast.LENGTH_SHORT).show()
            }
        }

        // ============================================
        // 2. 亮度调节（SeekBar拖动条）
        //
        // SeekBar 使用流程：
        //   1. 从 SP 读取保存的亮度值
        //   2. 设置到 SeekBar 的 progress（当前进度）
        //   3. 更新百分比文字显示
        //   4. 监听拖动事件，实时更新 + 停止时保存
        // ============================================

        // 读取保存的亮度值，默认50
        val brightness = SPUtil.getInt("brightness", 50)
        seekBrightness.progress = brightness
        tvBrightValue.text = "当前：$brightness%"

        // SeekBar 拖动监听
        // OnSeekBarChangeListener：拖动条状态变化时自动触发
        //   onProgressChanged → 拖动中实时回调（每次进度变化都触发）
        //   onStartTrackingTouch → 手指按下拖动条时触发
        //   onStopTrackingTouch → 手指松开拖动条时触发（保存数据的好时机）
        seekBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 正在拖动 → 实时更新百分比文字
                // progress = 当前进度值（0~100）
                // fromUser = 是否是用户手动拖动（true=手动，false=代码设置）
                tvBrightValue.text = "当前：$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 手指按下拖动条 → 不需要处理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 手指松开 → 保存当前亮度值到 SP
                SPUtil.putInt("brightness", seekBar!!.progress)
                Toast.makeText(this@SettingActivity, "亮度已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 3. 音量调节（SeekBar拖动条）
        // 结构和亮度完全一样
        // ============================================

        // 读取保存的音量值，默认50
        val volume = SPUtil.getInt("volume", 50)
        seekVolume.progress = volume
        tvVolumeValue.text = "当前：$volume%"

        // 音量 SeekBar 拖动监听
        seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 实时更新百分比
                tvVolumeValue.text = "当前：$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 手指按下 → 不处理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 手指松开 → 保存音量值
                SPUtil.putInt("volume", seekBar!!.progress)
                Toast.makeText(this@SettingActivity, "音量已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 4. 字体大小调节
        // ============================================
        // 读取保存的字体大小，默认16sp
        val fontSize = SPUtil.getInt(KEY_FONT, 16)
        seekFont.progress = fontSize - 12  // 12~22sp对应0~10
        tvFont.text = "当前字体大小：${fontSize}sp"

        // 拖动条修改字体大小保存SP
        seekFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress + 12  // 12~22sp
                tvFont.text = "当前字体大小：${size}sp"
                SPUtil.putInt(KEY_FONT, size)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        // ============================================
        // 5. 夜间模式开关
        // ============================================
        // 读取保存的夜间模式状态
        val nightOpen = SPUtil.getBoolean(KEY_NIGHT)
        cbNight.isChecked = nightOpen

        // 夜间模式勾选保存
        cbNight.setOnCheckedChangeListener { _, isChecked ->
            SPUtil.putBoolean(KEY_NIGHT, isChecked)
            Toast.makeText(this@SettingActivity, 
                if (isChecked) "夜间模式已开启" else "夜间模式已关闭", 
                Toast.LENGTH_SHORT).show()
        }

        // ============================================
        // 6. 退出登录按钮
        // ============================================
        btnLogout.setOnClickListener {
            // 清除用户登录信息
            SPUtil.remove(LoginActivity.KEY_ACCOUNT)
            SPUtil.remove(LoginActivity.KEY_PWD)
            // 关闭自动登录（否则退出后还会自动登录）
            SPUtil.putBoolean(LoginActivity.KEY_AUTO, false)

            Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show()

            // 跳转到登录页
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}