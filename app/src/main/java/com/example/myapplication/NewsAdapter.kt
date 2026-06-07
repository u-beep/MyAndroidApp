package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.bean.NewsItem

/**
 * 新闻列表适配器（翻页模式）
 *
 * 底脚三种状态：
 *   STATE_MORE    → 还有下一页（"↓ 下滑查看更多"）
 *   STATE_LOADING → 正在加载中（转圈 + "加载中..."）
 *   STATE_LAST    → 最后一页（"—— 已经是最后了 ——"
 */
class NewsAdapter(var list: MutableList<NewsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    val STATE_MORE = 0       // 有更多，提示下滑
    val STATE_LOADING = 1    // 正在加载
    val STATE_LAST = 2       // 最后一页
    private var _footerState = STATE_LOADING
    val footerState: Int get() = _footerState

    var currentPage = 1
    var itemClick: ((NewsItem) -> Unit)? = null

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvInfo: TextView = itemView.findViewById(R.id.tv_info)
    }

    inner class FooterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pbLoading: ProgressBar = itemView.findViewById(R.id.pb_loading)
        val tvFooter: TextView = itemView.findViewById(R.id.tv_footer)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOOTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            FooterVH(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            VH(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FooterVH) {
            when (_footerState) {
                STATE_MORE -> {
                    holder.pbLoading.visibility = View.GONE
                    holder.tvFooter.text = "↓ 下滑查看更多"
                }
                STATE_LOADING -> {
                    holder.pbLoading.visibility = View.VISIBLE
                    holder.tvFooter.text = "加载中..."
                }
                STATE_LAST -> {
                    holder.pbLoading.visibility = View.GONE
                    holder.tvFooter.text = "—— 已经是最后了 ——"
                }
            }
        } else if (holder is VH) {
            val item = list[position]
            holder.tvTitle.text = item.title
            holder.tvInfo.text = "${item.source} | ${item.ctime}"
            holder.itemView.setOnClickListener { itemClick?.invoke(item) }
        }
    }

    override fun getItemCount(): Int = if (list.isEmpty()) 0 else list.size + 1

    /**
     * 替换整页数据，默认底脚显示"下滑查看更多"
     */
    fun refreshData(newList: List<NewsItem>, page: Int) {
        currentPage = page
        list.clear()
        list.addAll(newList)
        _footerState = STATE_MORE
        notifyDataSetChanged()
    }

    fun setFooterState(state: Int) {
        _footerState = state
        if (list.isNotEmpty()) notifyItemChanged(list.size)
    }
}
