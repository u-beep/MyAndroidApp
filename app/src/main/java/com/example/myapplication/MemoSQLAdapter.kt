package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 备忘录SQLite适配器
 * 
 * 用于绑定Memo数据到item_memo_new.xml布局
 * 支持点击编辑和长按删除功能
 */
class MemoSQLAdapter(var list: MutableList<Memo>) : RecyclerView.Adapter<MemoSQLAdapter.VH>() {
    
    // 条目点击回调：编辑备忘录
    var itemClick: (Memo) -> Unit = {}
    
    // 条目长按回调：删除备忘录弹窗
    var longClick: (Memo) -> Unit = {}

    /**
     * ViewHolder类，缓存布局中的控件
     */
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    /**
     * 创建ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.item_memo_new, parent, false)
        return VH(view)
    }

    /**
     * 绑定数据到ViewHolder
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        
        // 绑定数据到布局
        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
        holder.tvTime.text = item.createTime

        // 点击事件：编辑备忘录
        holder.itemView.setOnClickListener { 
            itemClick(item)
        }
        
        // 长按事件：删除备忘录弹窗
        holder.itemView.setOnLongClickListener {
            longClick(item)
            true // 返回true表示已处理长按事件
        }
    }

    /**
     * 获取列表项数量
     */
    override fun getItemCount() = list.size

    /**
     * 刷新列表数据
     */
    fun refresh(newList: MutableList<Memo>) {
        list = newList
        notifyDataSetChanged()
    }
}