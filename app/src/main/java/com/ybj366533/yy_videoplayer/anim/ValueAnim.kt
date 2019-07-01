package com.ybj366533.yy_videoplayer.anim

import android.animation.*

/**
 * Created by wittyneko on 2017/7/7.
 */

open class ValueAnim : ValueAnimator(), AnimListener {
    companion object {
        internal val argbEvaluator = ArgbEvaluator()

        fun ofInt(vararg values: Int): ValueAnim {
            val anim = ValueAnim()
            anim.setIntValues(*values)
            return anim
        }

        fun ofArgb(values: IntArray): ValueAnim {
            val anim = ValueAnim()
            anim.setIntValues(*values)
            anim.setEvaluator(argbEvaluator)
            return anim
        }

        fun ofFloat(vararg values: Float): ValueAnim {
            val anim = ValueAnim()
            anim.setFloatValues(*values)
            return anim
        }

        fun ofPropertyValuesHolder(vararg values: PropertyValuesHolder): ValueAnim {
            val anim = ValueAnim()
            anim.setValues(*values)
            return anim
        }

        fun ofObject(evaluator: TypeEvaluator<*>, vararg values: Any): ValueAnim {
            val anim = ValueAnim()
            anim.setObjectValues(*values)
            anim.setEvaluator(evaluator)
            return anim
        }

    }


    private var _isAnimReverse: Boolean = true

    var listener: AnimListener? = null

    var isAnimEnd: Boolean = false
        protected set

    var isAnimCancel: Boolean = false
        protected set

    //是否反向
    var isAnimReverse: Boolean
        get() {
            if (isRunning) {
                return isReversing
            } else {
                return _isAnimReverse
            }
        }
        internal set(value) {
            _isAnimReverse = value
        }

    //动画播放时间
    val animCurrentPlayTime: Long
        get() {
            if (isRunning && isAnimReverse) {
                return duration - currentPlayTime
            } else {
                return currentPlayTime
            }
        }

    init {
        addListener(this)
        addUpdateListener(this)
    }


    /**
     * 正向播放
     */
    open fun animStart() {
        when {
            isRunning && isAnimReverse -> {
                reverse()
            }
            !isRunning -> {
                start()
            }
        }
    }

    /**
     * 反向播放
     */
    open fun animReverse() {
        when {
            isRunning && !isAnimReverse -> {
                reverse()
            }
            !isRunning -> {
                reverse()
            }
        }
    }

    /**
     * 切换播放方向
     */
    open fun animTrigger() {
        if (isAnimReverse) {
            animStart()
        } else {
            animReverse()
        }
    }

    override fun start() {
        isAnimCancel = false
        isAnimEnd = false
        super.start()
    }

    override fun reverse() {
        isAnimCancel = false
        isAnimEnd = false
        super.reverse()
    }

    override fun end() {
        isAnimCancel = false
        isAnimEnd = true
        super.end()
    }

    override fun cancel() {
        isAnimCancel = true
        isAnimEnd = false
        super.cancel()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        listener?.onAnimationUpdate(animation)
    }

    override fun onAnimationStart(animation: Animator?) {
        listener?.onAnimationStart(animation)
    }

    override fun onAnimationEnd(animation: Animator?) {
        if ((isStarted || isRunning) && animation is ValueAnimator) {
            _isAnimReverse = animation.isReversing
        }
        listener?.onAnimationEnd(animation)
    }

    override fun onAnimationCancel(animation: Animator?) {
        listener?.onAnimationCancel(animation)
    }

    override fun onAnimationRepeat(animation: Animator?) {
        listener?.onAnimationRepeat(animation)
    }
}

// 动画播放时方向 api22+
val ValueAnimator.isReversing: Boolean
    get() {
        try {
            var rfield = ValueAnimator::class.java.getDeclaredField("mReversing")
            rfield.isAccessible = true
            return rfield.get(this) as? Boolean ?: false
        } catch (e: Throwable) {
            return isPlayingBackwards
        }
    }

// 动画播放时方向 api21-
val ValueAnimator.isPlayingBackwards: Boolean
    get() {
        try {
            var rfield = ValueAnimator::class.java.getDeclaredField("mPlayingBackwards")
            rfield.isAccessible = true
            return rfield.get(this) as? Boolean ?: false
        } catch (e: Throwable) {
            return false
        }
    }