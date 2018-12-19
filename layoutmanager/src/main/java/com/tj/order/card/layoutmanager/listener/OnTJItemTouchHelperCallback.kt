package com.tj.order.card.layoutmanager.listener

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView

/**
 * 作者:TJbaobao
 * 时间:2018/12/18  15:53
 * 说明:
 * 使用：
 */
interface OnTJItemTouchHelperCallback {

    fun onChildDraw(canvas:Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,touchState:Int)

    fun onFlingEnd(holder: RecyclerView.ViewHolder, position: Int)

    fun onFlingUp(holder: RecyclerView.ViewHolder,velocityX: Float, velocityY: Float):Boolean{
        return true
    }

    fun onFling(holder: RecyclerView.ViewHolder,speedX: Float, speedY: Float, offsetX: Float, offsetY: Float){

    }

    fun onResetEnd(holder: RecyclerView.ViewHolder){

    }

    fun onLastCardEnd(holder: RecyclerView.ViewHolder){

    }

    /**
     * @param orientation 0左边 1右边
     *
     */
    fun onFlingEdge(orientation: Int){

    }
}