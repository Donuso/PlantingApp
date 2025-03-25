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
            LabelItem(
                tagId = 2,
                tagName = "光照状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_only_used_in_label_sunlight,
                isCustom = false
            ),
            LabelItem(
                tagId = 3,
                tagName = "根系状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_only_used_in_label_root,
                isCustom = false
            ),
            LabelItem(
                tagId = 4,
                tagName = "花朵状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_only_used_in_label_flower,
                isCustom = false
            ),
            LabelItem(
                tagId = 5,
                tagName = "通风状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_only_used_in_label_wind,
                isCustom = false
            ),
            LabelItem(
                tagId = 5,
                tagName = "结果率",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_only_used_in_label_fruit,
                isCustom = false
            )

        )

        val defDataLabels = listOf(
            LabelItem(
                tagId = 1,
                tagName = "生长高度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_ruler,
                valUnit = "cm",
                isCustom = false
            ),
            LabelItem(
                tagId = 2,
                tagName = "光照强度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_sunlight,
                valUnit = "lux",
                isCustom = false
            ),
            LabelItem(
                tagId = 3,
                tagName = "环境温度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_temp,
                valUnit = "℃",
                isCustom = false
            ),
            LabelItem(
                tagId = 4,
                tagName = "土壤湿度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_moisture,
                valUnit = "%",
                isCustom = false
            ),
            LabelItem(
                tagId = 5,
                tagName = "施肥浓度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_fertilizer,
                valUnit = "mg/L",
                isCustom = false
            ),
            LabelItem(
                tagId = 6,
                tagName = "二氧化碳浓度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_only_used_in_label_co2,
                valUnit = "ppm",
                isCustom = false
            )
        )
    }
}