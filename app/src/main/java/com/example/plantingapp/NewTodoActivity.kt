package com.example.plantingapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.plantingapp.adapter.GroupSpinnerAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.converter.TodoConverter
import com.example.plantingapp.dao.ToDoDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.UniversalTodoItem
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
    private lateinit var dao:ToDoDAO
    private lateinit var todoContentEditText:EditText
    private lateinit var confirm:TextView
    private lateinit var title:TextView
    private lateinit var intervalText: EditText
    private lateinit var groupSpinner:Spinner
    private lateinit var switchCompat :SwitchCompat
    private lateinit var attachTitleTextView:TextView
    private lateinit var spinnerAdapter: GroupSpinnerAdapter

    private var ifNeverStop = true
    private val neverStopDisplacement = 147f
    private val chooseToPickDisplacement = 106f
    private var greyLine: Int = 0
    private var greyText: Int = 0
    private var themeDarkGreen: Int = 0
    private var pickedTime = MaterialDatePicker.todayInUtcMilliseconds() + 24 * 60 * 60 * 1000
    private var pickedTimeText = "----"
    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    private var transferredItem:UniversalTodoItem? = null
    private var groupAttachMode = GROUP_ATTACH_MODE_NO_ATTACH

    companion object{
        const val GROUP_ATTACH_MODE_DO_ATTACH = 1
        const val GROUP_ATTACH_MODE_NO_ATTACH = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)

        initViewsAndSoOn()
        initData()
        setOnListeners()
    }

    private fun initViewsAndSoOn() {
        changeButton = findViewById(R.id.fake_switch_compat)
        neverStop = findViewById(R.id.left_view_never_stop)
        chooseToPick = findViewById(R.id.left_view_pick_time)
        simulateDateButton = findViewById(R.id.sim_date_button)
        title = findViewById(R.id.title)
        back = findViewById(R.id.back_btn)
        endTimeText = findViewById(R.id.end_time)
        pickUpHint = findViewById(R.id.pick_up_hint)
        todoContentEditText = findViewById(R.id.todo_content)
        confirm = findViewById(R.id.confirm_button)
        intervalText = findViewById(R.id.reminder_interval)
        groupSpinner = findViewById(R.id.plant_group_spinner)
        switchCompat = findViewById(R.id.switch_compat)
        attachTitleTextView = findViewById(R.id.group_binder_title)
        groupSpinner.dropDownVerticalOffset = Utils.dpToPx(this,40f)

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

        dao = ToDoDAO(this)
    }

    private fun setOnListeners(){
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
            onBackPressedDispatcher.onBackPressed()
        }
        confirm.setOnClickListener {
            newConfirm()
        }
        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                switchAttachGroupMode(GROUP_ATTACH_MODE_DO_ATTACH)
            }else{
                switchAttachGroupMode(GROUP_ATTACH_MODE_NO_ATTACH)
            }
        }
    }

    private fun initData(){
        initGroupBinding()
        val preData = intent.getStringExtra("alter_todo")
        if(preData != null){
            title.text = "修改待办"
            transferredItem = TodoConverter.fromJsonToItem(preData)
            intervalText.setText("${transferredItem!!.interval}")
            todoContentEditText.setText(transferredItem!!.todoName)
            if(transferredItem!!.endTime != null){
                ifNeverStop = true
                simulateSwitchCompat()
                ifNeverStop = false
                endTimeText.text = transferredItem!!.endTime?.let { Utils.dateStringToCH(it) }
                pickedTimeText = transferredItem!!.endTime.toString()
                pickedTime = Utils.dateStringToTimestamp(transferredItem!!.endTime.toString())
                pickUpHint.text = getString(R.string.picked_time_chl)
            }else{
                ifNeverStop = false
                simulateSwitchCompat()
                ifNeverStop = true
            }
            var cnt = 0
            if(transferredItem!!.logGroupId != null){
                for(it in spinnerAdapter.items){
                    if(it.id == transferredItem!!.logGroupId){
                        groupSpinner.setSelection(cnt)
                        switchAttachGroupMode(
                            GROUP_ATTACH_MODE_DO_ATTACH
                        )
                        switchCompat.isChecked = true
                    }
                    cnt++
                }
            }
            // 预留：系统通知选项
        }
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

    private fun newConfirm() {
        val todoContent = todoContentEditText.text.toString()
        val interval = intervalText.text.toString()
        if (todoContent.isEmpty()) {
            Toast.makeText(this, "待办名称不能为空", Toast.LENGTH_SHORT).show()
        } else if (todoContent.length > 15) {
            Toast.makeText(this, "待办名称不能超过15个字", Toast.LENGTH_SHORT).show()
        } else if (interval.isEmpty() || interval.toInt() < 0 || interval.toInt() > 365) {
            Toast.makeText(this, "请正确填写时间间隔", Toast.LENGTH_SHORT).show()
        } else {
            when (transferredItem) {
                null -> {
                    val todoId = dao.insertTodo(
                        userId = DataExchange.USERID!!.toInt(),
                        todoName = todoContent,
                        interval = interval.toInt(),
                        isEnabled = UniversalTodoItem.STATUS_RUNNING,
                        endTime = if (ifNeverStop || pickedTimeText == "----") {
                            null
                        } else {
                            Utils.timestampToDateString(pickedTime)
                        },
                        logGroupId = if (groupAttachMode == GROUP_ATTACH_MODE_NO_ATTACH || groupSpinner.selectedItemPosition == 0) {
                            null
                        } else {
                            spinnerAdapter.items[groupSpinner.selectedItemPosition].id
                        }
                    )
                    setReminder(todoId.toInt(), interval.toInt())
                    Toast.makeText(this, "新建待办成功", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    transferredItem!!.todoName = todoContent
                    transferredItem!!.interval = interval.toInt()
                    transferredItem!!.endTime = if (ifNeverStop || pickedTimeText == "----") {
                        null
                    } else {
                        Utils.timestampToDateString(pickedTime)
                    }
                    transferredItem!!.logGroupId = if (groupAttachMode == GROUP_ATTACH_MODE_NO_ATTACH || groupSpinner.selectedItemPosition == 0) {
                        null
                    } else {
                        spinnerAdapter.items[groupSpinner.selectedItemPosition].id
                    }
                    dao.updateTodo(transferredItem!!)
                    setReminder(transferredItem!!.todoId, interval.toInt())
                    Toast.makeText(this, "修改待办成功", Toast.LENGTH_SHORT).show()
                }
            }
            startActivity(Intent(this, AllToDoActivity::class.java))
            finish()
        }
    }

    private fun setReminder(todoId: Int, interval: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TodoReminderReceiver::class.java)
        intent.putExtra("todoId", todoId)
        intent.putExtra("todoName", todoContentEditText.text.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            todoId,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, interval)
        val triggerTime = calendar.timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun initGroupBinding() {
        spinnerAdapter = GroupSpinnerAdapter(this){ pos ->
            groupSpinner.setSelection(pos)
            groupSpinner.performClick()
        }
        groupSpinner.adapter = spinnerAdapter.apply {
            items.addAll(
                dao.getLogGroupsByUserId(DataExchange.USERID!!.toInt())
            )
        }
        groupSpinner.setSelection(0)
        groupSpinner.visibility = View.GONE
    }

    // Switch逻辑封装
    private fun switchAttachGroupMode(
        mode:Int
    ) {
        when(mode){
            GROUP_ATTACH_MODE_DO_ATTACH -> {
                if(spinnerAdapter.items.size == 1){
                    Toast.makeText(this, "未创建任何日志组", Toast.LENGTH_SHORT).show()
                    switchCompat.isChecked = false
                    groupAttachMode = GROUP_ATTACH_MODE_NO_ATTACH
                    attachTitleTextView.setTextColor(ContextCompat.getColor(this,R.color.general_grey_wzc))
                }else{
                    groupSpinner.visibility = View.VISIBLE
                    groupAttachMode = GROUP_ATTACH_MODE_DO_ATTACH
                    attachTitleTextView.setTextColor(ContextCompat.getColor(this,R.color.themeDarkGreen))
                }
            }
            GROUP_ATTACH_MODE_NO_ATTACH -> {
                groupSpinner.visibility = View.GONE
                groupAttachMode = GROUP_ATTACH_MODE_NO_ATTACH
                attachTitleTextView.setTextColor(ContextCompat.getColor(this,R.color.general_grey_wzc))
            }
        }

    }

    // 原完成逻辑
//    private fun oldConfirm(){
//        val reminderIntervalEditText = findViewById<EditText>(R.id.reminder_interval)
//        val reminderInterval = reminderIntervalEditText.text.toString().toIntOrNull()?: 0
//        val todoContent = todoContentEditText.text.toString()
//        if(todoContent.isEmpty()){
//            Toast.makeText(this,"请填写待办名称",Toast.LENGTH_SHORT).show()
//        }else if(reminderInterval < 0 || reminderInterval > 365){
//            Toast.makeText(this,"请正确填写间隔天数",Toast.LENGTH_SHORT).show()
//        }else{
//            val endTime = endTimeText.text.toString()
//            val dbHelper = DBHelper(this)
//            val db = dbHelper.writableDatabase
//            val createTime = System.currentTimeMillis()
//
//            // 将 MutableMap 转换为 ContentValues
//            val values = ContentValues()
//            values.put("userId", 1) // 假设用户ID为1，需根据实际情况修改
//            values.put("todoName", todoContent)
//            values.put("createTime", createTime)
//            values.put("endTime", endTime)
//            values.put("interval", reminderInterval)
//            values.put("isEnabled", 1) // 1表示启用，0表示停用，2表示其他状态，根据实际需求修改
//
//            val newRowId = db.insert("todo", null, values)
//            if (newRowId != -1L) {
//                Toast.makeText(this, "待办事项添加成功", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "待办事项添加失败", Toast.LENGTH_SHORT).show()
//            }
//            db.close()
//            setResult(RESULT_OK)
//            finish()
//        }
//    }
}