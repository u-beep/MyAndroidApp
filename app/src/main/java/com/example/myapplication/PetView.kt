package com.example.myapplication

// Canvas：画布，用于自定义绘制
import android.graphics.Canvas
// Paint：画笔，设置颜色、大小等
import android.graphics.Paint
// Color：颜色工具
import android.graphics.Color
// RectF：圆角矩形区域
import android.graphics.RectF
// MotionEvent：触摸事件
import android.view.MotionEvent
// View：所有控件的基类
import android.view.View
// Context：上下文
import android.content.Context
// Handler + Runnable：定时器，用于动画和延时
import android.os.Handler
import android.os.Looper
// Log：日志
import android.util.Log

/**
 * 互动小宠物控件 🐱
 *
 * 功能：
 *   1. 用 emoji 显示可爱的猫咪表情
 *   2. 点击宠物 → 随机说话（气泡对话）
 *   3. 可以拖动宠物到屏幕任意位置
 *   4. 宠物会眨眼、换表情（自动动画）
 *   5. 长按宠物 → 特殊互动
 *
 * 知识点：
 *   - 自定义 View：继承 View，重写 onDraw 自己画
 *   - onTouchEvent：处理触摸事件（点击、拖动）
 *   - Handler.postDelayed：延时执行（动画、气泡消失）
 *   - Canvas + Paint：画布画笔，绘制图形和文字
 */
class PetView(context: Context) : View(context) {

    // ============================================
    // 宠物表情相关
    // ============================================

    // 所有可用的表情（emoji字符）
    private val faces = arrayOf("🐱", "😺", "😸", "😻", "😼", "😽", "🙀", "😿", "😾", "🐕", "🐶", "🐰")

    // 当前表情索引
    private var faceIndex = 0

    // 当前显示的表情
    private var currentFace = faces[0]

    // ============================================
    // 气泡对话相关
    // ============================================

    // 宠物说话内容
    private val speeches = arrayOf(
        "喵~你好呀！",
        "今天天气真好~",
        "摸摸我~",
        "你真棒！",
        "要不要休息一下？",
        "我在陪你写代码哦~",
        "加油加油！",
        "喵呜~想你了",
        "点我可以拖动哦~",
        "今天也要开心！",
        "我饿了...😺",
        "陪你到天荒地老~",
        "你是最好的！",
        "啦啦啦~♪"
    )

    // 当前气泡文字
    private var bubbleText = ""

    // 气泡是否显示
    private var showBubble = false

    // ============================================
    // 拖拽相关
    // ============================================

    // 手指按下时的坐标
    private var downX = 0f
    private var downY = 0f

    // 控件离左上角的偏移量
    private var offsetX = 0f
    private var offsetY = 0f

    // 是否正在拖动
    private var isDragging = false

    // 拖动阈值（超过这个距离才算拖动，否则算点击）
    private val DRAG_THRESHOLD = 10

    // ============================================
    // 画笔相关
    // ============================================

