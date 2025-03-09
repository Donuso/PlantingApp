package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class DateSelectionActivity : AppCompatActivity() {

    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var todayButton: Button
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_selection)
        enableEdgeToEdge()

        yearSpinner = findViewById(R.id.year_spinner)
        monthSpinner = findViewById(R.id.month_spinner)
        datePicker = findViewById(R.id.date_picker)
        todayButton = findViewById(R.id.today_button)
        confirmButton = findViewById(R.id.confirm_button)

        // Initialize spinners with years and months
        val years = resources.getStringArray(R.array.years)
        val months = resources.getStringArray(R.array.months)
        yearSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)

        // Set up the date picker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        datePicker.init(year, month, day) { _, selectedYear, selectedMonth, selectedDay ->
            // Update the spinners to reflect the selected date
            yearSpinner.setSelection(selectedYear - 2023)
            monthSpinner.setSelection(selectedMonth)
        }

        // Set up the "Today" button
        todayButton.setOnClickListener {
            val todayCalendar = Calendar.getInstance()
            val todayYear = todayCalendar.get(Calendar.YEAR)
            val todayMonth = todayCalendar.get(Calendar.MONTH)
            val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)
            datePicker.updateDate(todayYear, todayMonth, todayDay)
            yearSpinner.setSelection(todayYear - 2023)
            monthSpinner.setSelection(todayMonth)
        }

        // Set up the "Confirm" button
        // 在 DateSelectionActivity 中
        confirmButton.setOnClickListener {
            // 获取选中的日期
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month + 1 // 月份从 0 开始，需要加 1
            val selectedDay = datePicker.dayOfMonth

            // 将日期格式化为字符串
            val dateString = "${selectedYear}年${selectedMonth}月${selectedDay}日"

            // 创建 Intent 并传递数据
            val intent = Intent(this, LogDetailActivity::class.java).apply {
                putExtra("DATE", dateString) // 传递日期
                putExtra("PLANT_NAME", intent.getStringExtra("PLANT_NAME")) // 如果需要传递植物名称
            }

            // 启动 LogDetailActivity
            startActivity(intent)
        }
    }

    private fun enableEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            // Apply the insets to the view
            insets
        }
    }
}