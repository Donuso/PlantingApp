package com.example.plantingapp.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.plantingapp.DBHelper
import com.example.plantingapp.item.MeItem

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

    fun getUserByIdYZR(userId: Int): MeItem? {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT userId, username, user_avatar FROM user WHERE userId = ?"
        val args = arrayOf(userId.toString())

        var meItem: MeItem? = null
        db.rawQuery(query, args).use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("userId"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val avatar = cursor.getString(cursor.getColumnIndexOrThrow("user_avatar"))
                meItem = MeItem(id, name, avatar)
            }
        }
        return meItem
    }

    fun updateUserAvatarYZR(meItem: MeItem):Int{
        val db: SQLiteDatabase = dbHelper.writableDatabase
        var result = MeItem.SUCCESS

        if (meItem.userAvatar != null) {
            val updateCount = db.update(
                "user",
                ContentValues().apply {
                    put("user_avatar", meItem.userAvatar)
                },
                "userId = ?",
                arrayOf(meItem.userId.toString())
            )

            if (updateCount == 0) {
                result = MeItem.FAILED_ALTER_USERAVATAR
            }
        }
        return result
    }

    fun updateUserNameYZR(meItem: MeItem): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        var result = MeItem.SUCCESS
        var result1 = MeItem.SUCCESS

        // 先尝试修改用户名
        if (meItem.username != getCurrentUsernameYZR(meItem.userId)) {
            val checkQuery = "SELECT COUNT(*) FROM user WHERE username = ? AND userId != ?"
            val checkArgs = arrayOf(meItem.username, meItem.userId.toString())
            val cursor = db.rawQuery(checkQuery, checkArgs)

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                result1 = MeItem.FAILED_ALTER_USERNAME
            } else {
                db.execSQL(
                    "UPDATE user SET username = ? WHERE userId = ?",
                    arrayOf(meItem.username, meItem.userId.toString())
                )
            }
            cursor.close()
        }


        if(result1!=MeItem.SUCCESS){
            result = MeItem.FAILED_ALTER_USERNAME
        }

        return result
    }

    fun updatePasswordYZR(userId: Int, oldPassword: String, newPassword: String): Int {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT password FROM user WHERE userId = ?"
        val args = arrayOf(userId.toString())
        var storedPassword = ""
        db.rawQuery(query, args).use { cursor ->
            if (cursor.moveToFirst()) {
                storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            } else {
                return PASSWORD_OLD_NOT_MATCH
            }
        }
        if (storedPassword != oldPassword) {
            return PASSWORD_OLD_NOT_MATCH
        }
        if (!newPassword.matches(Regex("^[A-Za-z0-9]{8,}$"))) {
            return PASSWORD_NEW_INVALID
        }
        val updateCount = db.update(
            "user",
            ContentValues().apply {
                put("password", newPassword)
            },
            "userId = ?",
            arrayOf(userId.toString())
        )

        return if (updateCount > 0) {
            PASSWORD_SUCCESS
        } else {
            PASSWORD_ALTER_FAILED
        }
    }

    private fun getCurrentUsernameYZR(userId: Int): String {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT username FROM user WHERE userId = ?"
        val args = arrayOf(userId.toString())
        return db.rawQuery(query, args).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow("username"))
            } else {
                ""
            }
        }
    }

    companion object {
        const val AUTH_SUCCESS = 1        // 认证成功
        const val INCORRECT_PASSWORD = 2  // 密码不正确
        const val USER_NOT_EXIST = 3      // 用户不存在

        const val PASSWORD_OLD_NOT_MATCH = 1
        const val PASSWORD_NEW_INVALID = 2
        const val PASSWORD_SUCCESS = 3
        const val PASSWORD_ALTER_FAILED = 4
    }
}