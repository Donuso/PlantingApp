package com.example.plantingapp.item

data class Todo(
    val todoId: Int,
    val userId: Int,
    val todoName: String,
    val createTime: Long,
    val endTime: Long,
    val interval: Int?,
    val isEnabled: Int,
    val logGroupId: Int?,
    val finishedTimes: Int
)