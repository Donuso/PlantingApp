package com.example.plantingapp.utils

import com.example.plantingapp.item.LabelItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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