package com.example.plantingapp.dao

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import com.example.plantingapp.DBHelper
import com.example.plantingapp.item.LogGroup
import com.example.plantingapp.item.Todo

class SearchDAO(private val context: Context) {
    // 搜索日志组
    fun searchLogGroups(dbHelper: DBHelper, userId: Int, keyword: String): List<LogGroup> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<LogGroup>()

        val cursor = db.rawQuery("""
            SELECT * FROM logGroup 
            WHERE userId = ? 
            AND groupName LIKE '%' || ? || '%'
        """.trimIndent(), arrayOf(userId.toString(), keyword))

        while (cursor.moveToNext()) {
            list.add(LogGroup(
                cursor.getInt(cursor.getColumnIndexOrThrow("logGroupId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("groupName")),
                cursor.getString(cursor.getColumnIndexOrThrow("remark")),
                cursor.getString(cursor.getColumnIndexOrThrow("coverImageUri")),
                cursor.getLong(cursor.getColumnIndexOrThrow("lastModified")),
                cursor.getLong(cursor.getColumnIndexOrThrow("createdTime"))
            ))
        }
        cursor.close()
        return list
    }

    // 搜索待办
    fun searchTodos(dbHelper: DBHelper, userId: Int, keyword: String): List<Todo> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Todo>()

        val cursor = db.rawQuery("""
            SELECT * FROM todo 
            WHERE userId = ? 
            AND (todoName LIKE '%' || ? || '%' 
                 OR EXISTS (
                    SELECT 1 FROM logGroup 
                    WHERE logGroup.logGroupId = todo.logGroupId 
                    AND logGroup.groupName LIKE '%' || ? || '%'
                 ))
        """.trimIndent(), arrayOf(userId.toString(), keyword, keyword))

        while (cursor.moveToNext()) {
            list.add(Todo(
                cursor.getInt(cursor.getColumnIndexOrThrow("todoId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("todoName")),
                cursor.getLong(cursor.getColumnIndexOrThrow("createTime")),
                cursor.getLong(cursor.getColumnIndexOrThrow("endTime")),
                cursor.getInt(cursor.getColumnIndexOrThrow("interval")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isEnabled")),
                cursor.getInt(cursor.getColumnIndexOrThrow("logGroupId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("finished_times"))
            ))
        }
        cursor.close()
        return list
    }


}