package com.tj.order.card.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tj.order.card.R
import kotlinx.android.synthetic.main.main_activity_list_item_layout.view.*

/**
 * 作者:TJbaobao
 * 时间:2018/12/18  15:13
 * 说明:
 * 使用：
 */
class MainAdapter<Info>(private val list:MutableList<Info>) : RecyclerView.Adapter<MainAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_activity_list_item_layout,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.tvTest.text = "测试$position"
    }


    class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView)
}