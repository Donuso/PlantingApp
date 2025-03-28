package com.example.plantingapp.item

data class MsgItem(
    var content:String,
    val type:Int,
    var status:Int = 1
) {

    companion object{
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1

        const val STATUS_START_LOADING = 0
        const val STATUS_LOAD_SUCCESSFULLY = 1
        const val STATUS_LOAD_FAILED = -1
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as MsgItem
//        return content == other.content && type == other.type && status == other.status
//    }
}