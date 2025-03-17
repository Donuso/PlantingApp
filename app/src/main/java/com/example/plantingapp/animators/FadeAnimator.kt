package com.example.plantingapp.animators

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class FadeAnimator(private var targetView: View) {
    private var duration: Long = 300L
    private var interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
    private var isAnimating = false

    // 链式调用方法设置 View
    fun setTargetView(view: View): FadeAnimator {
        this.targetView = view
        return this
    }

    // 链式调用方法设置动画时长
    fun setDuration(duration: Long): FadeAnimator {
        this.duration = duration
        return this
    }

    // 链式调用方法设置插值器
    fun setInterpolator(interpolator: TimeInterpolator): FadeAnimator {
        this.interpolator = interpolator
        return this
    }

    // 启动动画
    fun start(visible: Boolean) {
        targetView.let { view ->
            if (isAnimating) cancel() // 如果正在动画，先取消之前的动画

            val startAlpha = if (visible) 0f else view.alpha
            val endAlpha = if (visible) 1f else 0f

            val animator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha).apply {
                this.duration = this@FadeAnimator.duration
                this.interpolator = this@FadeAnimator.interpolator

                addUpdateListener { view.visibility = View.VISIBLE }

                // 动画结束后的处理
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = if (visible) View.VISIBLE else View.GONE
                        view.alpha = if (visible) 1f else 0f // 确保最终状态正确
                        isAnimating = false
                    }
                })
            }

            animator.start()
            isAnimating = true
        }
    }

    // 取消正在进行的动画
    private fun cancel() {
        targetView.animate()?.cancel()
        isAnimating = false
    }
}