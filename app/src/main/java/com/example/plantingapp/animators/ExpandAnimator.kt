package com.example.plantingapp.animators

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.plantingapp.Utils
import com.google.android.material.card.MaterialCardView
import kotlin.math.ln
import kotlin.math.pow

/* 变化类型 rateType ：1 - 线性 ， 2 - 对数（先慢后快） ， 3 - 反比例（先快后慢）
 * 移动方向 moveDirection ：0 - 不移动 ， 1 - 左右移动 ， 2 - 上下移动
 * fadeEnabled: 是否启用淡入淡出效果
 * 注意：颜色渐变效果需要修复，暂时无法使用
 * */
class ExpandAnimator(private val context: Context, private val targetView: View) {

    companion object {
        // ExpandAnimator的三种动画速率
        const val linearRatio = 1
        const val logRatio = 2
        const val inverseRatio = 3
        const val iOSRatio = 4
    }

    private var duration: Long = 300
    private var rateType: Int = 1
    private var colorChangeFlag: Boolean = false // 是否启用背景颜色变化
    private var startColorLong: Long = 0
    private var endColorLong: Long = 0
    private var moveDirection: Int = 0
    private var moveDistanceDp: Float = 0f
    private var moveDistancePx: Int = 0
    private var fadeEnabled: Boolean = false // 是否启用淡入淡出
    private var startAlpha: Float = 0f
    private var endAlpha: Float = 1f
    private var isAnimating = false

    // 链式调用方法
    fun setDuration(duration: Long) = apply { this.duration = duration }
    fun setRateType(rateType: Int) = apply { this.rateType = rateType }
    fun setColor(startColor: Long, endColor: Long) = apply {
        this.startColorLong = startColor
        this.endColorLong = endColor
    }
    fun setIfChangeColor(flag: Boolean) = apply {
        this.colorChangeFlag = flag
    }
    fun setMoveDirection(direction: Int, distanceDp: Float) = apply {
        this.moveDirection = direction
        this.moveDistanceDp = distanceDp
        this.moveDistancePx = Utils.dpToPx(context, distanceDp)
    }
    fun setIfFade(needFade:Boolean) = apply {
        this.fadeEnabled = needFade
    }
    fun setFade(startAlpha: Float, endAlpha: Float) = apply {
        this.startAlpha = startAlpha
        this.endAlpha = endAlpha
    }
    fun reverseColor() = apply {
        setColor(endColorLong,startColorLong)
    }
    fun start() {
        targetView.let { view ->
            if (isAnimating) cancel()
            // 创建对应插值器
            val interpolator: TimeInterpolator = when (rateType) {
                1 -> LinearInterpolator()
                2 -> LogInterpolator()
                3 -> InverseInterpolator()
                4 -> IOSLikeInterpolator()
                else -> LinearInterpolator()
            }

            // 创建动画
            ValueAnimator.ofFloat(0f, 1f).apply {
                this.duration = this@ExpandAnimator.duration
                this.interpolator = interpolator

                addUpdateListener { animation ->
                    view.visibility = View.VISIBLE
                    val progress = animation.animatedValue as Float
                    updateViewProperties(view, progress)
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = if (endAlpha == 1f) View.VISIBLE else View.GONE
                        isAnimating = false
                    }
                })
            }.start()
            isAnimating = true
        }
    }

    // 更新视图属性
    private fun updateViewProperties(view: View, progress: Float) {
        // 处理位移
        when (moveDirection) {
            1 -> {
                if(moveDistancePx >= 0){
                    view.translationX = moveDistancePx * progress
                }else{
                    view.translationX = -moveDistancePx * (1 - progress)
                }
            }
            2 -> {
                if(moveDistancePx >= 0) {
                    view.translationY = moveDistancePx * progress
                }else{
                    view.translationY = -moveDistancePx * (1 - progress)
                }
            }
        }

        // 处理颜色变化
        if (colorChangeFlag) {
            val startColor = Utils.longToColor(startColorLong)
            val endColor = Utils.longToColor(endColorLong)
            when(view){
                is TextView -> {
                    view.setTextColor(
                        android.animation.ArgbEvaluator().evaluate(
                            progress,
                            startColor,
                            endColor
                        ) as Int
                    )
                }
                is MaterialCardView -> {
                    view.setCardBackgroundColor(
                        android.animation.ArgbEvaluator().evaluate(
                            progress,
                            startColor,
                            endColor
                        ) as Int
                    )
                }
                is CardView -> {
                    view.setCardBackgroundColor(
                        android.animation.ArgbEvaluator().evaluate(
                            progress,
                            startColor,
                            endColor
                        ) as Int
                    )
                }
                else -> {
                    view.setBackgroundColor(
                        android.animation.ArgbEvaluator().evaluate(
                            progress,
                            startColor,
                            endColor
                        ) as Int
                    )
                }
            }
        }

        // 处理淡入淡出
        if (fadeEnabled) {
            val alpha = startAlpha + (endAlpha - startAlpha) * progress
            view.alpha = alpha
        }
    }

    // 对数插值器（动画开始慢，逐渐加快）
    private class LogInterpolator : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            return if (input <= 0) 0f else (ln(input.toDouble() + 1) / ln(2.0)).toFloat()
        }
    }


    // 调整后的反比例插值器（更平缓的减速）
    private class InverseInterpolator : TimeInterpolator {
        // 添加平滑系数和偏移量
        override fun getInterpolation(input: Float): Float {
            val smoothFactor = 0.8f // 平滑系数（0-1）
            val offset = 0.2f // 偏移量防止初始突变
            return (input / (1 + (1 - input) * smoothFactor + offset)).coerceAtMost(1f)
        }
    }

    private class IOSLikeInterpolator : TimeInterpolator {
        override fun getInterpolation(input: Float): Float {
            return when {
                input <= 0 -> 0f
                input >= 1 -> 1f
                else -> {
                    // 计算对数函数值（注意处理浮点精度）
                    val logValue:Float = ln(input.toDouble()).toFloat()

                    // 计算原始函数值（限制极端值）
                    val y:Float = -1 * (1 / (40 * input)) * logValue

                    // 将结果映射到[0,1]区间
                    (1 - y).coerceAtMost(1f)
                }
            }
        }

        // 辅助函数：计算自然对数（避免Math.log的极端值）
        private fun ln(x: Float): Float {
            return if (x <= 0) Float.NEGATIVE_INFINITY else Math.log(x.toDouble()).toFloat()
        }
    }


    private fun cancel() {
        targetView.animate()?.cancel()
        isAnimating = false
    }
}