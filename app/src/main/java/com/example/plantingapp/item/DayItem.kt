package com.example.plantingapp.item

data class DayItem(
    val day:Int,
    val hasRecord:Boolean = false,
    var status:Int = STATUS_NO_RECORD
){
    companion object {
        const val STATUS_NO_RECORD = 0
        const val STATUS_HAS_RECORD = 1
        const val STATUS_CHOSEN = 2
    }
}