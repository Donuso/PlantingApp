package com.example.plantingapp.item

data class LogPicItem(
    val logID:Int,
    val uriString: String,
    var status:Int = MODE_DISPLAY
) {
    companion object{
        const val MODE_DISPLAY = 0
        const val MODE_DEL = 1
    }
}