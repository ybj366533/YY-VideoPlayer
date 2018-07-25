package com.ybj366533.yy_videoplayer.anim

import android.animation.Animator
import android.animation.ValueAnimator

/**
 * Created by wittyneko on 2017/7/6.
 */
interface AnimListener : ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
}

open class SampleAnimListener : AnimListener {

    override fun onAnimationUpdate(animation: ValueAnimator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }
}