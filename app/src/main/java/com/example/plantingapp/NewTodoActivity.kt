package com.example.plantingapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantingapp.fragments.TodoFragment
import java.util.Calendar

class NewTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_todo)

        val switchCompat = findViewById<Switch>(R.id.switchcompat)
        val dateButton = findViewById<Button>(R.id.date_button)

        switchCompat.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                dateButton.isEnabled = isChecked
            }
        })

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            finish() // 结束当前Activity，返回上一个界面
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "${selectedYear}年${selectedMonth + 1}月${selectedDay}日"
                // 更新 Button 的文本为所选日期
                val dateButton = findViewById<Button>(R.id.date_button)
                dateButton.text = selectedDate
            },
            year,
            month,
            day
        ).show()
    }
}