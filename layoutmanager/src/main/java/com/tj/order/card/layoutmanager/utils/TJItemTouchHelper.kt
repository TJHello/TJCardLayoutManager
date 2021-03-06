package com.tj.order.card.layoutmanager.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.support.v7.recyclerview.R
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import com.tj.order.card.layoutmanager.listener.OnTJGestureListener
import com.tj.order.card.layoutmanager.listener.OnTJItemTouchHelperCallback
import com.tj.order.card.layoutmanager.listener.TJAnimatorListener
import kotlin.math.abs
import kotlin.math.max

@Suppress("ProtectedInFinal")
/**
 * 作者:TJbaobao
 * 时间:2018/12/18  10:44
 * 说明:
 * 使用：
 */
class TJItemTouchHelper(private val callback:OnTJItemTouchHelperCallback) : RecyclerView.ItemDecoration() {

    companion object {
        const val TOUCH_STATE_BASE = 0
        const val TOUCH_STATE_MOVE = 1
        const val TOUCH_STATE_NEXT = 2
        const val TOUCH_STATE_RESET = 3
        const val TOUCH_STATE_LAST = 4
        const val EDGE_WEIGHT = 0.1f
    }

    protected var mRecyclerView : RecyclerView ?= null
    private val onItemTouchListener : OnItemTouchListener = OnItemTouchListener()
    private val onChildAttachStateChangeListener : OnChildAttachStateChangeListener = OnChildAttachStateChangeListener()
    private val isTouchOnlyTop = true
    private val animHolderList = mutableListOf<HolderAnim>()
    private var touchState = 0
    private var isTouchEdge = false

    //一些变量
    private var selectHolder : HolderAnim ?= null
    private var selectPosition = -1

    //一些工具
    private lateinit var gestureUtil : TJGestureUtil

    //一些系统的一些滑动参数
    private var mSwipeEscapeVelocity: Float = 0f
    private var mMaxSwipeVelocity: Float = 0f

