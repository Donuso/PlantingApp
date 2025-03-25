package com.example.plantingapp.item

data class DayItem(
    val day:Int,
    val hasRecord:Boolean = false,
    var status:Int = STATUS_NO_RECORD
){
    companion object {
        val STATUS_NO_RECORD = 0
        val STATUS_HAS_RECORD = 1
        val STATUS_CHOSEN = 2
    }
}