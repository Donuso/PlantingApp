package com.example.plantingapp.item

data class LogGroup(
    val logGroupId: Int,
    val userId: Int,
    val groupName: String,
    val remark: String?,
    val coverImageUri: String,
    val lastModified: Long,
    val createdTime: Long
)