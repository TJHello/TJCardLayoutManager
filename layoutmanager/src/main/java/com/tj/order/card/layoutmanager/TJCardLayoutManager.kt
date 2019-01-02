package com.tj.order.card.layoutmanager

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.tj.order.card.layoutmanager.utils.LogUtil

@Suppress("MemberVisibilityCanBePrivate")
/**
 * 作者:TJbaobao
 * 时间:2018/12/18  10:30
 * 说明:
 * 使用：
 */
class TJCardLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        const val CARD_SHOW_NUM = 4
        const val CARD_SCALE = 0.08f
        const val CARD_TRANS_Y = 30
        const val CARD_TRANS_Y_WEIGHT = 0.02f
        const val ORIENTATION_TOP = 0//向上凸出
        const val ORIENTATION_BOTTOM = 1//向下凸出
        const val TRANS_Y_MODE_WEIGHT = 0//Y轴偏移模式-根据item高度比例偏移
        const val TRANS_Y_MODE_FIXED = 1//Y轴偏移模式-根据固定值偏移

    }

    var cardShowNum = CARD_SHOW_NUM
    var cardScale = CARD_SCALE
    var cardTransY = CARD_TRANS_Y
    var cardTransYWeight = CARD_TRANS_Y_WEIGHT
    var cacheNum = cardShowNum + 4
    var orientation = ORIENTATION_BOTTOM
    var transYMode = TRANS_Y_MODE_WEIGHT

    private var positionTop = 0
    private var positionAnim = -1
    private var recycler: RecyclerView.Recycler? = null
    private var isFistLayoutChildren = false
    val hashCodeList = mutableListOf<Int>()

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }
        this.recycler = recycler
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if(child!=null){
                val position = (child.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                if(position>=0){
                    if(position<positionTop||position>=positionTop+cacheNum){
                        removeAndRecycleView(child,recycler)
                    }
                }
            }
        }
        detachAndScrapAttachedViews(recycler)
        var index = positionTop + cacheNum - 1
        for (i in positionTop + cacheNum - 1 downTo positionTop) {
            if (i >= itemCount) return
            val view = recycler.getViewForPosition(i)
//            if(hashCodeList.contains(view.hashCode())){
//                if(view.tag!=i){
//                    LogUtil.i("已经存在")
//                    detachAndScrapView(view,recycler)
//                    removeAndRecycleView(view,recycler)
//                    view = recycler.getViewForPosition(i)
//                }
//            }
            if (isOutScreen(view)) {
                view.translationX = 0f
                view.translationY = 0f
                view.alpha = 0f
                view.scaleX = 1f
                view.scaleY = 1f
                removeAndRecycleView(view, recycler)
                continue
            }
            addView(view)
            view.tag = i
//            if(!hashCodeList.contains(view.hashCode())){
//                hashCodeList.add(view.hashCode())
//            }
            measureChildWithMargins(view, 0, 0)
            if (i > positionAnim) {
                val itemWidth = getDecoratedMeasuredWidth(view)
                val itemHeight = getDecoratedMeasuredHeight(view)
                val left = (width - itemWidth) / 2
                val top = (height - itemHeight) / 2
                layoutDecorated(view, left, top, left + itemWidth, top + itemHeight)
                val level = index - positionTop
                if (level >= CARD_SHOW_NUM) {
                    view.alpha = 0f
                } else {
                    view.alpha = 1f
                }
                val scale = 1f - level * cardScale
                val fixTranY = if (transYMode == TRANS_Y_MODE_FIXED) {
                    cardTransY.toFloat()
                } else {
                    cardTransYWeight * itemHeight.toFloat()
                }
                val translationY = if (orientation == ORIENTATION_BOTTOM) {
                    level * fixTranY + (1f - scale) * 0.5f * itemHeight
                } else {
                    -level * fixTranY - (1f - scale) * 0.5f * itemHeight
                }
                view.scaleX = scale
                view.scaleY = scale
                view.translationY = translationY
                view.translationX = 0f
                index--
            }
        }
