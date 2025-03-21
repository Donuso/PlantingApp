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

    companion object {
        const val AUTH_SUCCESS = 1        // 认证成功
        const val INCORRECT_PASSWORD = 2  // 密码不正确
        const val USER_NOT_EXIST = 3      // 用户不存在
    }
}