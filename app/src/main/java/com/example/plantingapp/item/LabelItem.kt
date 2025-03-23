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
    val isCustom: Boolean,
    var status:Int = MODE_DISPLAY
)
{
    companion object{
        val MODE_DISPLAY = 0
        val MODE_DEL = 1

        val TYPE_DATA = 1
        val TYPE_STATUS = 2

        val STATUS_1 = 1 //好
        val STATUS_2 = 2 //较好
        val STATUS_3 = 3 //一般
        val STATUS_4 = 4 //较差
        val STATUS_5 = 5 //差

    }
}

object LabelItemConverter {

    private val gson = Gson()

    // 将 MutableList<LabelItem> 转换为 JSON 字符串
    fun listToJson(labelItems: MutableList<LabelItem>): String {
        return gson.toJson(labelItems)
    }

    fun labelToJson(l: LabelItem): String {
        return gson.toJson(l)
    }

    // 将 JSON 字符串 转换为 MutableList<LabelItem>
    fun jsonToList(json: String): MutableList<LabelItem> {
        val listType = object : TypeToken<MutableList<LabelItem>>() {}.type
        return gson.fromJson(json, listType)
    }

    fun jsonToLabel(str: String): LabelItem {
        return gson.fromJson(str, LabelItem::class.java)
    }
}
