package com.example.plantingapp.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.example.plantingapp.DBHelper
import com.example.plantingapp.R
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LogGroupItem

class LogGroupDAO(context: Context) {
    private val db = DBHelper(context)

    companion object {
        val OPT_DEL = 0
        val OPT_ALT = 1
    }

    // 删除日志组
    fun deleteLogGroup(logGroupItem: LogGroupItem): Int {
        return db.writableDatabase.delete(
            "logGroup",
            "logGroupId = ?",
            arrayOf(logGroupItem.gpId.toString())
        )
    }

    // 新增日志组（带查重）
    fun insertLogGroup(logGroupItem: LogGroupItem): Long {
        if (isGroupNameExist(logGroupItem.gpName, DataExchange.USERID!!.toInt())) {
            throw IllegalArgumentException("日志组名称已存在")
        }

        val cv = ContentValues().apply {
            put("userId", DataExchange.USERID!!.toInt())
            put("groupName", logGroupItem.gpName)
            put("remark", logGroupItem.hint)
            put("coverImageUri", logGroupItem.coverUri ?: "default")
            put("lastModified", -1L)
            put("createdTime", logGroupItem.createTime)
        }
        return db.writableDatabase.insert("logGroup", null, cv)
    }

    // 修改日志组
    fun updateLogGroup(logGroupItem: LogGroupItem): Int {
        if (isGroupNameExist(logGroupItem.gpName, DataExchange.USERID!!.toInt())) {
            throw IllegalArgumentException("日志组名称已存在")
        }
        val cv = ContentValues().apply {
            put("groupName", logGroupItem.gpName)
            put("remark", logGroupItem.hint)
            put("coverImageUri", logGroupItem.coverUri ?: "default")
            put("lastModified", System.currentTimeMillis())
        }
        return db.writableDatabase.update(
            "logGroup",
            cv,
            "logGroupId = ?",
            arrayOf(logGroupItem.gpId.toString())
        )
    }

    private fun isGroupNameExist(groupName: String, userId: Int): Boolean {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT 1 FROM logGroup WHERE userId = ? AND groupName = ?",
            arrayOf(userId.toString(), groupName)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    fun getAllLogGroups(): MutableList<LogGroupItem> {
        val userId = DataExchange.USERID ?: return mutableListOf()
        val logGroups = mutableListOf<LogGroupItem>()

        db.readableDatabase.query(
            "logGroup",
            arrayOf("logGroupId", "groupName", "remark", "coverImageUri", "createdTime", "lastModified"),
            "userId = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "createdTime ASC" // 按创建时间顺序排列
        ).use { cursor ->
            while (cursor.moveToNext()) {
                with(cursor) {
                    val gpId = getInt(getColumnIndex("logGroupId"))
                    val gpName = getString(getColumnIndex("groupName"))
                    val remark = getString(getColumnIndex("remark"))
                    val coverImageUri = getString(getColumnIndex("coverImageUri"))
                    val createdTime = getLong(getColumnIndex("createdTime"))
                    val lastModified = getLong(getColumnIndex("lastModified"))

                    // 解析封面类型
                    val coverType = if (coverImageUri == "default") {
                        LogGroupItem.RES_COVER
                    } else {
                        LogGroupItem.URI_COVER
                    }

                    // 构建LogGroupItem对象
                    logGroups.add(
                        LogGroupItem(
                            gpId = gpId,
                            gpName = gpName,
                            createTime = createdTime,
                            lastModified = lastModified,
                            coverRes = if (coverType == LogGroupItem.RES_COVER) R.drawable.icon_main else 0,
                            coverUri = if (coverType == LogGroupItem.URI_COVER) coverImageUri else null,
                            coverType = coverType,
                            hint = remark,
                            status = LogGroupItem.MODE_OPEN
                        )
                    )
                }
            }
        }
        return logGroups
    }
}