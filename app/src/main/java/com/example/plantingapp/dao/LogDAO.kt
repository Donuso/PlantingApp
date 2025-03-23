package com.example.plantingapp.dao

import android.content.Context
import com.example.plantingapp.DBHelper
import com.example.plantingapp.Utils
import com.example.plantingapp.item.DayItem

class LogDAO(context: Context) {
    private val db = DBHelper(context)


    fun prepareNewLog(){

    }

    fun checkLogFromRange(start:String): MutableList<DayItem> {
        var l: MutableList<DayItem> = mutableListOf()
        for(i in 1..31){
            var compStr = i.toString()
            l.add(
                DayItem(
                    i,
                    false,
                    DayItem.STATUS_NO_RECORD
                )
            )
        }
        return l
    }

    fun getLogLabels(){

    }

    fun getLogPics(){

    }

    fun insertLogLabel(){

    }

    fun insertLogPic(){

    }

    fun deleteLogPic(){

    }

    fun deleteLogLabel(){

    }

}