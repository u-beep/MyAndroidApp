package com.example.myapplication

// LayoutInflater：用来把XML布局文件"吹"成真正的View对象
import android.view.LayoutInflater
// View：所有控件的基类
import android.view.View
// ViewGroup：View的容器（如LinearLayout、RecyclerView等）
import android.view.ViewGroup
// TextView：文字显示控件
import android.widget.TextView
// RecyclerView：安卓最核心的列表控件
import androidx.recyclerview.widget.RecyclerView

/**
 * 用户列表适配器（核心桥梁）
 *
 * 作用：把"用户数据集合"和"列表条目布局"连接起来
 *   - 数据源：MutableList<User>（所有注册用户）
 *   - 条目布局：item_user.xml（每条数据长什么样）
 *
 * 三要素：
 *   1. onCreateViewHolder → 创建一条条目的View
 *   2. onBindViewHolder     → 把数据填进条目的控件
 *   3. getItemCount         → 告诉列表总共有几条数据
 *
 * @param list 用户数据集合，从外部传入
 */
class UserAdapter(var list: MutableList<User>) : RecyclerView.Adapter<UserAdapter.UserVH>() {

    /**
     * 条目点击回调接口
     * 用法：adapter.itemClick = { user -> ... }
     * 当用户点击某一条列表项时触发
     */
    var itemClick: ((User) -> Unit)? = null

    /**
     * 条目长按回调接口
     * 用法：adapter.longClick = { user -> ... }
     * 当用户长按某一条列表项时触发
     * 用于弹出删除确认弹窗等操作
     */
    var longClick: ((User) -> Unit)? = null

    /**
     * ViewHolder（视图持有者）
     * 作用：缓存一条条目中的所有控件，避免每次都findViewById
     *
     * 为什么需要ViewHolder？
     *   - RecyclerView滚动时，条目会被复用（滚出屏幕的条目给新条目用）
     *   - ViewHolder把控件引用保存下来，复用时只需更新文字，不用重新查找控件
     *   - 这样滚动非常流畅，不卡顿
     */
    inner class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 在ViewHolder创建时，就把所有TextView找出来缓存
        val tvAcc: TextView = itemView.findViewById(R.id.tv_acc)       // 账号
        val tvSex: TextView = itemView.findViewById(R.id.tv_sex)       // 性别
        val tvHobby: TextView = itemView.findViewById(R.id.tv_hobby)   // 爱好
        val tvCity: TextView = itemView.findViewById(R.id.tv_city)     // 城市
    }

    /**
     * 第1步：创建条目视图（ViewHolder）
     *
     * 什么时候调用？列表需要新的条目来显示数据时
     * 做什么？把item_user.xml"吹"成View，包进ViewHolder
     *
     * @param parent   列表容器（RecyclerView自身）
     * @param viewType 条目类型（本例只有一种，用不到）
     * @return UserVH  包含条目视图的ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        // LayoutInflater.from(parent.context)：获取布局"吹风机"
        // inflate(R.layout.item_user, parent, false)：
        //   把item_user.xml变成View对象
        //   第3个参数false = 不立即添加到parent中（RecyclerView会自己管理）
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserVH(view)
    }

    /**
     * 第2步：绑定数据到条目（核心！）
     *
     * 什么时候调用？条目要显示数据时（包括复用时）
     * 做什么？从数据集合中取出对应位置的数据，填入条目的控件
     *
     * @param holder   包含条目控件的ViewHolder
     * @param position 当前条目在列表中的位置（从0开始）
     */
    override fun onBindViewHolder(holder: UserVH, position: Int) {
        // 从集合中取出当前位置的用户数据
        val user = list[position]

        // 把用户数据设置到对应的TextView上
        holder.tvAcc.text = "账号：${user.account}"     // 例如"账号：zhangsan"
        holder.tvSex.text = "性别：${user.sex}"         // 例如"性别：男"
        holder.tvHobby.text = "爱好：${user.hobby}"     // 例如"爱好：篮球 看书"
        holder.tvCity.text = "城市：${user.city}"       // 例如"城市：北京"

        // ============================================
        // 条目点击事件
        // 给整条条目设置点击监听
        // 点击后触发itemClick回调，把当前用户数据传出去
        // ============================================
        holder.itemView.setOnClickListener {
            itemClick?.invoke(user)
        }

        // ============================================
        // 条目长按事件
        // 长按条目触发longClick回调
        // 用于弹出删除确认弹窗
        // setOnLongClickListener返回true表示消费了长按事件
        // ============================================
        holder.itemView.setOnLongClickListener {
            longClick?.invoke(user)
            true
        }
    }

    /**
     * 第3步：返回数据总数
     *
     * 列表需要知道一共有多少条数据，才能正确滚动和显示
     * @return 数据集合的大小
     */
    override fun getItemCount(): Int = list.size
}
