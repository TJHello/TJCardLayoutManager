package com.tj.order.card.layoutmanager.listener

import android.view.View

/**
 * 作者:TJbaobao
 * 时间:2019/1/5  17:54
 * 说明:
 * 使用：
 */
interface OnManagerLayoutListener{

    /**
     * @param fraction 移动的百分比 0f-1f
     * @param index 所处位置 上到下==>0开始
     */
    fun onChildDraw(childView: View,fraction : Float,index:Int)



}