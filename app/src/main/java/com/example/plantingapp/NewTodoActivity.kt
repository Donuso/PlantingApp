package com.example.plantingapp

import android.content.Intent
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plantingapp.animators.ExpandAnimator
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.DateValidatorPointForward
import java.util.Calendar
import android.widget.Toast
import android.content.ContentValues

class NewTodoActivity : BaseActivity() {
    private lateinit var changeButton: MaterialCardView
    private lateinit var neverStop: MaterialCardView
    private lateinit var chooseToPick: MaterialCardView
    private lateinit var simulateDateButton: MaterialCardView
    private lateinit var back: ImageButton
    private lateinit var endTimeText: TextView
    private lateinit var pickUpHint: TextView
    private lateinit var animeNS: ExpandAnimator
    private lateinit var animeTP: ExpandAnimator
    private var ifNeverStop = true
    private val neverStopDisplacement = 147f
    private val chooseToPickDisplacement = 106f
    private var greyLine: Int = 0
    private var greyText: Int = 0
    private var themeDarkGreen: Int = 0
    private var pickedTime = MaterialDatePicker.todayInUtcMilliseconds() + 24 * 60 * 60 * 1000
    private var pickedTimeText = "----"
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)
        val todoContentEditText = findViewById<EditText>(R.id.todo_content)
        val confirmButton = findViewById<TextView>(R.id.confirm_button)
        initAll()
        changeButton.setOnClickListener {
            simulateSwitchCompat()
            ifNeverStop = !ifNeverStop
            simulateDateButton.isClickable = !ifNeverStop
        }
        simulateDateButton.setOnClickListener {
            showMaterialDatePicker()
        }
        simulateDateButton.isClickable = false
        back.setOnClickListener {
            finish() // 结束当前Activity，返回上一个界面
        }
        confirmButton.setOnClickListener {
            val todoContent = todoContentEditText.text.toString()
            val endTime = endTimeText.text.toString()
            val reminderIntervalEditText = findViewById<EditText>(R.id.reminder_interval)
            val reminderInterval = reminderIntervalEditText.text.toString().toIntOrNull()?: 0

            val dbHelper = DBHelper(this)
            val db = dbHelper.writableDatabase
            val createTime = System.currentTimeMillis()

            // 将 MutableMap 转换为 ContentValues
            val values = ContentValues()
            values.put("userId", 1) // 假设用户ID为1，需根据实际情况修改
            values.put("todoName", todoContent)
            values.put("createTime", createTime)
            values.put("endTime", endTime)
            values.put("interval", reminderInterval)
            values.put("isEnabled", 1) // 1表示启用，0表示停用，2表示其他状态，根据实际需求修改

            val newRowId = db.insert("todo", null, values)
            if (newRowId != -1L) {
                Toast.makeText(this, "待办事项添加成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "待办事项添加失败", Toast.LENGTH_SHORT).show()
            }
            db.close()

            setResult(RESULT_OK)
            finish()
        }
    }

    private fun initAll() {
        changeButton = findViewById(R.id.fake_switch_compat)
        neverStop = findViewById(R.id.left_view_never_stop)
        chooseToPick = findViewById(R.id.left_view_pick_time)
        simulateDateButton = findViewById(R.id.sim_date_button)
        back = findViewById(R.id.back_btn)
        endTimeText = findViewById(R.id.end_time)
        pickUpHint = findViewById(R.id.pick_up_hint)
        animeNS = ExpandAnimator(this, neverStop)
            .setIfFade(true)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(400)
        animeTP = ExpandAnimator(this,chooseToPick)
            .setIfFade(true)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(400)

        themeDarkGreen = ContextCompat.getColor(this,R.color.themeDarkGreen)
        greyText = ContextCompat.getColor(this,R.color.general_grey_wzc)
        greyLine = ContextCompat.getColor(this, R.color.line_grey_wzc)
    }

    private fun simulateSwitchCompat() {
        if (ifNeverStop) { // 打开日期选择
            if (pickedTimeText == "----") {
                pickUpHint.text = getString(R.string.allow_pick_up_time_chl)
            } else {
                pickUpHint.text = getString(R.string.picked_time_chl)
            }
            simulateDateButton.strokeColor = themeDarkGreen
            pickUpHint.setTextColor(themeDarkGreen)
            endTimeText.setTextColor(themeDarkGreen)
            endTimeText.text = pickedTimeText
            animeNS.setMoveDirection(1, neverStopDisplacement)
                .setFade(1f, 0f)
                .start()
            animeTP.setMoveDirection(1, chooseToPickDisplacement)
                .setFade(0f, 1f)
                .start()
        } else { // 关闭
            pickUpHint.text = getString(R.string.stop_pick_up_time_chl)
            pickUpHint.setTextColor(greyText)
            endTimeText.setTextColor(greyText)
            endTimeText.text = getString(R.string.no_time_chl)
            simulateDateButton.strokeColor = greyLine
            animeTP.setMoveDirection(1, -neverStopDisplacement)
                .setFade(1f, 0f)
                .start()
            animeNS.setMoveDirection(1, -chooseToPickDisplacement)
                .setFade(0f, 1f)
                .start()
        }
    }

    private fun showMaterialDatePicker() {
        val calendar = Calendar.getInstance()
        val todayMillis = calendar.timeInMillis
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(todayMillis + 24 * 60 * 60 * 1000)) // 今天之后（不包括今天）
            .build()
        // 配置日期选择器
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("选择结束日期") // 可自定义标题
            .setTheme(R.style.ThemeOverlay_App_MaterialDatePicker) // 使用自定义主题（可选）
            .setSelection(pickedTime) // 默认选择明天
            .setCalendarConstraints(constraints)
        val datePicker = builder.build()
        // 设置监听器
        datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedDateMillis
            val selectedDate = dateFormat.format(selectedCalendar.time)
            endTimeText.text = selectedDate
            pickedTimeText = selectedDate
            pickedTime = selectedDateMillis
            pickUpHint.text = getString(R.string.picked_time_chl)
        }
        // 设置取消监听器（可选）
        datePicker.addOnNegativeButtonClickListener {
            // 用户取消选择时的操作
        }
        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
    }
}