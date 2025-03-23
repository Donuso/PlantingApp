package com.example.plantingapp

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import java.util.concurrent.TimeUnit


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

        // 时间戳转日期字符串
        fun timestampToDateString(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

        //日期字符串转时间戳
        fun dateStringToTimestamp(dateString: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            return date?.time ?: 0L // 如果解析失败，返回0L
        }

        // 使用时间戳计算该时间距离今天有多少天
        fun daysBetweenNowAndTimestamp(timestamp: Long): Long {
            val now = System.currentTimeMillis()

            // 将时间戳和当前时间都转换为“yyyy-MM-dd”的起始时间（即当天的00:00:00）
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateNow = dateFormat.parse(dateFormat.format(Date(now)))
            val dateThen = dateFormat.parse(dateFormat.format(Date(timestamp)))

            val timeNow = dateNow?.time ?: 0L
            val timeThen = dateThen?.time ?: 0L

            // 计算天数差
            val diffInMillis = timeNow - timeThen
            return TimeUnit.MILLISECONDS.toDays(diffInMillis)
        }

        fun dateStringToCH(str:String): String {
            val res = "${str.substring(0..3)}年${str.substring(5..6)}月${str.substring(8..9)}日"
            return res
        }


        // 存入单张图片，并获取存储后的uri
        fun storeSinglePicture(context: Context, uri: Uri): Uri? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val outputFile = File(context.filesDir, fileName)

                FileOutputStream(outputFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }

                // 生成content URI
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

    }
}