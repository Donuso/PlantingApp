package com.example.plantingapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.fragments.TodoFragment
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class NewTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)

        val switchCompat = findViewById<Switch>(R.id.switchcompat)
        val dateButton = findViewById<Button>(R.id.date_button)
        val testbtn = findViewById<Button>(R.id.test)
        val NeverStop = findViewById<MaterialCardView>(R.id.left_view_never_stop)
        val TimePicker = findViewById<MaterialCardView>(R.id.left_view_pick_time)
        var ifNeverStop = true

        val AnimeFadeNS = FadeAnimator(NeverStop).setDuration(200)
        val AnimeExNS = ExpandAnimator(this,NeverStop).setDuration(200)

        val AnimeFadeTP = FadeAnimator(TimePicker).setDuration(200)
        val AnimeExTP = ExpandAnimator(this,TimePicker).setDuration(200)

        val shortDis = 147f
        val shortDisPx = Utils.dpToPx(this,shortDis).toFloat()
        val longDis = 105f
        val longDisPx = Utils.dpToPx(this,longDis).toFloat()

        testbtn.setOnClickListener{
            if(ifNeverStop){
                NeverStop.translationX = 0f
                TimePicker.translationX = 0f
                ifNeverStop = false
                AnimeFadeNS.start(false)
                AnimeExNS.setMoveDirection(1,shortDis).start()
                AnimeFadeTP.start(true)
                AnimeExTP.setMoveDirection(1,longDis).start()
            }else{
                NeverStop.translationX = 50f
                TimePicker.translationX = 50f
                ifNeverStop = true
                AnimeFadeTP.start(false)
                AnimeExTP.setMoveDirection(1,-longDis).start()
                AnimeFadeNS.start(true)
                AnimeExNS.setMoveDirection(1,-shortDis).start()
            }
        }

        switchCompat.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                dateButton.isEnabled = isChecked
            }
        })

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        findViewById<ImageButton>(R.id.back_btn).setOnClickListener {
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