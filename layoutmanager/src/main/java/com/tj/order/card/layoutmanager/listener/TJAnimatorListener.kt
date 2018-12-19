package com.tj.order.card.layoutmanager.listener

import android.animation.Animator

/**
 * 作者:TJbaobao
 * 时间:2018/8/11  11:35
 * 说明:
 * 使用：
 */
interface TJAnimatorListener : Animator.AnimatorListener{

    override fun onAnimationStart(animation: Animator?) {

    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }
}