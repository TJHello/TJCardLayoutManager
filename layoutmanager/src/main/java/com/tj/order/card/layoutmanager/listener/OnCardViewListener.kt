package com.tj.order.card.layoutmanager.listener

/**
 * 作者:TJbaobao
 * 时间:2018/12/19  16:15
 * 说明:
 * 使用：
 */
interface OnCardViewListener {

    fun onNexCardBegin()

    fun onLastCardBegin()

    fun onNexCardEnd(){}

    fun onLastCardEnd(){}

    fun onNoData(){

    }
}