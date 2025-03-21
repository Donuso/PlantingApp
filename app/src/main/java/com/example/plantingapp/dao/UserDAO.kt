package com.example.plantingapp.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.plantingapp.DBHelper

class UserDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    fun verifyUser(name: String, pwd: String): Int {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT password FROM user WHERE username = ?"
        val args = arrayOf(name)

        val cursor: Cursor? = db.rawQuery(query, args)

        return try {
            if (cursor != null && cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                if (storedPassword == pwd) {
                    AUTH_SUCCESS  // 认证成功
                } else {
                    INCORRECT_PASSWORD  // 密码不正确
                }
            } else {
                USER_NOT_EXIST  // 用户不存在
            }
        } finally {
            cursor?.close()
        }
    }
    // 检查用户名是否存在
    fun isUsernameExist(username: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT 1 FROM user WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // 插入新用户
    fun insertUser(username: String, password: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        return db.insert("user", null, values)
    }
    // 根据用户名获取用户 ID
    fun getUserIdByUsername(username: String): String? {
        val db = dbHelper.readableDatabase
        val query = "SELECT userId FROM user WHERE username = ?"  // 假设你的用户表有一个 'id' 字段
        val cursor: Cursor? = db.rawQuery(query, arrayOf(username))

        return try {
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow("userId"))
            } else {
                null  // 如果用户不存在，返回 null
            }
        } finally {
            cursor?.close()
        }
    }

    companion object {
        const val AUTH_SUCCESS = 1        // 认证成功
        const val INCORRECT_PASSWORD = 2  // 密码不正确
        const val USER_NOT_EXIST = 3      // 用户不存在
    }
}