    // 表情画笔
    private val facePaint = Paint().apply {
        textSize = 80f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    // 气泡背景画笔
    private val bubblePaint = Paint().apply {
        color = Color.parseColor("#E0F7FA")
        isAntiAlias = true
        // 圆角矩形不需要 style 设置，默认就是FILL
    }

    // 气泡文字画笔
    private val bubbleTextPaint = Paint().apply {
        color = Color.parseColor("#00695C")
        textSize = 32f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    // 气泡小三角画笔
    private val trianglePaint = Paint().apply {
        color = Color.parseColor("#E0F7FA")
        isAntiAlias = true
    }

    // 阴影画笔（宠物下方的阴影）
    private val shadowPaint = Paint().apply {
        color = Color.parseColor("#15000000")
        isAntiAlias = true
    }

    // ============================================
    // 定时器：宠物自动眨眼换表情
    // ============================================
    private val handler = Handler(Looper.getMainLooper())

    // ============================================
    // 自动行为任务：随机换表情 + 冒泡说话
    //
    // 每隔 5~10 秒宠物自动做一个动作：
    //   - 换一个随机表情
    //   - 随机说一句话（显示气泡）
    //   - 气泡 3 秒后自动消失
    // ============================================
    private val autoActionRunnable = object : Runnable {
        override fun run() {
            if (!isDragging) {
                // 1. 随机换表情
                val newIndex = (0..4).random()  // 前5个正常表情
                faceIndex = newIndex
                currentFace = faces[faceIndex]

                // 2. 随机说一句话
                showSpeech(speeches.random())
            }
            // 每5~10秒触发一次
            handler.postDelayed(this, (5000..10000).random().toLong())
        }
    }

    // 气泡消失任务
    private val bubbleHideRunnable = Runnable {
        showBubble = false
        currentFace = faces[faceIndex]  // 恢复正常表情
        invalidate()
    }

    // ============================================
    // 初始化
    // ============================================
    init {
        // 启动自动行为：4秒后开始第一次
        handler.postDelayed(autoActionRunnable, 4000)
    }

    // ============================================
    // 测量控件大小
    // ============================================
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 宠物区域 120x120，上方留200给气泡
        setMeasuredDimension(300, 320)
    }

    // ============================================
    // 绘制宠物
    // ============================================
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // --------------------------------------------
        // 1. 画气泡（如果显示的话）
        // --------------------------------------------
        if (showBubble && bubbleText.isNotEmpty()) {
            drawBubble(canvas)
        }

        // --------------------------------------------
        // 2. 画宠物阴影
        // --------------------------------------------
        val shadowRect = RectF(45f, 260f, 255f, 280f)
        canvas.drawOval(shadowRect, shadowPaint)

        // --------------------------------------------
        // 3. 画宠物身体（圆形底色）
        // --------------------------------------------
        val bodyPaint = Paint().apply {
            color = Color.parseColor("#FFF8E1")
            isAntiAlias = true
        }
        canvas.drawCircle(150f, 220f, 55f, bodyPaint)

        // 身体边框
        val bodyStrokePaint = Paint().apply {
            color = Color.parseColor("#FFE082")
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawCircle(150f, 220f, 55f, bodyStrokePaint)

        // --------------------------------------------
        // 4. 画表情 emoji
        // --------------------------------------------
        val faceY = 220f + facePaint.textSize / 3  // 垂直居中修正
        canvas.drawText(currentFace, 150f, faceY, facePaint)
    }

    // ============================================
    // 绘制气泡对话框
    // ============================================
    private fun drawBubble(canvas: Canvas) {
        // 测量文字宽度
        val textWidth = bubbleTextPaint.measureText(bubbleText)
        val bubbleWidth = textWidth + 40f
        val bubbleHeight = 60f
        val bubbleLeft = 150f - bubbleWidth / 2
        val bubbleTop = 10f

        // 画圆角矩形气泡
        val rect = RectF(bubbleLeft, bubbleTop, bubbleLeft + bubbleWidth, bubbleTop + bubbleHeight)
        canvas.drawRoundRect(rect, 20f, 20f, bubblePaint)

        // 画气泡下方的小三角
        val triangleX = 150f
        val triangleTop = bubbleTop + bubbleHeight
        val trianglePath = android.graphics.Path()
        trianglePath.moveTo(triangleX - 10f, triangleTop)
        trianglePath.lineTo(triangleX, triangleTop + 12f)
        trianglePath.lineTo(triangleX + 10f, triangleTop)
        trianglePath.close()
        canvas.drawPath(trianglePath, trianglePaint)

        // 画气泡文字
        canvas.drawText(bubbleText, 150f, bubbleTop + bubbleHeight / 2 + 12f, bubbleTextPaint)
    }

    // ============================================
    // 触摸事件处理（点击 + 拖动）
    // ============================================
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // 手指按下
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                offsetX = event.rawX - left
                offsetY = event.rawY - top
                isDragging = false
                return true
            }

            // 手指移动
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - downX
                val dy = event.rawY - downY

                // 超过阈值才算拖动
                if (!isDragging && (Math.abs(dx) > DRAG_THRESHOLD || Math.abs(dy) > DRAG_THRESHOLD)) {
                    isDragging = true
                }

                if (isDragging) {
                    // 更新位置
                    val newX = (event.rawX - offsetX).toInt()
                    val newY = (event.rawY - offsetY).toInt()

                    // 限制不超出父控件边界
                    val parent = parent as? View
                    val maxX = (parent?.width ?: 1000) - width
                    val maxY = (parent?.height ?: 2000) - height

                    layout(
                        newX.coerceIn(0, maxX),
                        newY.coerceIn(0, maxY),
                        (newX + width).coerceIn(width, (parent?.width ?: 1000)),
                        (newY + height).coerceIn(height, (parent?.height ?: 2000))
                    )
                }
                return true
            }

            // 手指抬起
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    // 没有拖动 = 点击 → 触发互动
                    onPetClick()
                } else {
                    // 拖动结束 → 说句话
                    if ((0..1).random() == 1) {
                        showSpeech("别推我嘛~")
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // ============================================
    // 点击宠物互动
    // ============================================
    private fun onPetClick() {
        // 随机选一句话
        val speech = speeches.random()
        showSpeech(speech)

        // 随机换一个开心表情
        val happyFaces = arrayOf("😺", "😸", "😻", "😽")
        currentFace = happyFaces.random()
        invalidate()
    }

    // ============================================
    // 显示气泡对话
    // ============================================
    fun showSpeech(text: String) {
        bubbleText = text
        showBubble = true
        invalidate()

        // 移除之前的隐藏任务
        handler.removeCallbacks(bubbleHideRunnable)
        // 3秒后自动隐藏气泡
        handler.postDelayed(bubbleHideRunnable, 3000)
    }

    // ============================================
    // 清理资源（Activity销毁时调用）
    // ============================================
    fun release() {
        handler.removeCallbacks(autoActionRunnable)
        handler.removeCallbacks(bubbleHideRunnable)
    }
}
