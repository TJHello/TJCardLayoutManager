package com.tj.order.card.layoutmanager.listener;

/**
 *
 * Created by TJbaobao on 2017/12/26.
 */


public abstract class OnTJGestureListener
{
    public abstract void onSingleMove(float firstX,float firstY,float offsetX,float offsetY);

    /**
     * 单指移动
     * @param firstX 第一下点击的X
     * @param firstY 第一下点击的Y
     * @param offsetX 移动/偏移量X
     * @param offsetY 移动/偏移量Y
     * @param x 当前X
     * @param y 当前Y
     */
    public void onSingleMove(float firstX,float firstY,float offsetX,float offsetY,float x,float y)
    {
        onSingleMove(firstX,firstY,offsetX,offsetY);
    }

    /**
     * 单击事件
     * @param x 当前X
     * @param y 当前Y
     */
    public void onDown(float x,float y)
    {

    }

    /**
     * 单击事件
     * @param x 当前X
     * @param y 当前Y
     */
    public void onClick(float x,float y)
    {

    }

    public void onDownLong(float x,float y)
    {

    }

    public void onDownLongMove(float x,float y,float firstX,float firstY,float offsetX,float offsetY)
    {
        onSingleMove(firstX,firstY,offsetX,offsetY,x,y);
    }

    public void onDrag(float x,float y,float firstX,float firstY,float offsetX,float offsetY)
    {
        onSingleMove(firstX,firstY,offsetX,offsetY,x,y);
    }

    public void onPointerScale(float centerX,float centerY,float offsetX,float offsetY,float scale,float distance)
    {

    }

    public void onFling(float speedX,float speedY,float offsetX,float offsetY)
    {

    }

    public void onFlingUp(float velocityX,float velocityY)
    {

    }

    public void onFlingEnd(int type){
//        onTouchUp(type);
    }

    public abstract void onTouchUp(int type);

    public  void onCancelAnim(){}
}
