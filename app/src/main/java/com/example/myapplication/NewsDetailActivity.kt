package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 新闻详情页
 *
 * 功能：
 *   1. 显示新闻标题、来源、时间、图片、描述
 *   2. 点击「查看原文」用浏览器打开新闻链接
 *
 * 知识点：
 *   - Intent传String参数：逐字段传递，避免Serializable反序列化问题
 *   - 隐式Intent：ACTION_VIEW打开浏览器查看网页
 */
class NewsDetailActivity : AppCompatActivity() {

    private val TAG = "life"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        Log.d(TAG, "NewsDetailActivity → onCreate")

        // ========== 接收新闻数据（逐字段接收） ==========
        val title = intent.getStringExtra("news_title") ?: ""
        val source = intent.getStringExtra("news_source") ?: ""
        val ctime = intent.getStringExtra("news_ctime") ?: ""
        val desc = intent.getStringExtra("news_desc") ?: ""
        val picUrl = intent.getStringExtra("news_pic_url") ?: ""
        val newsUrl = intent.getStringExtra("news_url") ?: ""

        // 绑定控件
        val tvTitle = findViewById<TextView>(R.id.tv_detail_title)
        val tvInfo = findViewById<TextView>(R.id.tv_detail_info)
        val ivPic = findViewById<ImageView>(R.id.iv_detail_pic)
        val tvDesc = findViewById<TextView>(R.id.tv_detail_desc)
        val btnOpenUrl = findViewById<android.widget.Button>(R.id.btn_open_url)

        // 填充数据
        tvTitle.text = title
        tvInfo.text = "$source | $ctime"

        // 描述（如果为空则显示提示文字）
        if (desc.isNotEmpty()) {
            tvDesc.text = desc
        } else {
            tvDesc.text = "暂无详细描述，请点击下方「查看原文」阅读完整报道。"
        }

        // 图片（如果有的话加载显示，403时隐藏图片区域）
        if (picUrl.isNotEmpty()) {
            ivPic.visibility = ImageView.VISIBLE
            Thread {
                try {
                    val url = java.net.URL(picUrl)
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.connectTimeout = 5000
                    conn.readTimeout = 5000
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                    // 添加Referer头，绕过图片防盗链
                    conn.setRequestProperty("Referer", picUrl)
                    if (conn.responseCode == 200) {
                        val bitmap = android.graphics.BitmapFactory.decodeStream(conn.inputStream)
                        if (bitmap != null) {
                            runOnUiThread {
                                ivPic.setImageBitmap(bitmap)
                            }
                        } else {
                            runOnUiThread { ivPic.visibility = ImageView.GONE }
                        }
                    } else {
                        Log.e("NetLog", "图片加载失败，HTTP状态码：${conn.responseCode}")
                        runOnUiThread { ivPic.visibility = ImageView.GONE }
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    Log.e("NetLog", "图片加载异常：${e.message}")
                    runOnUiThread { ivPic.visibility = ImageView.GONE }
                }
            }.start()
        }

        // ========== 查看原文：用浏览器打开新闻链接 ==========
        btnOpenUrl.setOnClickListener {
            if (newsUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl))
                startActivity(intent)
            } else {
                Toast.makeText(this, "暂无原文链接", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ========== 生命周期日志 ==========
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "NewsDetailActivity → onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "NewsDetailActivity → onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "NewsDetailActivity → onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "NewsDetailActivity → onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "NewsDetailActivity → onDestroy")
    }
}
