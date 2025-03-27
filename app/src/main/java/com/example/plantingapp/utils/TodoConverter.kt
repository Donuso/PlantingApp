package com.example.plantingapp.converter

import com.example.plantingapp.item.TodayTodoItem
import com.example.plantingapp.item.UniversalTodoItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TodoConverter {
    private val gson = Gson()

    // TodayTodoItem列表 ↔ JSON
    fun toJson(items: MutableList<TodayTodoItem>): String {
        return gson.toJson(items)
    }

    fun fromJsonToList(json: String): MutableList<TodayTodoItem> {
        val type = object : TypeToken<MutableList<TodayTodoItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // UniversalTodoItem ↔ JSON
    fun toJson(item: UniversalTodoItem): String {
        return gson.toJson(item)
    }

    fun fromJsonToItem(json: String): UniversalTodoItem {
        return gson.fromJson(json, UniversalTodoItem::class.java)
    }
}