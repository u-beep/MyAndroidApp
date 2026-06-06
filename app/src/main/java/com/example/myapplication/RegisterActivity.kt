package com.example.myapplication

// Bundle：保存Activity状态的数据
import android.os.Bundle
// Button：按钮控件
import android.widget.Button
// CheckBox：复选框控件（可勾选/取消，多个可同时勾选）
import android.widget.CheckBox
// EditText：输入框控件
import android.widget.EditText
// ProgressBar：加载进度条控件
import android.widget.ProgressBar
// RadioGroup：单选分组容器
import android.widget.RadioGroup
// Spinner：下拉选择框控件
import android.widget.Spinner
// ArrayAdapter：数组适配器，把数组和Spinner连接起来
import android.widget.ArrayAdapter
// AdapterView：Spinner的父类，用于选中事件监听
import android.widget.AdapterView
// Toast：弹出的短暂提示消息
import android.widget.Toast
// View：控件基类，用于控制显示/隐藏
import android.view.View
// AppCompatActivity：兼容低版本Android的Activity基类
import androidx.appcompat.app.AppCompatActivity

/**
 * 注册页面 Activity
 * 功能：注册新账号 + 密码确认 + 性别单选 + 爱好多选 + 保存到数据库
 *
 * 核心思路：
 *   使用 SQLite 数据库存储用户信息
 *   每个用户一条记录：account、pwd、sex、hobby 四个字段
 *   通过 UserDao 操作数据库（增删改查）
 */
class RegisterActivity : AppCompatActivity() {

    // onCreate：Activity创建时自动调用的方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView：把XML布局文件加载到屏幕上显示
        setContentView(R.layout.activity_register)

        // ============================================
        // 绑定控件
        // ============================================
        val etAccount = findViewById<EditText>(R.id.et_account)       // 注册账号输入框
        val etPwd = findViewById<EditText>(R.id.et_pwd)               // 密码输入框
        val etPwd2 = findViewById<EditText>(R.id.et_pwd2)             // 确认密码输入框
        val btnRegister = findViewById<Button>(R.id.btn_register)     // 注册按钮
        val rgSex = findViewById<RadioGroup>(R.id.rg_sex)             // 性别单选组
        val cbBall = findViewById<CheckBox>(R.id.cb_ball)             // 爱好：打篮球
        val cbBook = findViewById<CheckBox>(R.id.cb_book)             // 爱好：看书
        val cbGame = findViewById<CheckBox>(R.id.cb_game)             // 爱好：玩游戏
        val loading = findViewById<ProgressBar>(R.id.loading)         // 加载进度条
        val spCity = findViewById<Spinner>(R.id.sp_city)             // 城市下拉选择

        // ============================================
        // Spinner 城市下拉选择绑定
        //
        // Spinner = 下拉选择框，像网页上的下拉菜单
        // ArrayAdapter = 适配器，把数组和Spinner连接起来
        //
        // 流程：
        //   1. 定义城市数组（数据源）
        //   2. 创建 ArrayAdapter（桥梁：数据 → 控件）
        //   3. 把适配器设置给 Spinner
        //   4. 监听选中事件，获取用户选择的城市
        // ============================================

        // 1. 定义城市数组（数据源）
        val cityArr = arrayOf("北京", "上海", "广州", "深圳", "杭州", "成都", "重庆")

        // 默认选中第一个城市
        var selectCity = cityArr[0]

        // 2. 创建 ArrayAdapter 适配器
        //   第1个参数 this = 上下文
        //   第2个参数 android.R.layout.simple_spinner_dropdown_item = 系统自带的下拉样式
        //   第3个参数 cityArr = 数据源数组
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            cityArr
        )

        // 3. 把适配器设置给 Spinner
        spCity.adapter = adapter

        // 4. 监听选中事件
        // onItemSelectedListener：当用户选择某一项时触发
        //   onItemSelected：选中某项时调用，pos就是选中项的位置
        //   onNothingSelected：没有选中时调用（一般用不到，但要空实现）
        spCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                // 根据选中位置，从数组中取出城市名
                selectCity = cityArr[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 没有选中任何项时调用（一般不需要处理）
            }
        }

        // ============================================
        // 点击注册按钮
        // ============================================
        btnRegister.setOnClickListener {

            // 显示加载动画 + 禁止重复点击
            loading.visibility = View.VISIBLE
            btnRegister.isEnabled = false

            // .text.toString()：获取输入框中的文字并转成字符串
            // .trim()：去掉文字前后的空格
            val account = etAccount.text.toString().trim()
            val pwd = etPwd.text.toString().trim()
            val pwd2 = etPwd2.text.toString().trim()

            // --------------------------------------------
            // 第1步：判断不能为空
            // --------------------------------------------
            if (account.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
                Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnRegister.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第2步：判断两次密码一致
            // --------------------------------------------
            if (pwd != pwd2) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnRegister.isEnabled = true
                return@setOnClickListener
            }

            // --------------------------------------------
            // 第3步：获取性别（RadioGroup单选）
            // checkedRadioButtonId：获取当前选中的RadioButton的id
            // 如果选中的是 rb_woman，就是女，否则默认男
            // --------------------------------------------
            var sex = "男"
            if (rgSex.checkedRadioButtonId == R.id.rb_woman) {
                sex = "女"
            }

            // --------------------------------------------
            // 第4步：获取爱好（CheckBox多选）
            // isChecked：判断复选框是否被勾选
            // 可以同时勾选多个，所以每个都要单独判断
            // --------------------------------------------
            var hobby = ""
            if (cbBall.isChecked) hobby += "篮球 "
            if (cbBook.isChecked) hobby += "看书 "
            if (cbGame.isChecked) hobby += "游戏 "
            if (hobby.isEmpty()) hobby = "无"

            // --------------------------------------------
            // 第5步+第6步：通过数据库保存用户信息（核心！）
            // 使用 UserDao 操作数据库
            // addUser()：插入一条新用户记录
            //   - 如果账号已存在（account列UNIQUE），insert会失败返回-1
            //   - 这样就自动防重复了，不需要单独判断账号是否存在
            // --------------------------------------------
            val userDao = UserDao(this)
            val isSuccess = userDao.addUser(account, pwd, sex, hobby, selectCity)

            if (isSuccess) {
                // 注册成功
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()

                // --------------------------------------------
                // 第7步：跳回登录页
                // finish()：关闭当前注册页，自动回到登录页
                // --------------------------------------------
                finish()
            } else {
                // 注册失败（通常是账号已存在）
                Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                btnRegister.isEnabled = true
            }
        }
    }
}
