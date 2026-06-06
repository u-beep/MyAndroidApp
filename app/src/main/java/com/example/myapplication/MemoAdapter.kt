package com.example.myapplication

// LayoutInflater：用来把XML布局文件"吹"成真正的View对象
import android.view.LayoutInflater
// View：所有控件的基类
import android.view.View
// ViewGroup：View的容器
import android.view.ViewGroup
// Button：按钮控件
import android.widget.Button
// TextView：文字显示控件
import android.widget.TextView
// RecyclerView：安卓最核心的列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 备忘录列表适配器
 *
 * 作用：把"备忘录数据集合"和"列表条目布局"连接起来
 *   - 数据源：MutableList<MemoItem>（当前账号的所有备忘录）
 *   - 条目布局：item_memo.xml
 *
 * 两个回调接口：
 *   - itemClick：点击条目 → 编辑备忘录
 *   - itemDelete：点击删除 → 删除备忘录
 *
 * @param list 备忘录数据集合
 */
class MemoAdapter(var list: MutableList<MemoItem>) : RecyclerView.Adapter<MemoAdapter.MemoVH>() {

    /**
     * 条目点击回调（点击编辑）
     * 用法：adapter.itemClick = { memoItem -> ... }
     */
    var itemClick: ((MemoItem) -> Unit)? = null

    /**
     * 条目删除回调（点击删除按钮）
     * 用法：adapter.itemDelete = { memoItem -> ... }
     */
    var itemDelete: ((MemoItem) -> Unit)? = null

    /**
     * ViewHolder：缓存条目中的控件
     */
    inner class MemoVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 备忘录内容预览
        val tvContent: TextView = itemView.findViewById(R.id.tv_memo_content)
        // 保存时间
        val tvTime: TextView = itemView.findViewById(R.id.tv_memo_time)
        // 删除按钮
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete_memo)
    }

    /**
     * 创建条目视图
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoVH(view)
    }

    /**
     * 绑定数据到条目
     */
    override fun onBindViewHolder(holder: MemoVH, position: Int) {
        // 取出当前条目的备忘录数据
        val memo = list[position]

        // 设置内容预览文字
        holder.tvContent.text = memo.content

        // 设置时间文字
        holder.tvTime.text = memo.timestamp

        // ============================================
        // 点击条目 → 触发编辑回调
        // ============================================
        holder.itemView.setOnClickListener {
            itemClick?.invoke(memo)
        }

        // ============================================
        // 点击删除按钮 → 触发删除回调
        // ============================================
        holder.btnDelete.setOnClickListener {
            itemDelete?.invoke(memo)
        }
    }

    /**
     * 返回数据总数
     */
    override fun getItemCount(): Int = list.size
}
