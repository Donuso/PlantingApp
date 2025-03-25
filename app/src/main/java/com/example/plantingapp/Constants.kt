package com.example.plantingapp


import java.time.LocalDate
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
        //定位请求码
        const val REQUEST_CODE_LOCATION = 1001
        // 2025-2027 年的节气列表
        val SOLAR_TERMS: List<Pair<String, LocalDate>> = listOf(
            "立春" to LocalDate.of(2025, 2, 4),
            "雨水" to LocalDate.of(2025, 2, 19),
            "惊蛰" to LocalDate.of(2025, 3, 6),
            "春分" to LocalDate.of(2025, 3, 20),
            "清明" to LocalDate.of(2025, 4, 5),
            "谷雨" to LocalDate.of(2025, 4, 20),
            "立夏" to LocalDate.of(2025, 5, 6),
            "小满" to LocalDate.of(2025, 5, 21),
            "芒种" to LocalDate.of(2025, 6, 6),
            "夏至" to LocalDate.of(2025, 6, 22),
            "小暑" to LocalDate.of(2025, 7, 7),
            "大暑" to LocalDate.of(2025, 7, 23),
            "立秋" to LocalDate.of(2025, 8, 8),
            "处暑" to LocalDate.of(2025, 8, 23),
            "白露" to LocalDate.of(2025, 9, 8),
            "秋分" to LocalDate.of(2025, 9, 23),
            "寒露" to LocalDate.of(2025, 10, 8),
            "霜降" to LocalDate.of(2025, 10, 24),
            "立冬" to LocalDate.of(2025, 11, 8),
            "小雪" to LocalDate.of(2025, 11, 23),
            "大雪" to LocalDate.of(2025, 12, 7),
            "冬至" to LocalDate.of(2025, 12, 22),

            "立春" to LocalDate.of(2026, 2, 4),
            "雨水" to LocalDate.of(2026, 2, 19),
            "惊蛰" to LocalDate.of(2026, 3, 6),
            "春分" to LocalDate.of(2026, 3, 20),
            "清明" to LocalDate.of(2026, 4, 5),
            "谷雨" to LocalDate.of(2026, 4, 20),
            "立夏" to LocalDate.of(2026, 5, 6),
            "小满" to LocalDate.of(2026, 5, 21),
            "芒种" to LocalDate.of(2026, 6, 6),
            "夏至" to LocalDate.of(2026, 6, 22),
            "小暑" to LocalDate.of(2026, 7, 7),
            "大暑" to LocalDate.of(2026, 7, 23),
            "立秋" to LocalDate.of(2026, 8, 8),
            "处暑" to LocalDate.of(2026, 8, 23),
            "白露" to LocalDate.of(2026, 9, 8),
            "秋分" to LocalDate.of(2026, 9, 23),
            "寒露" to LocalDate.of(2026, 10, 8),
            "霜降" to LocalDate.of(2026, 10, 24),
            "立冬" to LocalDate.of(2026, 11, 8),
            "小雪" to LocalDate.of(2026, 11, 23),
            "大雪" to LocalDate.of(2026, 12, 7),
            "冬至" to LocalDate.of(2026, 12, 22),

            "立春" to LocalDate.of(2027, 2, 4),
            "雨水" to LocalDate.of(2027, 2, 19),
            "惊蛰" to LocalDate.of(2027, 3, 6),
            "春分" to LocalDate.of(2027, 3, 20),
            "清明" to LocalDate.of(2027, 4, 5),
            "谷雨" to LocalDate.of(2027, 4, 20),
            "立夏" to LocalDate.of(2027, 5, 6),
            "小满" to LocalDate.of(2027, 5, 21),
            "芒种" to LocalDate.of(2027, 6, 6),
            "夏至" to LocalDate.of(2027, 6, 22),
            "小暑" to LocalDate.of(2027, 7, 7),
            "大暑" to LocalDate.of(2027, 7, 23),
            "立秋" to LocalDate.of(2027, 8, 8),
            "处暑" to LocalDate.of(2027, 8, 23),
            "白露" to LocalDate.of(2027, 9, 8),
            "秋分" to LocalDate.of(2027, 9, 23),
            "寒露" to LocalDate.of(2027, 10, 8),
            "霜降" to LocalDate.of(2027, 10, 24),
            "立冬" to LocalDate.of(2027, 11, 8),
            "小雪" to LocalDate.of(2027, 11, 23),
            "大雪" to LocalDate.of(2027, 12, 7),
            "冬至" to LocalDate.of(2027, 12, 22)
        )




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