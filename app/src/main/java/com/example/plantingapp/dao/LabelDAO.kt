package com.example.plantingapp.dao

import android.content.Context
import com.example.plantingapp.DBHelper
import com.example.plantingapp.item.LabelItem

class LabelDAO(context: Context) {
    private val db = DBHelper(context)

    fun getAllCustomLabels():MutableList<LabelItem> {
        return mutableListOf()
    }

    fun deleteCustomLabel(id:Int){

    }

}