package com.example.plantingapp.item

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class LabelItem(
    val logId:Int? = null,
    var tagId:Int,
    var tagName:String,
    var tagType:Int,
    val tagIcon:Int,
    var valStatus:Int? = null,
    var valData:Double? = null,
    var valUnit:String? = null,
    var hint:String? = null,
    var isCustom: Boolean,
    var status:Int = MODE_DISPLAY
)
{
    companion object{
        const val MODE_DISPLAY = 0
        const val MODE_DEL = 1

        const val TYPE_DATA = 2
        const val TYPE_STATUS = 1

        const val STATUS_1 = 1 //好
        const val STATUS_2 = 2 //较好
        const val STATUS_3 = 3 //一般
        const val STATUS_4 = 4 //较差
        const val STATUS_5 = 5 //差

    }
}


