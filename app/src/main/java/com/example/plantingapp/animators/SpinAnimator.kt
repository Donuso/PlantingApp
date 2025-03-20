package com.example.plantingapp.animators

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.LinearInterpolator

class SpinAnimator(private var targetView: View) {
    private var duration: Long = 300L
    private var interpolator: TimeInterpolator = LinearInterpolator()
    private var isAnimating = false
    private var animator: ObjectAnimator? = null

    // 链式调用方法设置目标视图
    fun setTargetView(view: View): SpinAnimator {
        this.targetView = view
        return this
    }

    // 链式调用方法设置动画时长
    fun setDuration(duration: Long): SpinAnimator {
        this.duration = duration
        return this
    }

    // 链式调用方法设置插值器
    fun setInterpolator(interpolator: TimeInterpolator): SpinAnimator {
        this.interpolator = interpolator
        return this
    }

    // 启动无限旋转动画
    fun start() {
        targetView.let { view ->
            if (isAnimating) stop() // 如果正在动画，先停止

            // 计算当前旋转角度作为起始值
            val currentRotation = view.rotation
            val endRotation = currentRotation + 360f

            animator = ObjectAnimator.ofFloat(view, "rotation", currentRotation, endRotation).apply {
                this.duration = this@SpinAnimator.duration
                this.interpolator = this@SpinAnimator.interpolator
                repeatCount = ObjectAnimator.INFINITE   // 无限循环
                repeatMode = ObjectAnimator.RESTART     // 每次从头开始

                // 监听动画取消事件
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator) {
                        isAnimating = false
                    }
                })
            }

            animator?.start()
            isAnimating = true
        }
    }

    // 停止动画
    fun stop() {
        animator?.cancel()
        isAnimating = false
    }
}