package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager2 + Fragment 适配器
 *
 * 知识点：
 *   - FragmentStateAdapter：ViewPager2专用的Fragment适配器
 *   - 继承FragmentStateAdapter，只需实现2个方法：
 *     1. getItemCount()：返回页面数量
 *     2. createFragment()：根据位置创建对应的Fragment
 *   - ViewPager2默认预加载相邻页面（左右各1页）
 *
 * 使用方式：
 *   val frags = listOf(homeFrag, userFrag, mineFrag)
 *   val adapter = FragVPAdapter(this, frags)
 *   binding.viewPager.adapter = adapter
 */
class FragVPAdapter(
    act: FragmentActivity,
    private val fragList: List<Fragment>
) : FragmentStateAdapter(act) {

    // 返回Fragment总数
    override fun getItemCount(): Int = fragList.size

    // 根据位置返回对应的Fragment实例
    override fun createFragment(position: Int): Fragment = fragList[position]
}
