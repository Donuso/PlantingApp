package com.example.plantingapp

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import kotlin.math.roundToInt

class Utils {

    /*
    * 这是一个工具类，你可以在其伴生类中放置一些你要多处跨类使用的，封装好的方法。
    * 请使用备注提示其用途。
    * */
    companion object {

        // 这是一个将 dp（屏幕密度）单位的值转化为实际像素值的方法，通常用于移动控件时方便计算实际运动距离
        fun dpToPx(context: Context, dp: Float): Int {
            val displayMetrics = context.resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }

        // 这是一个将长整型值颜色正确映射到整型值颜色的方法，通常用于ARGB类型颜色的转换
        fun longToColor(longColor: Long): Int {
            // 提取透明度 (Alpha)、红色 (Red)、绿色 (Green)、蓝色 (Blue)
            val alpha = (longColor shr 24 and 0xFF).toInt()   // 获取高8位
            val red = (longColor shr 16 and 0xFF).toInt()     // 获取中高8位
            val green = (longColor shr 8 and 0xFF).toInt()    // 获取中低8位
            val blue = (longColor and 0xFF).toInt()           // 获取低8位

            return Color.argb(alpha, red, green, blue)
        }

    }
}