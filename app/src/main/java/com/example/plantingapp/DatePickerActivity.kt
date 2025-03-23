package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.DayAdapter
import com.example.plantingapp.dao.LogDAO
import com.example.plantingapp.item.DayItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class DatePickerActivity : AppCompatActivity() {

//    private lateinit var dayAdapter:
    private lateinit var days :MutableList<DayItem>
    private lateinit var dayManager: FlexboxLayoutManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dao:LogDAO

    private lateinit var backBtn: ImageButton
    private lateinit var minusYear: ImageButton
    private lateinit var plusYear: ImageButton
    private lateinit var yearDisplay: TextView
    private lateinit var minusMonth: ImageButton
    private lateinit var plusMonth: ImageButton
    private lateinit var monthDisplay: TextView
    private lateinit var daysRecyclerView: RecyclerView
    private lateinit var backTodayBg: CardView
    private lateinit var backTodayText: TextView
    private lateinit var makeSureText: TextView

    private var gpId:Int = -1
    private var chosenTime: String = ""

    private var currentYear:Int = -1
    private var currentMonth:Int = -1
    private var currentDay:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_picker_wzc)
        initViews()
        preData()
        addOnListeners()
    }

    private fun initViews(){
        backBtn = findViewById(R.id.back_btn)
        minusYear = findViewById(R.id.minus_year)
        plusYear = findViewById(R.id.plus_year)
        yearDisplay = findViewById(R.id.year_display)
        minusMonth = findViewById(R.id.minus_month)
        plusMonth = findViewById(R.id.plus_month)
        monthDisplay = findViewById(R.id.month_display)
        daysRecyclerView = findViewById(R.id.days)
        backTodayBg = findViewById(R.id.back_today_bg)
        backTodayText = findViewById(R.id.back_today_text)
        makeSureText = findViewById(R.id.make_sure_text)
    }

    private fun preData(){
        chosenTime = intent.getStringExtra("chosenTime").toString()
        gpId = intent.getIntExtra("log_group_id",-1)

        if(gpId == -1 || chosenTime.isEmpty()){
            Toast.makeText(this,"错误：未获取日志组和选择日期",Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }else{
            dao = LogDAO(this)
            dayManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
            days = dao.checkLogFromRange(
                chosenTime.substring(0..7) + "01"
            )
            currentYear = chosenTime.substring(0..3).toInt()
            currentMonth = chosenTime.substring(5..6).toInt()
            currentDay = chosenTime.substring(8..9).toInt()

            yearDisplay.text = "${currentYear}年"
            monthDisplay.text = "${currentMonth}月"

            daysRecyclerView.layoutManager = dayManager
            days[currentDay - 1].status = DayItem.STATUS_CHOSEN
            dayAdapter = DayAdapter(days,this)
            daysRecyclerView.adapter = dayAdapter
        }
    }

    private fun addOnListeners(){
        backBtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        plusYear.setOnClickListener {
            currentYear++
            refreshDisplay()
        }
        minusYear.setOnClickListener {
            currentYear--
            refreshDisplay()
        }
        plusMonth.setOnClickListener {
            currentMonth++
            refreshDisplay()
        }
        minusMonth.setOnClickListener {
            currentMonth--
            refreshDisplay()
        }
        backTodayText.setOnClickListener {
            chosenTime = Utils.timestampToDateString(System.currentTimeMillis())
            currentYear = chosenTime.substring(0..3).toInt()
            currentMonth = chosenTime.substring(5..6).toInt()
            currentDay = chosenTime.substring(8..9).toInt()
            refreshDisplay(currentDay - 1)
        }
        makeSureText.setOnClickListener {
            if(dayAdapter.lastChosen == -1){
                Toast.makeText(this,"您尚未选择日期",Toast.LENGTH_SHORT).show()
            }else{
                val returnIntent = Intent()
                val m = if(currentMonth < 10){
                    "0$currentMonth"
                }else{
                    "$currentMonth"
                }
                val originDay = dayAdapter.lastChosen + 1
                Log.v("originDay","$originDay")
                val d = if(originDay < 10){
                    "0$originDay"
                }else{
                    "$originDay"
                }
                Log.v("processedDayStr",d)
                returnIntent.putExtra("selected_date","$currentYear-$m-$d")
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun refreshDisplay(choose:Int = -1){
        yearDisplay.text = "${currentYear}年"
        monthDisplay.text = "${currentMonth}月"
        dayAdapter.dayList = dao.checkLogFromRange(
            "${currentYear}-${currentMonth}-01"
        )
        when(choose){
            -1 -> {
                dayAdapter.lastChosen = -1
            }
            else -> {
                dayAdapter.lastChosen = choose
                dayAdapter.dayList[choose].status = DayItem.STATUS_CHOSEN
            }
        }
        dayAdapter.notifyDataSetChanged()
    }
}