package com.example.plantingapp

import java.time.LocalDate

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







    }

}