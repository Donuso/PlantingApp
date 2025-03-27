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
        const val URI_COVER = 1
        const val RES_COVER = 0

        const val MODE_OPEN = 1
        const val MODE_EDIT = 2
        const val MODE_DEL = 3
    }

}