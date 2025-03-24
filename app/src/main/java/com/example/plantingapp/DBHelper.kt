package com.example.plantingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "main.db"
        private const val DATABASE_VERSION = 1

        private val createLogGroup: String = """
            CREATE TABLE logGroup (
                logGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                groupName TEXT NOT NULL CHECK(LENGTH(groupName) <= 15),
                remark TEXT CHECK(LENGTH(remark) <= 15),
                coverImageUri TEXT DEFAULT 'default'
            );
        """.trimIndent()

        private val createLog: String = """
            CREATE TABLE log (
                logId INTEGER PRIMARY KEY AUTOINCREMENT,
                logGroupId INTEGER,
                textRecord TEXT,
                recordDate INTEGER NOT NULL,
                weatherTemperatureRange TEXT
            );
        """.trimIndent()

        private val createUser: String = """
            CREATE TABLE user (
                userId INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE CHECK(LENGTH(username) >= 5),
                password TEXT NOT NULL CHECK(LENGTH(password) >= 8),
                user_avatar TEXT
            );
        """.trimIndent()

        private val createTodo: String = """
            CREATE TABLE todo (
                todoId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                todoName TEXT NOT NULL CHECK(LENGTH(todoName) <= 15),
                createTime INTEGER NOT NULL,
                endTime INTEGER DEFAULT 0,
                interval INTEGER CHECK(interval BETWEEN 1 AND 365),
                isEnabled INTEGER DEFAULT 0 CHECK(isEnabled IN (0,1,2)),
                logGroupId INTEGER,
                finished_times INTEGER DEFAULT 0
            );
        """.trimIndent()

        private val createCustomTag: String = """
            CREATE TABLE customTag (
                customTagId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                tagName TEXT NOT NULL CHECK(LENGTH(tagName) <= 8),
                tagIcon TEXT NOT NULL,
                tagType INTEGER NOT NULL CHECK(tagType IN (1,2)),
                unit TEXT
            );
        """.trimIndent()

        private val createLogTags: String = """
            CREATE TABLE logTags (
                logId INTEGER NOT NULL,
                tagId INTEGER NOT NULL,
                tagType INTEGER NOT NULL CHECK(tagType IN (1,2)),
                tagValue1 INTEGER CHECK(tagValue1 BETWEEN 1 AND 5),
                tagValue2 REAL,
                tagTips TEXT,
                PRIMARY KEY(logId, tagId)
            );
        """.trimIndent()

        private val createLogPics: String = """
            CREATE TABLE logPics (
                logId INTEGER NOT NULL,
                imageUri TEXT NOT NULL,
                PRIMARY KEY(logId, imageUri)
            );
        """.trimIndent()

        private val dropLogPics: String = """
            DROP TABLE IF EXISTS logPics;
        """.trimIndent()

        private val dropLogTags: String = """
            DROP TABLE IF EXISTS logTags;
        """.trimIndent()

        private val dropCustomTag: String = """
            DROP TABLE IF EXISTS customTag;
        """.trimIndent()

        private val dropTodo: String = """
            DROP TABLE IF EXISTS todo;
        """.trimIndent()

        private val dropLog: String = """
            DROP TABLE IF EXISTS log;
        """.trimIndent()

        private val dropLogGroup: String = """
            DROP TABLE IF EXISTS logGroup;
        """.trimIndent()

        private val dropUser: String = """
            DROP TABLE IF EXISTS user;
        """.trimIndent()
//密码以及修改成a1111111,昵称更新成忆植测试1
        private val insertDefaultUser = """
            INSERT INTO user (username, password, user_avatar)
            VALUES ('admin', '11111111', NULL);
        """.trimIndent()

        val clearUserTable: String = "DELETE FROM user;"
        val clearLogGroupTable: String = "DELETE FROM logGroup;"
        val clearLogTable: String = "DELETE FROM log;"
        val clearTodoTable: String = "DELETE FROM todo;"
        val clearCustomTagTable: String = "DELETE FROM customTag;"
        val clearLogTagsTable: String = "DELETE FROM logTags;"
        val clearLogPicsTable: String = "DELETE FROM logPics;"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createLogGroup)
        db.execSQL(createLog)
        db.execSQL(createUser)
        db.execSQL(createTodo)
        db.execSQL(createCustomTag)
        db.execSQL(createLogTags)
        db.execSQL(createLogPics)
        // 插入默认账户
        db.execSQL(insertDefaultUser)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(dropLogPics)
        db.execSQL(dropLogTags)
        db.execSQL(dropCustomTag)
        db.execSQL(dropTodo)
        db.execSQL(dropLog)
        db.execSQL(dropLogGroup)
        db.execSQL(dropUser)

        db.execSQL(createLogGroup)
        db.execSQL(createLog)
        db.execSQL(createUser)
        db.execSQL(createTodo)
        db.execSQL(createCustomTag)
        db.execSQL(createLogTags)
        db.execSQL(createLogPics)
    }

}