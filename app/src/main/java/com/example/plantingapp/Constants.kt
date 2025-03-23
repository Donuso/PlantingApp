package com.example.plantingapp

import com.example.plantingapp.item.LabelItem
import java.util.Collections.list

class Constants {

    /*
    * 这是一个常量类，请在其伴生类中放入所有你需要跨类使用/与他人共享的常量。
    * 请注意，是常量。
    * 请备注其用途。
    * */

    companion object {
        // 来自MsgItem类
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1

        //来自labelItem的内置常量
        val defStatusLabels = listOf(
            LabelItem(
                tagId = 1,
                tagName = "叶片状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_main,
                isCustom = false
            ),
        )

        val defDataLabels = listOf(
            LabelItem(
                tagId = 1,
                tagName = "生长高度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_main,
                valUnit = "cm",
                isCustom = false
            )
        )



    }
}