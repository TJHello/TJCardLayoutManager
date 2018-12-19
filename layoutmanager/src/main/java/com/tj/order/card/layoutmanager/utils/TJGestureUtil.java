package com.tj.order.card.layoutmanager.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import com.tj.order.card.layoutmanager.listener.OnTJGestureListener;


/**
 * Created by TJbaobao on 2017/12/26.
 * 手势处理工具类
 *
 * 版本说明:
 *
 * V0.9.0   2017/12/26
 * 初始版本
 *
 *
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class TJGestureUtil {

    //状态变量
    private boolean isSingleTouch = true;
    private int mMinimumFlingVelocity,mMaximumFlingVelocity;

    public static final int MOVE_TYPE_SINGLE = 0;
    public static final int MOVE_TYPE_POINTER = 1;
    private static final int DRAG_MIN_OFFSET = 5;
    private BaseHandler handler = new BaseHandler(new HandlerCallback());
    private int touchSlop ;
    private int scaleTouchSlop ;

    public TJGestureUtil(Context context)
    {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        scaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    private VelocityTracker mVelocityTracker;
    private float firstX1= 0,firstX2 = 0,firstY1 = 0,firstY2 = 0;
    private float firstCenterX = 0,firstCenterY = 0;
    private float firstDistance = 0;
    private float lastMoveX = 0,lastMoveY = 0;
    private boolean isOpenDrag = false;

    public void onTouchEvent(MotionEvent event)
    {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        if(isSingleTouch(event))
        {
            //单指操作
            handleTouchSingleMove(event);
        }
        else
        {
            //多指操作
            handleTouchPointer(event);
        }
    }

    private boolean isSinglePress = false;
    private long downTime = 0;
    private boolean isDrag = false,isMoveLong = false,isQMove = false,isDownLong = false;
    private static final int IS_MOVE_MIN_PX = 5;
    private static final int IS_MOVE_MAX_PX = 10;
    private int IS_DRAG_MOVE_MIN_TIME = 200;
    private static final int IS_DRAG_MOVE_MAX_TIME = 800;

    //处理单指移动
    private void handleTouchSingleMove(MotionEvent event)
    {
        final float x = event.getX();
        final float y = event.getY();
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                isSinglePress = true;
                cancelAnim();
                firstX1 = x ;
                firstY1 = y ;
                lastMoveX = 0;
                lastMoveY = 0;
                downTime = System.currentTimeMillis();
                isDrag = false;
                isMoveLong = false;
                isQMove = false;
                Message msg = handler.obtainMessage();
                msg.what = HandlerCallback.DOWN_LONG;
                Bundle bundle = new Bundle();
                bundle.putFloat("x",x);
                bundle.putFloat("y",y);
                msg.setData(bundle);
                if(mOnGestureListener!=null)
                {
                    mOnGestureListener.onDown(x,y);
                }
                handler.sendMessageDelayed(msg,IS_DRAG_MOVE_MIN_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isSinglePress)
                {
                    float moveX = x-firstX1;
                    float moveY = y-firstY1;
                    float offsetX = moveX-lastMoveX ;
                    float offsetY = moveY-lastMoveY ;
                    if(Math.abs(moveX)>touchSlop||Math.abs(moveY)>touchSlop||isQMove||isMoveLong||isDrag)
                    {
                        lastMoveX = moveX ;
                        lastMoveY = moveY;
                    }
                    else
                    {
                        return;
                    }
                    long moveUserTime = System.currentTimeMillis()-downTime;
                    if(moveUserTime<IS_DRAG_MOVE_MIN_TIME||isQMove)
                    {
                        isQMove = true;
                        //这是快速移动
                        if(mOnGestureListener!=null)
                        {
                            mOnGestureListener.onSingleMove(firstX1,firstY1,offsetX,offsetY,x,y);
                        }
                        handler.removeMessages(HandlerCallback.DOWN_LONG);
                    }
                    else if((moveUserTime>IS_DRAG_MOVE_MIN_TIME&&moveUserTime<IS_DRAG_MOVE_MAX_TIME)||isDrag)
                    {
                        //这是拖动
                        isDrag = true;
                        if(mOnGestureListener!=null)
                        {
                            float ox = Math.abs(offsetX),oy =Math.abs(offsetY) ;
                            float minOffsetD = Math.max(ox/DRAG_MIN_OFFSET,oy/DRAG_MIN_OFFSET);
                            float minXOffset = ox/minOffsetD;
                            float minYOffset = oy/minOffsetD;
                            float tmpX = x-offsetX,tmpY = y-offsetY;
                            do
                            {
                                mOnGestureListener.onDrag(tmpX,tmpY,firstX1,firstY1,0,0);
                                if(ox>=minXOffset)
                                {
                                    ox-=minXOffset;
                                    if(offsetX<0)
                                    {
                                        tmpX-=minXOffset;
                                    }
                                    else if(offsetX>0)
                                    {
                                        tmpX+=minXOffset;
                                    }
                                }
                                if(oy>=minYOffset)
                                {
                                    oy-=minYOffset;
                                    if(offsetY<0)
                                    {
                                        tmpY-=minYOffset;
                                    }
                                    else if(offsetY>0)
                                    {
                                        tmpY+=minYOffset;
                                    }
                                }
                            }while (ox>minXOffset||oy>minYOffset);
                            mOnGestureListener.onDrag(tmpX,tmpY,firstX1,firstY1,offsetX,offsetY);
                        }
                    }
                    else if(moveUserTime>IS_DRAG_MOVE_MAX_TIME||isMoveLong)
                    {
                        //这是长按移动
                        isMoveLong = true;
                        if(mOnGestureListener!=null)
                        {
                            mOnGestureListener.onDownLongMove(x,y,firstX1,firstY1,offsetX,offsetY);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:case MotionEvent.ACTION_CANCEL:
                long moveUserTime = System.currentTimeMillis()-downTime;
                if(isSinglePress)
                {
                    if(Math.abs(lastMoveX)>touchSlop||Math.abs(lastMoveY)>touchSlop)
                    {
                        if(!isDrag||!isOpenDrag)
                        {
                            handler.removeMessages(HandlerCallback.DOWN_LONG);
                        }
                        handleTouchFling(event,MOVE_TYPE_SINGLE);
                    }
                    else
                    {
                        if((Math.abs(lastMoveX)==0&&Math.abs(lastMoveY)==0)&&moveUserTime<IS_DRAG_MOVE_MIN_TIME)
                        {
                            handler.removeMessages(HandlerCallback.DOWN_LONG);
                            if(mOnGestureListener!=null)
                            {
                                mOnGestureListener.onClick(x,y);
                            }
                        }
                        if(mOnGestureListener!=null)
                        {
                            mOnGestureListener.onTouchUp(MOVE_TYPE_SINGLE);
                        }
                    }
                    isSinglePress = false;
                }
                break;
        }
    }

    private boolean isPointerPress = false;
    private void handleTouchPointer(MotionEvent event)
    {
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
        {
            isPointerPress =  true;
            isSinglePress = false;
            cancelAnim();
            firstX1 = x1;
            firstY1 = y1;
            firstX2 = x2;
            firstY2 = y2;
            firstCenterX = (firstX1 + firstX2) / 2f;
            firstCenterY = (firstY1 + firstY2) / 2f;
            firstDistance = (float) Equation.getDistanceBy2Dot(firstX1, firstY1, firstX2, firstY2);
            lastMoveX = 0;
            lastMoveY = 0;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(isPointerPress)
            {
                float centerX = (x1 + x2) / 2f;
                float centerY = (y1 + y2) / 2f;
                float moveX = centerX - firstCenterX, moveY = centerY - firstCenterY;
                float offsetX = moveX-lastMoveX,offsetY = moveY-lastMoveY;
                float distance = (float) Equation.getDistanceBy2Dot(x1, y1, x2, y2);
                if(distance>=scaleTouchSlop)
                {
                    float scale = distance / firstDistance ;
                    lastMoveX = moveX ;
                    lastMoveY = moveY ;
                    firstDistance = distance;
                    if(mOnGestureListener!=null)
                    {
                        mOnGestureListener.onPointerScale(centerX,centerY,offsetX,offsetY,scale,distance);
                    }
                }
            }
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                ||event.getAction()==MotionEvent.ACTION_POINTER_UP
                || event.getAction()==MotionEvent.ACTION_CANCEL)
        {
            if(isPointerPress)
            {
                lastMoveX = 0;
                lastMoveY = 0;
                handleTouchFling(event,MOVE_TYPE_POINTER);
                isPointerPress = false;
            }
        }
    }

    public boolean isFlingRun = true;
    private void handleTouchFling(MotionEvent event, int type)
    {
        final VelocityTracker velocityTracker = mVelocityTracker;
        final int pointerId = event.getPointerId(0);
        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        final float velocityX = velocityTracker.getXVelocity(pointerId);
        final float velocityY = velocityTracker.getYVelocity(pointerId);
        if (Math.abs(velocityX) > mMinimumFlingVelocity || Math.abs(velocityY) > mMinimumFlingVelocity) {
            new FlingAnimator(type,velocityX / 1000, velocityY / 1000,true,true).start();
            if(mOnGestureListener!=null)
            {
                mOnGestureListener.onFlingUp(velocityX,velocityY);
            }
        }else{
            if(mOnGestureListener!=null)
            {
                mOnGestureListener.onTouchUp(MOVE_TYPE_SINGLE);
            }
        }
    }

    private boolean isSingleTouch(MotionEvent event)
    {
        if(event.getPointerCount()==1)
        {
            if(!isSingleTouch)
            {
                firstX1 = event.getX(0);
                firstY1 = event.getY(0);
                lastMoveX = 0;
                lastMoveY = 0;
            }
            isSingleTouch = true;
        }
        else {
            isSingleTouch = false;
        }
        return isSingleTouch;
    }

    //region======================动画专区开始======================
    private ValueAnimator animatorFling;

    private class FlingAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        private int type ;
        private float speedX ,speedY ;
        private boolean isCancel = false;
        private FlingAnimator(int type,float speedX, float speedY ,boolean overBoundaryX, boolean overBoundaryY) {
            this.type = type ;
            this.speedX = speedX ;
            this.speedY = speedY ;
            float speed = (float) Math.sqrt(speedX*speedX+speedY*speedY);
            setObjectValues(new PointF(speedX,speedY),new PointF(0,0));
            setEvaluator(new PointFEvaluator());
            setDuration((long) (speed*100*1));
            setInterpolator(new DecelerateInterpolator());
        }

        public void start() {
            super.addUpdateListener(this);
            super.addListener(this);
            animatorFling = this;
            super.start();
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            float addSpeed = 8.5f;
            float speedTempX = pointF.x*addSpeed;
            float speedTempY = pointF.y*addSpeed;
            if(mOnGestureListener!=null&&!isCancel)
            {
                mOnGestureListener.onFling(speedX,speedY,speedTempX,speedTempY);
            }
        }

        @Override
        public void cancel() {
            isCancel = true;
            super.cancel();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            isFlingRun = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(mOnGestureListener!=null)
            {
//                mOnGestureListener.onTouchUp(type);
                mOnGestureListener.onFlingEnd(type);
            }
            isFlingRun = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isFlingRun = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }

    public void cancelFlingAnim()
    {
        if(animatorFling!=null)
        {
            animatorFling.cancel();
        }
    }

    private void cancelAnim()
    {
        cancelFlingAnim();
        if(mOnGestureListener!=null)
        {
            mOnGestureListener.onCancelAnim();
        }
    }

    public void setOpenDrag(boolean openDrag) {
        isOpenDrag = openDrag;
    }

    //endregion===================动画专区结束======================


    public boolean isSingleTouch() {
        return isSingleTouch;
    }

    /**
     * 设置长按确定时间
     * @param time 毫秒
     */
    public void setIS_DRAG_MOVE_MIN_TIME(int time)
    {
        IS_DRAG_MOVE_MIN_TIME = time;
    }

    private OnTJGestureListener mOnGestureListener ;
    public OnTJGestureListener getOnGestureListener() {
        return mOnGestureListener;
    }

    public void setOnGestureListener(OnTJGestureListener onGestureListener) {
        mOnGestureListener = onGestureListener;
    }

    private class HandlerCallback implements Handler.Callback
    {
        public static final int DOWN_LONG = 1001;
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==DOWN_LONG)
            {
                if(!isPointerPress&&!isQMove&&isSinglePress)
                {
                    Bundle bundle = msg.getData();
                    if(bundle!=null)
                    {
                        float x = bundle.getFloat("x");
                        float y = bundle.getFloat("y");
                        if(mOnGestureListener!=null) {
                            mOnGestureListener.onDownLong(x,y);
                        }
                    }
                }
            }
            return false;
        }
    }
}