package com.example.plantingapp.animators

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.plantingapp.Utils
import kotlin.math.ln

/* 该类尚未经过测试，请勿使用
*
* 变化类型 rateType ：1 - 线性 ， 2 - 对数（先慢后快） ， 3 - 反比例（先快后慢）
* 移动方向 moveDirection ：0 - 不移动 ， 1 - 左右移动 ， 2 - 上下移动
* */
class ExpandAnimator(private val context: Context, private val targetView: View) {
    private var duration: Long = 300
    private var rateType: Int = 1
    private var colorChangeFlag: Int = 0
    private var startColorLong: Long = 0
    private var endColorLong: Long = 0
    private var moveDirection: Int = 0
    private var moveDistanceDp: Float = 0f
    private var moveDistancePx: Int = 0

    // 链式调用方法
    fun setDuration(duration: Long) = apply { this.duration = duration }
    fun setRateType(rateType: Int) = apply { this.rateType = rateType }
    fun setColorChange(flag: Int, startColor: Long, endColor: Long) = apply {
        this.colorChangeFlag = flag
        this.startColorLong = startColor
        this.endColorLong = endColor
    }
    fun setMoveDirection(direction: Int, distanceDp: Float) = apply {
        this.moveDirection = direction
        this.moveDistanceDp = distanceDp
        this.moveDistancePx = Utils.dpToPx(context, distanceDp)
    }

    fun start() {
        targetView.let { view ->
            // 创建对应插值器
            val interpolator: TimeInterpolator = when (rateType) {
                1 -> LinearInterpolator()
                2 -> LogInterpolator()
                3 -> InverseInterpolator()
                else -> LinearInterpolator()
            }

            // 创建动画
            ValueAnimator.ofFloat(0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator

                addUpdateListener { animation ->
                    val progress = animation.animatedValue as Float
                    updateViewProperties(view, progress)
                }
            }.start()
        }
    }

    // 更新视图属性
    private fun updateViewProperties(view: View, progress: Float) {
        // 处理位移
        when (moveDirection) {
            1 -> view.translationX = moveDistancePx * progress
            2 -> view.translationY = moveDistancePx * progress
        }

        // 处理颜色变化
        if (colorChangeFlag == 1) {
            val startColor = Utils.longToColor(startColorLong)
            val endColor = Utils.longToColor(endColorLong)
            view.setBackgroundColor(
                android.animation.ArgbEvaluator().evaluate(
                    progress,
                    startColor,
                    endColor
                ) as Int
            )
        }
    }

    // 对数插值器（动画开始慢，逐渐加快）
    private class LogInterpolator : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            return if (input <= 0) 0f else (ln(input.toDouble() + 1) / ln(2.0)).toFloat()
        }
    }

    // 反比例插值器（动画开始快，逐渐减慢）
    private class InverseInterpolator : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            return (input / (2 - input)).coerceAtMost(1f)
        }
    }

}