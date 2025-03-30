package com.example.plantingapp.dao

import android.annotation.SuppressLint
import android.content.Context
import com.example.plantingapp.DBHelper
import com.example.plantingapp.Utils
import com.example.plantingapp.item.DayItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.plantingapp.item.LogPicItem
import com.example.plantingapp.item.LabelItem
import android.content.ContentValues
import com.example.plantingapp.Constants
import android.database.Cursor
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit
import com.example.plantingapp.item.DataExchange

class LogDAO(context: Context) {
    private val db = DBHelper(context)

    // 测试方法
    fun checkLogFromRange(start:String): MutableList<DayItem> {
        var l: MutableList<DayItem> = mutableListOf()
        for(i in 1..31){
            var compStr = i.toString()
            l.add(
                DayItem(
                    i,
                    false,
                    DayItem.STATUS_NO_RECORD
                )
            )
        }
        return l
    }

    // 检查并插入日志（不存在时插入）
    fun checkAndInsertLog(context: Context,logGroupId: Int, dateString: String): Long {

        val timestamp = Utils.dateStringToTimestamp(dateString)
        if (timestamp == 0L) return 0L // 无效日期处理

        //放置访问记录

        val recordDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
        val existingLog = getLogIdByGroupAndDate(logGroupId, recordDate)
        return if (existingLog != 0L) {
            existingLog
        } else {
            val newLogId = db.writableDatabase.insert(
                "log",
                null,
                ContentValues().apply {
                    put("logGroupId", logGroupId)
                    put("recordDate", recordDate)
                }
            )
            if (newLogId != -1L) {
                val sp = context.getSharedPreferences("${DataExchange.USERID}_prefs",Context.MODE_PRIVATE)
                with(sp.edit()){
                    putInt("logs_recorded_count",sp.getInt("logs_recorded_count",0)+1)
                    apply()
                }
                newLogId
            } else
                0L
        }
    }

    // 通过logGroupId和日期获取logId
    private fun getLogIdByGroupAndDate(logGroupId: Int, recordDate: String): Long {
        val cursor = db.readableDatabase.query(
            "log",
            arrayOf("logId"),
            "logGroupId = ? AND recordDate = ?",
            arrayOf(logGroupId.toString(), recordDate),
            null,
            null,
            null
        )
        var logId = 0L
        with(cursor) {
            if (moveToFirst()) logId = getLong(getColumnIndexOrThrow("logId"))
            close()
        }
        return logId
    }

