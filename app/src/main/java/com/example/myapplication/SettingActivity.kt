package com.example.myapplication

// Intent：用于页面跳转
import android.content.Intent
// SharedPreferences：本地轻量级存储（键值对）
import android.content.SharedPreferences
// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
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

/**
 * 设置页面 Activity
 *
 * 功能：
 *   1. 自动登录开关（Switch控件）
 *   2. 亮度调节（SeekBar拖动条）
 *   3. 音量调节（SeekBar拖动条）
 *   4. 用 SharedPreferences 保存所有设置（永久保存）
 *   5. 退出登录（清除用户信息，回到登录页）
 *
 * 知识点：
 *   - Switch = 开关控件
 *   - SeekBar = 拖动条控件，max设置最大值，progress获取当前进度
 *   - OnSeekBarChangeListener = 拖动监听（三个回调方法）
 *   - SP 保存设置 → 下次打开页面时恢复
 */
class SettingActivity : AppCompatActivity() {

    // SharedPreferences 引用，用于保存设置
    lateinit var sp: SharedPreferences

    // Switch 开关控件引用
    lateinit var switchAuto: Switch

    // 亮度 SeekBar + 百分比文字
    lateinit var seekBrightness: SeekBar
    lateinit var tvBrightValue: TextView

    // 音量 SeekBar + 百分比文字
    lateinit var seekVolume: SeekBar
    lateinit var tvVolumeValue: TextView

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

        // ============================================
        // 返回按钮 → 关闭当前页面，回到主页
        // ============================================
        btnBack.setOnClickListener {
            finish()
        }

        // ============================================
        // 获取 SharedPreferences
        // ============================================
        sp = getSharedPreferences("SETTING", MODE_PRIVATE)

        // ============================================
        // 1. 自动登录开关
        // ============================================

        // 读取保存的开关状态，恢复开关显示
        switchAuto.isChecked = sp.getBoolean("auto_login", false)

        // 开关状态改变时的监听
        switchAuto.setOnCheckedChangeListener { button, isChecked ->
            // 保存开关状态
            sp.edit().putBoolean("auto_login", isChecked).apply()

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
        val brightness = sp.getInt("brightness", 50)
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
                sp.edit().putInt("brightness", seekBar!!.progress).apply()
                Toast.makeText(this@SettingActivity, "亮度已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 3. 音量调节（SeekBar拖动条）
        // 结构和亮度完全一样
        // ============================================

        // 读取保存的音量值，默认50
        val volume = sp.getInt("volume", 50)
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
                sp.edit().putInt("volume", seekBar!!.progress).apply()
                Toast.makeText(this@SettingActivity, "音量已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 4. 退出登录按钮
        // ============================================
        btnLogout.setOnClickListener {
            // 清除用户登录信息
            getSharedPreferences("USER_DATA", MODE_PRIVATE).edit().clear().apply()
            // 同时关闭自动登录（否则退出后还会自动登录）
            getSharedPreferences("SETTING", MODE_PRIVATE).edit().putBoolean("auto_login", false).apply()

            Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show()

            // 跳转到登录页
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
