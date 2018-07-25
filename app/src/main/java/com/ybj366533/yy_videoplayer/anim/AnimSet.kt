package com.ybj366533.yy_videoplayer.anim

import android.animation.*
import android.view.animation.LinearInterpolator

/**
 * Created by wittyneko on 2017/7/6.
 */

open class AnimSet : ValueAnim() {

    companion object {

        fun ofDef(): AnimSet {
            return ofFloat(0f, 1f)
        }

        fun ofInt(vararg values: Int): AnimSet {
            val anim = AnimSet()
            anim.setIntValues(*values)
            return anim
        }

        fun ofArgb(values: IntArray): AnimSet {
            val anim = AnimSet()
            anim.setIntValues(*values)
            anim.setEvaluator(argbEvaluator)
            return anim
        }

        fun ofFloat(vararg values: Float): AnimSet {
            val anim = AnimSet()
            anim.setFloatValues(*values)
            return anim
        }

        fun ofPropertyValuesHolder(vararg values: PropertyValuesHolder): AnimSet {
            val anim = AnimSet()
            anim.setValues(*values)
            return anim
        }

        fun ofObject(evaluator: TypeEvaluator<*>, vararg values: Any): AnimSet {
            val anim = AnimSet()
            anim.setObjectValues(*values)
            anim.setEvaluator(evaluator)
            return anim
        }

    }

    var childAnimSet: HashSet<AnimWrapper> = hashSetOf()

    init {
        interpolator = LinearInterpolator()
    }

    /**
     * 计算子动画播放时间
     * @param delayed 子动画延迟时间
     * @param duration 子动画时长
     *
     * @return 子动画当前播放时间
     */
    fun animChildPlayTime(delayed: Long, duration: Long): Long {
        var childPlayTime = animCurrentPlayTime - delayed
        when {
            childPlayTime < 0 -> {
                childPlayTime = 0
            }
            childPlayTime > duration -> {
                childPlayTime = duration
            }
        }
        return childPlayTime
    }

    /**
     * 添加子动画
     * @param childAnim 子动画
     * @param delayed 子动画延迟时间
     * @param tag 子动画tag标签
     */
    fun addChildAnim(childAnim: ValueAnimator, delayed: Long = 0, tag: String = AnimWrapper.EMPTY_TAG): AnimSet {
        addChildAnim(AnimWrapper(childAnim, delayed, tag))
        return this
    }

    /**
     * 添加子动画
     * @param child 子动画包装类
     *
     * @throws e duration grate than parent
     */
    fun addChildAnim(child: AnimWrapper): AnimSet {
        if (child.delayed + child.anim.duration > this.duration)
            throw Exception("duration greater than parent")
        childAnimSet.add(child)
        return this
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        super.onAnimationUpdate(animation)

        childAnimSet.forEach {
            //刷新子动画
            val anim = it.anim
            anim.currentPlayTime = animChildPlayTime(it.delayed, anim.duration)

            if(anim is ValueAnim) {
                anim.isAnimReverse = isAnimReverse
            }
        }
    }

    override fun onAnimationStart(animation: Animator?) {
        super.onAnimationStart(animation)

        childAnimSet.forEach {
            val anim = it.anim
            anim.listeners?.forEach {
                it.onAnimationStart(anim)
            }
        }
    }

    override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)

        childAnimSet.forEach {
            val anim = it.anim
            if (isAnimEnd) {
                if (isAnimReverse)
                    anim.currentPlayTime = 0
                else
                    anim.currentPlayTime = anim.duration
            }
            anim.listeners?.forEach {
                it.onAnimationEnd(anim)
            }
        }
    }

    override fun onAnimationCancel(animation: Animator?) {
        super.onAnimationCancel(animation)

        childAnimSet.forEach {
            val anim = it.anim
            anim.listeners?.forEach {
                it.onAnimationCancel(anim)
            }
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {
        super.onAnimationRepeat(animation)

        childAnimSet.forEach {
            val anim = it.anim
            anim.listeners?.forEach {
                it.onAnimationRepeat(anim)
            }
        }
    }

    /**
     * 子动画包装类
     */
    class AnimWrapper(
            var anim: ValueAnimator,
            var delayed: Long = 0,
            var tag: String = EMPTY_TAG) {
        companion object {
            val EMPTY_TAG = ""
        }
    }
}