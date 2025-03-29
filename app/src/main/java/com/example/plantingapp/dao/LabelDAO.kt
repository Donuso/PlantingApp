package com.example.plantingapp.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.plantingapp.DBHelper
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LabelItem

class LabelDAO(context: Context) {
    private val db = DBHelper(context)
    fun getAllCustomTags(userId: String): List<LabelItem> {
        val labels = mutableListOf<LabelItem>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT * FROM customTag WHERE userId = ?",
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            labels.add(cursorToLabelItem(cursor).apply {
                isCustom = true
            })
        }
        cursor.close()
        return labels
    }

    // 插入新方法
    fun insertLogTag(logId: Int, labelItem: LabelItem) {
        val w = db.writableDatabase
        val values = ContentValues().apply {
            put("logId", logId)
            put("tagId", labelItem.tagId)
            put("tagType", if (labelItem.isCustom) 3 else labelItem.tagType) // 类型映射
            put("tagValue1", labelItem.valStatus ?: 0) // 处理空值
            put("tagValue2", labelItem.valData ?: 0.0)
            put("tagTips", labelItem.hint)
        }
        Log.v("insertLabel!",values.toString())

        w.insert("logTags", null, values)
        w.close()
    }

    // 更新方法（带智能匹配逻辑）
    fun updateLogTag(logId: Int, labelItem: LabelItem) {
        val u = db.writableDatabase
        val whereClause = "logId=? AND tagId=? AND tagType=?"
        val whereArgs = mutableListOf<String>()

        // 构建智能匹配条件
        whereArgs.add(logId.toString())
        whereArgs.add(labelItem.tagId.toString())
        Log.v("updating:",labelItem.isCustom.toString())
        whereArgs.add(
            if (labelItem.isCustom) "3" // 自定义标签匹配type=3
            else labelItem.tagType.toString() // 系统标签匹配原始type
        )

        val updateValues = ContentValues().apply {
            put("tagValue1", labelItem.valStatus ?: 0)
            put("tagValue2", labelItem.valData ?: 0.0)
            put("tagTips", labelItem.hint)
        }
        Log.v("UpdatingLabel!",updateValues.toString())
        u.update("logTags", updateValues, whereClause, whereArgs.toTypedArray())
        u.close()
    }

    // 删除自定义标签及其关联记录
    fun deleteCustomTag(userId: String, tagId: Int): Int {
        db.writableDatabase.beginTransaction()
        try {
            // 删除customTag记录
            db.writableDatabase.delete(
                "customTag",
                "userId = ? AND customTagId = ?",
                arrayOf(userId, tagId.toString())
            )

            // 删除关联logTags记录
            db.writableDatabase.delete(
                "logTags",
                "tagId = ? AND tagType = ?",
                arrayOf(tagId.toString(), "3")
            )

            db.writableDatabase.setTransactionSuccessful()
            return 1
        } finally {
            db.writableDatabase.endTransaction()
        }
    }

    // 添加自定义标签（分两步）
    fun addCustomTag(labelItem: LabelItem): Long {
        // 第一步：检查名称重复
        if (isTagNameExist(labelItem.tagName, DataExchange.USERID!!)) {
            throw IllegalArgumentException("自定义标签名称重复")
        }

        // 第二步：插入新标签
        val cv = ContentValues().apply {
            put("userId", DataExchange.USERID!!)
            put("tagName", labelItem.tagName)
            put("tagIcon", labelItem.tagIcon)
            put("tagType", labelItem.tagType)
            put("unit", labelItem.valUnit)
        }
        val i = db.writableDatabase.insert("customTag", null, cv)
        return i
    }

    fun isTagNameExist(tagName: String, userId: String): Boolean {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT 1 FROM customTag WHERE userId = ? AND tagName = ?",
            arrayOf(userId, tagName)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    private fun cursorToLabelItem(cursor: Cursor): LabelItem {
        return LabelItem(
            tagId = cursor.getInt(cursor.getColumnIndex("customTagId")),
            tagName = cursor.getString(cursor.getColumnIndex("tagName")),
            tagType = cursor.getInt(cursor.getColumnIndex("tagType")),
            tagIcon = cursor.getInt(cursor.getColumnIndex("tagIcon")),
            isCustom = true,
            valUnit = cursor.getString(cursor.getColumnIndex("unit"))
        )
    }

}