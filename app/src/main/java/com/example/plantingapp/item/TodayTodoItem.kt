package com.example.plantingapp.item


data class TodayTodoItem(
    val todoId: Int, // INTEGER PRIMARY KEY AUTOINCREMENT
    val userId: Int, // INTEGER NOT NULL
    var todoName: String, // TEXT NOT NULL CHECK(LENGTH(todoName) <= 15)
    var attachedGroupName: String? = null,
    var status:Int = STATUS_UNFINISHED
){
    companion object{
        const val STATUS_FINISHED = 5
        const val STATUS_UNFINISHED = 6
    }
}