    @SuppressLint("PrivateResource")
    fun attachToRecyclerView(recyclerView : RecyclerView){
        if(mRecyclerView!=recyclerView){
            if(mRecyclerView!=null){
                //清除回调接口
                destroyCallbacks()
            }
        }
        mRecyclerView = recyclerView
        if(mRecyclerView!=null){
            val resources = recyclerView.resources
            this.mSwipeEscapeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity)
            this.mMaxSwipeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity)
            gestureUtil = TJGestureUtil(recyclerView.context)
            gestureUtil.onGestureListener = OnGestureListener()
            setupCallbacks()
        }
    }

    private inner class OnItemTouchListener : RecyclerView.SimpleOnItemTouchListener() {

        private var firstX = 0f
        private var firstY = 0f

        override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {
            if(isTouchEdge) return
            gestureUtil.onTouchEvent(event)
            rv.invalidate()
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
            if(isTouchEdge) return true
            gestureUtil.onTouchEvent(event)
            rv.invalidate()
            val x = event.x
            val y = event.y
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    firstX = x
                    firstY = y
//                    return false
                }
                MotionEvent.ACTION_MOVE->{
                    val moveX = x-firstX
                    val moveY = y-firstY
                   if(abs(moveX)>gestureUtil.scaleTouchSlop|| abs(moveY)>gestureUtil.scaleTouchSlop){
                       return true
                   }
                }
                MotionEvent.ACTION_UP->{
                    firstX = 0f
                    firstY = 0f
//                    return false
                }
            }
            return super.onInterceptTouchEvent(rv, event)||selectHolder==null
        }
    }

    private inner class OnTJTouchListener : View.OnTouchListener{
        private var firstX = 0f
        private var firstY = 0f
        private var isReset = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    firstX = x
                    firstY = y
                    isReset = false
                    isTouchEdge = isTouchEdge(event)
                }
                MotionEvent.ACTION_MOVE->{
                    val moveX = x-firstX
//                    val moveY = y-firstY
                    if(isTouchEdge){
                        if(Math.abs(moveX)>100){
                            if(!isReset){
                                isReset = true
                                val orientation = if(moveX>0) 0 else 1
                                if(animHolderList.size==0||touchState== TOUCH_STATE_LAST)
                                {
                                    callback.onFlingEdge(orientation)
                                }
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{
                    firstX = 0f
                    firstY = 0f
                    if(isTouchEdge){
                        isReset = false
                        isTouchEdge = false
                        return true
                    }
                }
            }
            return isTouchEdge
        }
    }

    private inner class OnGestureListener : OnTJGestureListener() {

        override fun onSingleMove(firstX: Float, firstY: Float, offsetX: Float, offsetY: Float) {
            selectHolder?.let {
                if(!it.isRunning){
                    it.viewHolder.itemView.translationX+=offsetX
                    it.viewHolder.itemView.translationY+=offsetY
                }
            }
//            touchState = TOUCH_STATE_BASE
        }

        override fun onDown(x: Float, y: Float) {
            super.onDown(x, y)
            val holder = findHolder(x,y)
            if(selectHolder==null){
                selectHolder = if(holder==null){
                    null
                }else{
                    HolderAnim(holder)
                }
            }else{
                if(holder==null){
                    if(!selectHolder!!.isRunning||touchState == TOUCH_STATE_LAST){
                        selectHolder = null
                    }
                }else{
                    selectHolder = HolderAnim(holder)
                }
            }
        }

        override fun onFling(speedX: Float, speedY: Float, offsetX: Float, offsetY: Float) {
            super.onFling(speedX, speedY, offsetX, offsetY)

            selectHolder?.let {
                if(!it.isRunning){
                    callback.onFling(it.viewHolder,speedX,speedY,offsetX,offsetY)
                }
            }
        }

        override fun onFlingUp(velocityX: Float, velocityY: Float) {
//            LogUtil.i("velocityX=$velocityX,velocityY=$velocityY")
            selectHolder?.let {
                if(!it.isRunning){
                    val result = callback.onFlingUp(it.viewHolder,velocityX,velocityY)
                    if(!result){
                        resetCard(it.viewHolder)
                    }
                }
            }
        }

        override fun onFlingEnd(type: Int) {
            super.onFlingEnd(type)
        }

        override fun onTouchUp(type: Int) {
            if(type==TJGestureUtil.MOVE_TYPE_SINGLE){
                selectHolder?.let {
                    val result = callback.onFlingUp(it.viewHolder,0f,0f)
                    if(!result){
                        resetCard(it.viewHolder)
                    }
                }
            }
        }

    }

    private fun containsAnimHolder(holder: RecyclerView.ViewHolder?):Boolean{
        if(holder==null) return false
        return containsAnimHolder(holder.itemView)
    }

    private fun containsAnimHolder(view:View?):Boolean{
        if(view==null) return false
        for(i in 0 until animHolderList.size){
            val holderAnim = animHolderList[i]
            if(holderAnim.viewHolder.itemView==view){
                return true
            }
        }
        return false
    }

    private inner class OnChildAttachStateChangeListener : RecyclerView.OnChildAttachStateChangeListener{
        override fun onChildViewDetachedFromWindow(view: View) {
        }

        override fun onChildViewAttachedToWindow(view: View) {

        }
    }

    private inner class TJValueAnimator(private val holderAnim: HolderAnim,toPointX:Float,toPointY:Float,duration: Long=380,interpolator:Interpolator?=null,function:()->Unit={}){
        private val view = holderAnim.viewHolder.itemView
        private val fromPointF = PointF(view.translationX,view.translationY)
        private val toPointF = PointF(toPointX,toPointY)
        private val valueAnimator = ValueAnimator.ofObject(PointFEvaluator(),fromPointF,toPointF)
        init {
            holderAnim.isRunning = true
            valueAnimator.duration = duration
            if(interpolator!=null)
            {
                valueAnimator.interpolator = interpolator
            }
            valueAnimator.addUpdateListener {
                val pointF = it.animatedValue as PointF
                view.translationX = pointF.x
                view.translationY = pointF.y
                selectHolder?.let {viewHolder ->
                    if(view==viewHolder.viewHolder.itemView)
                    {
                        mRecyclerView?.invalidate()
                    }
                }
            }
            valueAnimator.addListener(object :TJAnimatorListener{
                override fun onAnimationEnd(animation: Animator?) {
                    valueAnimator.cancel()
                    holderAnim.isRunning = false
                    function()
                }
            })
        }

        fun start(){
            valueAnimator.start()
        }

    }

    private inner class HolderAnim(viewHolder: RecyclerView.ViewHolder){
        var isRunning = false
        var viewHolder = viewHolder
    }

    //region===================================ItemDecoration方法=================================

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        selectHolder?.let {
            callback.onChildDraw(c,parent,it.viewHolder,touchState)
        }
    }

    //endregion

    private fun destroyCallbacks(){
        mRecyclerView?.removeItemDecoration(this)
        mRecyclerView?.removeOnItemTouchListener(onItemTouchListener)
        mRecyclerView?.removeOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
    }

    private fun setupCallbacks(){
        mRecyclerView?.setOnTouchListener(OnTJTouchListener())
        mRecyclerView?.addItemDecoration(this)
        mRecyclerView?.addOnItemTouchListener(onItemTouchListener)
        mRecyclerView?.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener)
    }
    
    //region===================================业务逻辑=================================

    private fun findHolder(x:Float,y:Float):RecyclerView.ViewHolder?{
        if(touchState== TOUCH_STATE_LAST) return null
        for(i in mRecyclerView!!.childCount-1 downTo 0 ){
            val view = mRecyclerView!!.getChildAt(i)
            if(!containsAnimHolder(view)){
                if(!isTouchOnlyTop||i== mRecyclerView!!.childCount-1-animHolderList.size){
                    if(checkViewEvent(view,x,y)){
                        selectPosition = mRecyclerView!!.getChildAdapterPosition(view)
                        return mRecyclerView!!.getChildViewHolder(view)
                    }
                }
            }
        }
        return null
    }

    private fun checkViewEvent(child: View,x:Float,y:Float):Boolean{
        val translationX = child.translationX
        val translationY = child.translationY
        return x >= child.left.toFloat() + translationX && x <= child.right.toFloat() + translationX
                && y >= child.top.toFloat() + translationY && y <= child.bottom.toFloat() + translationY
    }

    private fun isTouchEdge(event: MotionEvent):Boolean{
        val parentWidth = mRecyclerView!!.width
        val maxOffset = parentWidth*EDGE_WEIGHT
        return event.x<maxOffset||event.x>parentWidth- maxOffset
    }

    fun nextCard(holder:RecyclerView.ViewHolder,xVel:Float=1f,yVel: Float=0f)
    {
        touchState = TOUCH_STATE_NEXT
        val toPosition = getToPosition(holder.itemView,xVel,yVel)
        selectHolder?.let {
            animHolderList.add(it)
            TJValueAnimator(it, toPosition[0].toFloat(), toPosition[1].toFloat(), 380) {
                animHolderList.remove(it)
                callback.onFlingEnd(holder,selectPosition)
                if(animHolderList.size==0){
                    touchState = TOUCH_STATE_BASE
                }
            }.start()
        }
    }

    fun resetCard(holder:RecyclerView.ViewHolder)
    {
        touchState = TOUCH_STATE_RESET
        TJValueAnimator(HolderAnim(holder), 0f,0f, 380,OvershootInterpolator()){
            callback.onResetEnd(holder)
            touchState = TOUCH_STATE_BASE
        }.start()
    }

    fun lastCard(view: View,orientation: Int){
        selectHolder = HolderAnim(mRecyclerView!!.getChildViewHolder(view))
        selectHolder?.let {
            animHolderList.add(it)
            val itemWidth = mRecyclerView!!.layoutManager!!.getDecoratedMeasuredWidth(view)
            val itemHeight = mRecyclerView!!.layoutManager!!.getDecoratedMeasuredHeight(view)
            if(orientation==0){
                view.translationX = -view.left-itemWidth.toFloat()
            }else{
                view.translationX =  mRecyclerView!!.width.toFloat()-view.left
            }
            touchState = TOUCH_STATE_LAST
            TJValueAnimator(it, 0f,0f, 380,DecelerateInterpolator()){
                animHolderList.remove(it)
                callback.onLastCardEnd(it.viewHolder)
                if(animHolderList.size==0)
                {
                    touchState = TOUCH_STATE_BASE
                }
            }.start()
        }
    }

    private fun getToPosition(view: View,xVel:Float,yVel:Float) : DoubleArray
    {
        val c = Math.sqrt((xVel*xVel+yVel*yVel).toDouble())
        val r = mRecyclerView!!.width/2f+view.width.toFloat()
        val ratio = r/c
        val toX = xVel*ratio
        val toY = yVel*ratio
        return doubleArrayOf(toX,toY)
    }

    private fun findHolderAnim(holder: RecyclerView.ViewHolder):HolderAnim{
        for(i in 0..animHolderList.size){
            val holderAnim = animHolderList[i]
            if(holderAnim.viewHolder==holder){
                return holderAnim
            }
        }
        return HolderAnim(holder)
    }

    //endregion

}