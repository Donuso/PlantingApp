package com.example.plantingapp.item

data class LabelItem(
    val logId:Int? = null,
    val tagId:Int,
    val tagName:String,
    val tagType:Int,
    val tagIcon:Int,
    var valStatus:Int? = null,
    var valData:Double? = null,
    var valUnit:String? = null,
    var hint:String? = null,
    val isCustom: Boolean,
    var status:Int = MODE_DISPLAY
)
{
    companion object{
        val MODE_DISPLAY = 0
        val MODE_DEL = 1

        val TYPE_DATA = 1
        val TYPE_STATUS = 2

    }
}
