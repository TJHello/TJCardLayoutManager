package com.tj.order.card.layoutmanager.listener

import android.support.v7.widget.RecyclerView

/**
 * 作者:TJbaobao
 * 时间:2018/12/19  16:15
 * 说明:
 * 使用：
 */
interface OnCardViewListener {

    fun onNexCardBegin(holder: RecyclerView.ViewHolder,position:Int)

    fun onLastCardBegin(holder: RecyclerView.ViewHolder,position:Int)

    fun onNexCardEnd(holder: RecyclerView.ViewHolder,position:Int){}

    fun onLastCardEnd(holder: RecyclerView.ViewHolder,position:Int){}

    fun onNoData(){

    }
}