//        for (i in 0 until childCount) {
//            val view = getChildAt(i)
//            if (view != null) {
//                if (isOutScreen(view)) {
//                    removeAndRecycleViewAt(i, recycler)
//                    view.translationX = 0f
//                    view.translationY = 0f
//                    view.alpha = 0f
//                    view.scaleX = 1f
//                    view.scaleY = 1f
//                }
//            }
//        }
        isFistLayoutChildren = false
    }

    private fun isOutScreen(view: View): Boolean {
        if (view.left == 0) return false
        val x = view.left + view.translationX
        val y = view.top + view.translationY
        val viewWidth = view.width
        val viewHeight = view.height
        return x > width || y > height || x + viewWidth < 0 || y + viewHeight < 0
    }

    /**
     * 下一张卡片,返回true代表操作生成，返回false代表即将缺少数据，请补充
     */
    fun nextCard(view: View): Boolean {
        positionTop++
        view.alpha = 0f
        view.translationX = 0f
        view.translationY = 0f
        view.scaleX = 1f
        view.scaleY = 1f
//        hashCodeList.remove(view.hashCode())
        removeAndRecycleView(view, recycler)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if(child!=null){
                val position = (child.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                if(position>=0){
                    if(position<positionTop||position>=positionTop+cacheNum){
//                        LogUtil.i("不在范围内，回收：$position,$positionTop")
                        removeAndRecycleView(child,recycler)
                    }
                }
            }
        }
        return positionTop < itemCount - cacheNum
    }

    fun lastCard(): View? {
        if (positionTop > 0) {
            positionTop--
            val childFirst = getChildAt(0)
            if(childFirst!=null){
                removeAndRecycleView(childFirst,recycler)
            }
            val view = recycler!!.getViewForPosition(positionTop)
//            if(!hashCodeList.contains(view.hashCode())){
//                hashCodeList.add(view.hashCode())
//            }
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(view)
            val itemHeight = getDecoratedMeasuredHeight(view)
            val left = (width - itemWidth) / 2
            val top = (height - itemHeight) / 2
            layoutDecorated(view, left, top, left + itemWidth, top + itemHeight)
            view.alpha = 1f
            view.translationX = width.toFloat() - left
            view.translationY = 0f
            view.scaleX = 1f
            view.scaleY = 1f
            return view
        }
        return null
    }

    fun refresh(progress: Float, index: Int = 0) {
        if (index < 0) return
//        LogUtil.i("refresh:progress=$progress,index=$index")
        for (i in 0 until childCount) {
            if (i >= itemCount) return
            val view = getChildAt(i)
            val level = childCount - i - 1 - index
            if (i < childCount - 1 - index) {
                if (level <= CARD_SHOW_NUM) {
                    if (level == CARD_SHOW_NUM) {
                        view.alpha = progress
                    } else {
                        view.alpha = 1f
                    }
                    val scale = 1f - level * CARD_SCALE + progress * CARD_SCALE
                    view.scaleX = scale
                    view.scaleY = scale
                    val itemHeight = view.height
                    val fixTranY = if (transYMode == TRANS_Y_MODE_FIXED) {
                        cardTransY.toFloat()
                    } else {
                        cardTransYWeight * itemHeight.toFloat()
                    }
                    val translationY = if (orientation == ORIENTATION_BOTTOM) {
                        level * fixTranY + (1f - scale) * 0.5f * itemHeight - progress * fixTranY
                    } else {
                        -level * fixTranY - (1f - scale) * 0.5f * itemHeight + progress * fixTranY
                    }
                    view.translationY = translationY
                } else {
                    view.alpha = 0f
                }
            }
        }
    }

    fun getTopPosition(): Int {
        return positionTop
    }

    fun setAnimPosition(position: Int) {
        positionAnim = position
    }

}