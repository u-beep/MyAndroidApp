package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.bean.NewsItem

/**
 * 新闻列表适配器
 *
 * 知识点：
 *   - RecyclerView.Adapter：列表适配器基类
 *   - ViewHolder：持有条目视图引用，避免重复findViewById
 *   - notifyDataSetChanged()：刷新全部数据
 *
 * 使用方式：
 *   val adapter = NewsAdapter(mutableListOf())
 *   rv.adapter = adapter
 *   adapter.refreshData(newsList)
 */
class NewsAdapter(var list: MutableList<NewsItem>) : RecyclerView.Adapter<NewsAdapter.VH>() {

    // ViewHolder：持有条目视图的引用，避免重复findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvInfo: TextView = itemView.findViewById(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.tvTitle.text = item.title
        holder.tvInfo.text = "${item.source} | ${item.time}"
    }

    override fun getItemCount(): Int = list.size

    /**
     * 刷新数据
     * 清空旧数据，填充新数据，通知列表刷新
     */
    fun refreshData(newList: List<NewsItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