    // 获取所有标签
    @SuppressLint("Range")
    fun getAllLabels(logId: Int): MutableList<LabelItem> {
        val labels = mutableListOf<LabelItem>()
        val cursor = db.readableDatabase.query(
            "logTags",
            arrayOf("tagId", "tagType", "tagValue1", "tagValue2", "tagTips"),
            "logId = ?",
            arrayOf(logId.toString()),
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val tagId = getInt(getColumnIndexOrThrow("tagId"))
                val tagType = getInt(getColumnIndexOrThrow("tagType"))
                val tagValue1 = getInt(getColumnIndexOrThrow("tagValue1"))
                val tagValue2 = getDouble(getColumnIndexOrThrow("tagValue2"))
                val tagTips = getString(getColumnIndexOrThrow("tagTips"))

                val labelItem = when (tagType) {
                    1 -> { // 处理状态类默认标签
                        Constants.defStatusLabels.firstOrNull { it.tagId == tagId }?.let { statusLabel ->
                            LabelItem(
                                logId = logId,
                                tagId = tagId,
                                tagName = statusLabel.tagName,
                                tagType = LabelItem.TYPE_STATUS,
                                tagIcon = statusLabel.tagIcon,
                                valStatus = tagValue1,
                                hint = tagTips,
                                isCustom = false
                            )
                        }
                    }
                    2 -> { // 处理数据类默认标签
                        Constants.defDataLabels.firstOrNull { it.tagId == tagId }?.let { dataLabel ->
                            LabelItem(
                                logId = logId,
                                tagId = tagId,
                                tagName = dataLabel.tagName,
                                tagType = LabelItem.TYPE_DATA,
                                tagIcon = dataLabel.tagIcon,
                                valData = tagValue2,
                                valUnit = dataLabel.valUnit,
                                hint = tagTips,
                                isCustom = false
                            )
                        }
                    }
                    3 -> { // 处理自定义标签
                        val customTagCursor = db.readableDatabase.query(
                            "customTag",
                            arrayOf("tagName", "tagIcon", "tagType", "unit"),
                            "customTagId = ? AND userId = ?",
                            arrayOf(tagId.toString(), DataExchange.USERID), // 假设USERID是String类型
                            null,
                            null,
                            null
                        )

                        customTagCursor.use { customCursor ->
                            if (customCursor.moveToFirst()) {
                                val customTagType = customCursor.getInt(customCursor.getColumnIndex("tagType"))
                                val tagName = customCursor.getString(customCursor.getColumnIndex("tagName"))
                                val tagIcon = customCursor.getInt(customCursor.getColumnIndex("tagIcon"))
                                val unit = customCursor.getString(customCursor.getColumnIndex("unit"))

                                when (customTagType) {
                                    1 -> LabelItem(
                                        logId = logId,
                                        tagId = tagId,
                                        tagName = tagName,
                                        tagType = LabelItem.TYPE_STATUS,
                                        tagIcon = tagIcon,
                                        valStatus = tagValue1,
                                        hint = tagTips,
                                        isCustom = true
                                    )
                                    2 -> LabelItem(
                                        logId = logId,
                                        tagId = tagId,
                                        tagName = tagName,
                                        tagType = LabelItem.TYPE_DATA,
                                        tagIcon = tagIcon,
                                        valData = tagValue2,
                                        valUnit = unit,
                                        hint = tagTips,
                                        isCustom = true
                                    )
                                    else -> {
                                        Log.v("readingLabel","Failed! no customtype")
                                        null
                                    } // 无效标签类型
                                }
                            } else {
                                Log.v("readingLabel","Failed! no custom column")
                                null // 未找到对应自定义标签
                            }
                        }
                    }
                    else -> {
                        Log.v("readingLabel","Failed! invalid type!")
                        null
                    } // 无效标签类型
                }

                labelItem?.let {
                    labels.add(it)
                    Log.v("readingLabel",labelItem.toString())
                } // 仅添加有效标签
            }
            close()
        }
        return labels
    }

    // 获取所有图片
    @SuppressLint("Range")
    fun getAllPics(logId: Int): MutableList<LogPicItem> {
        val pics = mutableListOf<LogPicItem>()
        db.readableDatabase.query(
            "logPics",
            arrayOf("imageUri"),
            "logId = ?",
            arrayOf(logId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                pics.add(LogPicItem(logID = logId, uriString = cursor.getString(cursor.getColumnIndex("imageUri"))))
            }
        }
        return pics
    }

    // 获取日志文本
    @SuppressLint("Range")
    fun getLogText(logId: Int): String? {
        return db.readableDatabase.query(
            "log",
            arrayOf("textRecord"),
            "logId = ?",
            arrayOf(logId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndex("textRecord"))
            else null
        }
    }

    // 添加图片
    fun addPic(logId: Int, uriString: String) {
        db.writableDatabase.insert(
            "logPics",
            null,
            ContentValues().apply {
                put("logId", logId)
                put("imageUri", uriString)
            }
        )
    }

    // 修改日志文本
    fun updateLogText(logId: Int, text: String?) {
        db.writableDatabase.update(
            "log",
            ContentValues().apply { put("textRecord", text) },
            "logId = ?",
            arrayOf(logId.toString())
        )
    }

    // 删除标签
    fun deleteLabel(labelItem: LabelItem) {
        db.writableDatabase.delete(
            "logTags",
            "logId = ? AND tagId = ?",
            arrayOf(labelItem.logId.toString(), labelItem.tagId.toString())
        )
    }

    // 删除图片
    fun deletePic(picItem: LogPicItem) {
        db.writableDatabase.delete(
            "logPics",
            "logId = ? AND imageUri = ?",
            arrayOf(picItem.logID.toString(), picItem.uriString)
        )
    }

    // 获取月份记录天数
    @SuppressLint("Range")
    fun getRecordedDaysInMonth(logGroupId: Int, yearMonth: String): MutableList<DayItem> {
        val days = mutableListOf<DayItem>()
        try {
            val calendar = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(yearMonth)!!
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            // 获取所有有记录的天数
            val recordedDays = mutableSetOf<Int>()
            db.readableDatabase.query(
                "log",
                arrayOf("recordDate"),
                "logGroupId = ? AND recordDate LIKE ?",
                arrayOf(logGroupId.toString(), "$yearMonth-%"),
                null,
                null,
                null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val day = SimpleDateFormat("dd", Locale.getDefault())
                        .parse(cursor.getString(cursor.getColumnIndex("recordDate")).substring(8))?.date ?: 0
                    recordedDays.add(day)
                }
            }

            // 生成整月天数列表
            (1..maxDay).forEach { day ->
                days.add(
                    DayItem(
                        day = day,
                        hasRecord = day in recordedDays,
                        status = if (day in recordedDays) DayItem.STATUS_HAS_RECORD else DayItem.STATUS_NO_RECORD
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return days
    }

    // 集成到所有方法中去
    fun updateLastModified(groupId: Int): Int {
        val currentTime = System.currentTimeMillis()
        val dbWritable = db.writableDatabase

        // 使用ContentValues构建更新内容
        val values = ContentValues().apply {
            put("lastModified", currentTime)
        }

        // 执行更新操作
        return dbWritable.update(
            "logGroup",
            values,
            "logGroupId = ?",
            arrayOf(groupId.toString())
        )
    }

    fun updateWeatherTemperatureRange(logId: Int, temperatureRange: String) {
        val values = ContentValues().apply {
            put("weatherTemperatureRange", temperatureRange)
        }
        db.writableDatabase.update(
            "log",
            values,
            "logId = ?",
            arrayOf(logId.toString())
        )
    }

    @SuppressLint("Range")
    fun getWeatherTemperatureRange(logId: Int): String? {
        val cursor = db.readableDatabase.query(
            "log",
            arrayOf("weatherTemperatureRange"),
            "logId = ?",
            arrayOf(logId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex("weatherTemperatureRange"))
        } else {
            null
        }.also { cursor.close() }
    }

}