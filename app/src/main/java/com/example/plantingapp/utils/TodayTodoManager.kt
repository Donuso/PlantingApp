package com.example.plantingapp.utils

import android.content.Context
import com.example.plantingapp.Utils
import com.example.plantingapp.converter.TodoConverter
import com.example.plantingapp.dao.ToDoDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.UniversalTodoItem
import com.example.plantingapp.item.TodayTodoItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TodayTodoManager {
    fun calculateTodayTodos(context: Context,uid:Int){
        val dao = ToDoDAO(context)
        val allTodos = dao.getTodosByUserId(uid)
        val allGroups = dao.getLogGroupsByUserIdWithMap(uid)
        val sp = context.getSharedPreferences("${uid}_prefs",Context.MODE_PRIVATE)
        val todayTodoString = sp.getString("today_todos",null)
        val todoTimeStamp = sp.getString("today_todo_timestamp",null)
        val runningTodos = mutableListOf<UniversalTodoItem>()
        val result = mutableListOf<TodayTodoItem>()
        if(todayTodoString == null
            || todoTimeStamp == null
            || isDateEarlier(todoTimeStamp)
        ){ // 待办过期或未生成第一次今日待办
            // 第一次筛选
            for(it in allTodos){
                if(it.isEnabled == UniversalTodoItem.STATUS_RUNNING){ // 置于运行状态
                    if(it.endTime != null){ // 有终止日期，且启用
                        if(isDateEarlier(it.endTime!!)){ // 有终止日期，启用，但是已经过期
                            dao.deleteTodo(it.todoId)
                        }else{ // 有终止日期，启用，且没过期
                            runningTodos.add(it)
                        }
                    }else{ // 无终止日期，且被启用
                        runningTodos.add(it)
                    }
                }
            }
            // 第二次筛选
            for(it in runningTodos){
                var diff = calculateDaysDifference(Utils.timestampToDateString(it.createTime)).toInt()
                if(it.interval != 0){
                    diff %= it.interval
                }
                if(diff == 0 || it.interval == 0) {
                    result.add(
                        TodayTodoItem(
                            todoId = it.todoId,
                            userId = it.userId,
                            todoName = it.todoName,
                            attachedGroupName = if (it.logGroupId == null || it.logGroupId == 0) {
                                null
                            } else {
                                allGroups[it.logGroupId]
                            }
                        )
                    )
                }
            }
            // 放入结果
            with(sp.edit()){
                putString("today_todos",TodoConverter.toJson(result))
                putString("today_todo_timestamp",Utils.timestampToDateString(System.currentTimeMillis()))
                apply()
            }
        }else{// 今日待办已经生成过，可能需要更新
            val origin = TodoConverter.fromJsonToList(todayTodoString)
            // 保留已有待办的执行状态
            val savedStatus = mutableMapOf<Int,Int>()
            for(it in origin){
                savedStatus[it.todoId] = it.status
            }
            // 重新计算所有待办
            for(it in allTodos){
                if(it.isEnabled == UniversalTodoItem.STATUS_RUNNING){ // 置于运行状态
                    if(it.endTime != null){ // 有终止日期，且启用
                        if(isDateEarlier(it.endTime!!)){ // 有终止日期，启用，但是已经过期
                            dao.deleteTodo(it.todoId)
                        }else{ // 有终止日期，启用，且没过期
                            runningTodos.add(it)
                        }
                    }else{ // 无终止日期，且被启用
                        runningTodos.add(it)
                    }
                }
            }
            for(it in runningTodos){
                var diff = calculateDaysDifference(Utils.timestampToDateString(it.createTime)).toInt()
                if(it.interval != 0){
                    diff %= it.interval
                }
                if(diff == 0 || it.interval == 0) {
                    result.add(
                        TodayTodoItem(
                            todoId = it.todoId,
                            userId = it.userId,
                            todoName = it.todoName,
                            attachedGroupName = if (it.logGroupId == null || it.logGroupId == 0) {
                                null
                            } else {
                                allGroups[it.logGroupId]
                            }
                        )
                    )
                }
            }
            // 复原待办的状态
            for(it in result){
                if(savedStatus.containsKey(it.todoId)){
                    it.status = savedStatus[it.todoId]!!
                }
            }
            // 放入结果
            with(sp.edit()){
                putString("today_todos",TodoConverter.toJson(result))
                apply()
            }
        }
    }

    fun calculateDaysDifference(dateStr: String): Long {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val inputDate = LocalDate.parse(dateStr, formatter)
        val today = LocalDate.now()

        // 计算日期差（自动处理顺序，保证结果非负）
        return ChronoUnit.DAYS.between(inputDate, today).coerceAtLeast(0)
    }

    fun isDateEarlier(dateStr: String): Boolean {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val inputDate = LocalDate.parse(dateStr, formatter)
        val today = LocalDate.now()

        return inputDate < today
    }


}