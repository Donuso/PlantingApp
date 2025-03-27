package com.example.plantingapp.item

import com.example.plantingapp.Utils

data class UniversalTodoItem(
    val todoId: Int, // from DB
    val userId: Int, // from DB
    var todoName: String, // from DB
    val createTime: Long, // from DB
    var endTime: String? = null, // from DB,DB为空时置为空
    var interval: Int, // from DB
    var isEnabled: Int, // from DB
    var logGroupId: Int? = null, // from DB,DB为空时置为空
    var attachedGroupName: String? = null,
    var finishedTimes: Int = 0, // from DB
    var displayMode:Int = DISPLAY_NORMAL
){
    companion object{
        const val STATUS_RUNNING = 1
        const val STATUS_DISABLED = 0
//        const val STATUS_OUTDATED = 12  // 已废弃

        const val DISPLAY_NORMAL = 13
        const val DISPLAY_DEL = 14
        const val DISPLAY_ALTER = 15
    }

    fun isOutDated():Boolean{
        return if(endTime!=null){
            Utils.dateStringToTimestamp(endTime!!)<System.currentTimeMillis()
        }else{
            false
        }
    }

    data class GroupBasicItem(
        val id:Int,
        val name:String
    )
}
