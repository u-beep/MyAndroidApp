package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
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
 *   6. 打开详情页（5种Intent传值演示）
 *   7. 退出登录（清除用户信息+清空全局变量，回到登录页）
 *
 * 演示：
 *   - 方式5：Application全局变量获取登录用户（无需Intent传参）
 *   - 生命周期日志
 */
class SettingActivity : AppCompatActivity() {

    private val TAG = "life"

    private val KEY_FONT = "font_size"
    private val KEY_NIGHT = "night_mode"

    private lateinit var switchAuto: Switch
    private lateinit var seekBrightness: SeekBar
    private lateinit var tvBrightValue: TextView
    private lateinit var seekVolume: SeekBar
    private lateinit var tvVolumeValue: TextView
    private lateinit var seekFont: SeekBar
    private lateinit var tvFont: TextView
    private lateinit var cbNight: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        Log.d(TAG, "SettingActivity → onCreate")

        // 绑定控件
        switchAuto = findViewById(R.id.switch_auto)
        val btnLogout = findViewById<Button>(R.id.btn_logout)
        val btnBack = findViewById<Button>(R.id.btn_back)
        seekBrightness = findViewById(R.id.seek_brightness)
        tvBrightValue = findViewById(R.id.tv_bright_value)
        seekVolume = findViewById(R.id.seek_volume)
        tvVolumeValue = findViewById(R.id.tv_volume_value)
        seekFont = findViewById(R.id.sb_font)
        tvFont = findViewById(R.id.tv_font)
        cbNight = findViewById(R.id.cb_night)

        // ============================================
        // 方式5：Application全局变量获取登录用户
        // 无需Intent传参，直接从MyApp全局获取
        // ============================================
        val myApp = application as MyApp
        val globalUser = myApp.loginUser
        if (globalUser != null) {
            Log.d(TAG, "设置页获取全局用户：${globalUser.account}，字体：${myApp.appFontSize}sp")
        }

        // ============================================
        // 方式2：Bundle打包传参接收
        // （如果MainActivity用Bundle方式传参的话）
        // ============================================
        val bundle = intent.extras
        val bundleAccount = bundle?.getString("bundle_account") ?: ""
        if (bundleAccount.isNotEmpty()) {
            Log.d(TAG, "设置页接收Bundle传参：$bundleAccount")
        }

        // 返回按钮
        btnBack.setOnClickListener {
            finish()
        }

        // ============================================
        // 1. 自动登录开关
        // ============================================
        switchAuto.isChecked = SPUtil.getBoolean("auto_login", false)
        switchAuto.setOnCheckedChangeListener { button, isChecked ->
            SPUtil.putBoolean("auto_login", isChecked)
            Toast.makeText(this, if (isChecked) "已开启自动登录" else "已关闭自动登录", Toast.LENGTH_SHORT).show()
        }

        // ============================================
        // 2. 亮度调节
        // ============================================
        val brightness = SPUtil.getInt("brightness", 50)
        seekBrightness.progress = brightness
        tvBrightValue.text = "当前：$brightness%"

        seekBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBrightValue.text = "当前：$progress%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                SPUtil.putInt("brightness", seekBar!!.progress)
                Toast.makeText(this@SettingActivity, "亮度已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 3. 音量调节
        // ============================================
        val volume = SPUtil.getInt("volume", 50)
        seekVolume.progress = volume
        tvVolumeValue.text = "当前：$volume%"

        seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumeValue.text = "当前：$progress%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                SPUtil.putInt("volume", seekBar!!.progress)
                Toast.makeText(this@SettingActivity, "音量已保存", Toast.LENGTH_SHORT).show()
            }
        })

        // ============================================
        // 4. 字体大小调节
        // ============================================
        val fontSize = SPUtil.getInt(KEY_FONT, 16)
        seekFont.progress = fontSize - 12
        tvFont.text = "当前字体大小：${fontSize}sp"

        seekFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress + 12
                tvFont.text = "当前字体大小：${size}sp"
                SPUtil.putInt(KEY_FONT, size)
                // 同步更新全局变量
                myApp.appFontSize = size
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        // ============================================
        // 5. 夜间模式开关
        // ============================================
        val nightOpen = SPUtil.getBoolean(KEY_NIGHT)
        cbNight.isChecked = nightOpen
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
            // 关闭自动登录
            SPUtil.putBoolean(LoginActivity.KEY_AUTO, false)

            // ========== 方式5：清空全局用户 ==========
            myApp.loginUser = null

            Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show()

            // 跳转到登录页
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // ========== Activity完整7大生命周期 ==========
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "SettingActivity → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "SettingActivity → onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "SettingActivity → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "SettingActivity → onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SettingActivity → onDestroy")
    }
}