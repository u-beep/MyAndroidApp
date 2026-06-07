package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 备忘录多布局适配器
 *
 * 核心原理：重写getItemViewType()，一套列表包含3种样式：
 *   TYPE_NORMAL = 0  普通备忘录条目
 *   TYPE_AD     = 1  广告条目
 *   TYPE_FOOT   = 2  底部加载脚布局
 *
 * 支持功能：
 *   - 下拉刷新：refreshAll() 替换全量数据
 *   - 上拉加载：addMoreData() 追加数据
 *   - 条目点击编辑、长按删除
 */
class MultiMemoAdapter(var dataList: MutableList<Memo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_NORMAL = 0   // 普通备忘录
        const val TYPE_AD = 1       // 广告
        const val TYPE_FOOT = 2     // 底部加载
    }

    // 条目点击、长按删除回调
    var itemClick: (Memo) -> Unit = {}
    var longClick: (Memo) -> Unit = {}

    // 上拉加载：是否还有下一页
    var hasMore: Boolean = true

    // ========= 三种ViewHolder =========

    /**
     * 普通备忘录ViewHolder
     */
    inner class NormalVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    /**
     * 广告ViewHolder
     */
    inner class AdVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAdText: TextView = itemView.findViewById(R.id.tv_ad_text)
    }

    /**
     * 底部加载脚ViewHolder
     */
    inner class FootVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFooter: TextView = itemView.findViewById(R.id.tv_footer)
        val pbFooter: ProgressBar = itemView.findViewById(R.id.pb_footer)
    }

    // 1. 根据position返回条目类型（多布局关键）
    override fun getItemViewType(position: Int): Int {
        // 最后一条固定是底部脚布局
        return if (position == itemCount - 1) TYPE_FOOT else dataList[position].itemType
    }

    // 2. 根据type创建不同布局ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_NORMAL -> {
                val view = inflater.inflate(R.layout.item_memo_new, parent, false)
                NormalVH(view)
            }
            TYPE_AD -> {
                val view = inflater.inflate(R.layout.item_memo_ad, parent, false)
                AdVH(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_footer_load, parent, false)
                FootVH(view)
            }
        }
    }

    // 3. 绑定数据，区分类型赋值
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NormalVH -> {
                val memo = dataList[position]
                holder.tvTitle.text = memo.title
                holder.tvContent.text = memo.content
                holder.tvTime.text = memo.createTime
                // 点击事件：编辑备忘录
                holder.itemView.setOnClickListener { itemClick(memo) }
                // 长按事件：删除备忘录弹窗
                holder.itemView.setOnLongClickListener { longClick(memo); true }
            }
            is AdVH -> {
                // 广告条目无需额外操作，文案在XML中已设置
            }
            is FootVH -> {
                // 修改底部文案和动画：无更多/加载中
                if (hasMore) {
                    holder.tvFooter.text = "正在加载更多..."
                    holder.pbFooter.visibility = View.VISIBLE
                } else {
                    holder.tvFooter.text = "没有更多备忘录了"
                    holder.pbFooter.visibility = View.GONE
                }
            }
        }
    }

    // 总数 = 数据数量 + 1条底部脚布局
    override fun getItemCount() = dataList.size + 1

    /**
     * 下拉刷新：替换全量数据
     * @param newList 新的数据列表
     */
    fun refreshAll(newList: MutableList<Memo>) {
        dataList = newList
        notifyDataSetChanged()
    }

    /**
     * 上拉加载：追加数据
     * @param addList 追加的数据列表
     */
    fun addMoreData(addList: MutableList<Memo>) {
        val oldSize = dataList.size
        dataList.addAll(addList)
        notifyItemRangeInserted(oldSize, addList.size)
    }
}