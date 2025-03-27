package com.example.plantingapp.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.plantingapp.DBHelper
import com.example.plantingapp.item.UniversalTodoItem

class ToDoDAO(context: Context) {
    private val db = DBHelper(context)
    private val writableDatabase = db.writableDatabase
    private val readableDatabase = db.readableDatabase

    fun getTodosByUserId(userId: Int): MutableList<UniversalTodoItem> {
        val todos = mutableListOf<UniversalTodoItem>()
        val cursor = readableDatabase.query(
            "todo",
            arrayOf("todoId", "userId", "todoName", "createTime", "endTime", "interval", "isEnabled", "logGroupId", "finished_times"),
            "userId = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val todoId = getInt(getColumnIndexOrThrow("todoId"))
                val todoName = getString(getColumnIndexOrThrow("todoName"))
                val createTime = getLong(getColumnIndexOrThrow("createTime"))
                val interval = getInt(getColumnIndexOrThrow("interval"))
                val isEnabled = getInt(getColumnIndexOrThrow("isEnabled"))
                val finishedTimes = getInt(getColumnIndexOrThrow("finished_times"))

                val logGroupId = if (isNull(getColumnIndexOrThrow("logGroupId"))) {
                    null
                } else {
                    getInt(getColumnIndexOrThrow("logGroupId"))
                }

                val endTime = if (isNull(getColumnIndexOrThrow("endTime"))) {
                    null
                } else {
                    getString(getColumnIndexOrThrow("endTime"))
                }

                todos.add(
                    UniversalTodoItem(
                        todoId = todoId,
                        userId = userId,
                        todoName = todoName,
                        createTime = createTime,
                        endTime = endTime,
                        interval = interval,
                        isEnabled = isEnabled,
                        logGroupId = logGroupId,
                        finishedTimes = finishedTimes
                    )
                )
            }
            close()
        }
        return todos
    }

    fun getLogGroupsByUserId(userId: Int): MutableList<UniversalTodoItem.GroupBasicItem> {
        val groups = mutableListOf<UniversalTodoItem.GroupBasicItem>()
        val cursor = readableDatabase.query(
            "logGroup",
            arrayOf("logGroupId", "groupName"),
            "userId = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("logGroupId"))
                val name = getString(getColumnIndexOrThrow("groupName"))
                groups.add(UniversalTodoItem.GroupBasicItem(id = id, name = name))
            }
            close()
        }
        return groups
    }

    fun getLogGroupsByUserIdWithMap(userId: Int): MutableMap<Int,String> {
        val groups = mutableMapOf<Int,String>()
        val cursor = readableDatabase.query(
            "logGroup",
            arrayOf("logGroupId", "groupName"),
            "userId = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                groups[getInt(getColumnIndexOrThrow("logGroupId"))] = getString(getColumnIndexOrThrow("groupName"))
            }
            close()
        }
        return groups
    }

    fun updateTodo(todoItem: UniversalTodoItem) {
        val contentValues = ContentValues().apply {
            put("todoName", todoItem.todoName)
            put("endTime", todoItem.endTime)
            put("interval", todoItem.interval)
            put("isEnabled", todoItem.isEnabled)
            put("logGroupId", todoItem.logGroupId ?: 0)
            put("finished_times", todoItem.finishedTimes)
        }

        writableDatabase.update(
            "todo",
            contentValues,
            "todoId = ?",
            arrayOf(todoItem.todoId.toString())
        )
    }

    fun deleteTodo(todoId: Int) {
        writableDatabase.delete(
            "todo",
            "todoId = ?",
            arrayOf(todoId.toString())
        )
    }

    fun insertTodo(
        userId: Int,
        todoName: String,
        endTime: String? = null,
        interval: Int,
        isEnabled: Int,
        logGroupId: Int? = null
    ): Long {
        // 参数校验
        val contentValues = ContentValues().apply {
            put("userId", userId)
            put("todoName", todoName)
            put("createTime", System.currentTimeMillis()) // 自动生成当前时间
            put("endTime", endTime)
            put("interval", interval)
            put("isEnabled", isEnabled)
            put("logGroupId", logGroupId)
            put("finished_times", 0) // 默认值
        }
        return writableDatabase.insert("todo", null, contentValues)
    }

    fun filterTodosByStatus(
        todos: MutableList<UniversalTodoItem>,
        status: Int
    ): MutableList<UniversalTodoItem> {
        return todos.filter { it.isEnabled == status }.toMutableList()
    }

}