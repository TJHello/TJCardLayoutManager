package com.tj.order.card.layoutmanager.utils

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import com.tj.order.card.layoutmanager.TJCardLayoutManager
import com.tj.order.card.layoutmanager.listener.OnCardViewListener
import com.tj.order.card.layoutmanager.listener.OnTJItemTouchHelperCallback


/**
 * 作者:TJbaobao
 * 时间:2018/12/19  14:41
 * 说明:
 * 使用：
 */
class TJCardFlingManager(private val recyclerView: RecyclerView,private val onCardViewListener: OnCardViewListener) {

    val layoutManager = TJCardLayoutManager()
    val helper = TJItemTouchHelper(Callback())

    init {
        recyclerView.layoutManager = layoutManager
        helper.attachToRecyclerView(recyclerView)
    }

    private inner class Callback : OnTJItemTouchHelperCallback {

        override fun onFlingUp(holder: RecyclerView.ViewHolder, velocityX: Float, velocityY: Float):Boolean {
            val childView = holder.itemView
            val moveProgress = Math.sqrt((childView.translationX * childView.translationX + childView.translationY * childView.translationY).toDouble()).toFloat()
            val fraction = moveProgress / (recyclerView.width*0.4f)
            if(Math.abs(velocityX)>500||Math.abs(velocityY)>500) {
                helper.nextCard(holder,velocityX,velocityY)
                onCardViewListener.onNexCardBegin()
                return true
            }else if(fraction>=1){
                helper.nextCard(holder,childView.translationX,childView.translationY)
                onCardViewListener.onNexCardBegin()
                return true
            }
            return false
        }

        override fun onFlingEnd(holder: RecyclerView.ViewHolder, position: Int) {
            val result = layoutManager.nextCard(holder.itemView)
            onCardViewListener.onNexCardEnd()
            if(!result){
                onCardViewListener.onNoData()
            }
        }

        override fun onFlingEdge(orientation: Int) {
            val view = layoutManager.lastCard()
            if(view!=null){
                helper.lastCard(view,orientation)
            }
        }

        override fun onLastCardEnd(holder: RecyclerView.ViewHolder) {
            onCardViewListener.onLastCardEnd()
        }

        override fun onResetEnd(holder: RecyclerView.ViewHolder) {

        }

        override fun onChildDraw(
            canvas: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            touchState:Int
        )
        {
            val childView = viewHolder.itemView
            val childPosition = recyclerView.getChildAdapterPosition(childView)
            val moveProgress = Math.sqrt((childView.translationX * childView.translationX + childView.translationY * childView.translationY).toDouble()).toFloat()
            var fraction = moveProgress / (recyclerView.width*0.4f)
            if (fraction > 1) {
                fraction = 1f
            }
            layoutManager.refresh(fraction,childPosition-layoutManager.getTopPosition())
            if(touchState==TJItemTouchHelper.TOUCH_STATE_NEXT){
                layoutManager.setAnimPosition(childPosition)
            }
        }
    }

}