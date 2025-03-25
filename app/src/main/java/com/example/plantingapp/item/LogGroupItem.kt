package com.example.plantingapp.item

import com.example.plantingapp.R

data class LogGroupItem(
    var gpId:Int,
    var gpName:String,
    val createTime:Long,
    var lastModified:Long?,
    val coverRes:Int = R.drawable.icon_main,
    var coverUri:String?,
    var coverType:Int = RES_COVER,
    var hint:String?,
    var status:Int = MODE_OPEN
)
{
    companion object {
        val URI_COVER = 1
        val RES_COVER = 0

        val MODE_OPEN = 1
        val MODE_EDIT = 2
        val MODE_DEL = 3
    }

}