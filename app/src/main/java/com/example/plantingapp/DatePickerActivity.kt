package com.example.plantingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.DayAdapter
import com.example.plantingapp.animators.ExpandAnimator
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

    private lateinit var backTodayListener: View.OnClickListener

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
            days = dao.getRecordedDaysInMonth(
                gpId,
                chosenTime.substring(0..6)
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
        backTodayListener = View.OnClickListener {
            chosenTime = Utils.timestampToDateString(System.currentTimeMillis())
            currentYear = chosenTime.substring(0..3).toInt()
            currentMonth = chosenTime.substring(5..6).toInt()
            currentDay = chosenTime.substring(8..9).toInt()
            backTodayBg.setCardBackgroundColor(
                ContextCompat.getColor(this,R.color.general_grey_wzc)
            )
            backTodayBg.cardElevation = 0f
            refreshDisplay()
            backTodayText.setOnClickListener(null)
        }

        backBtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        plusYear.setOnClickListener {
            currentYear++
            backTodayBg.setCardBackgroundColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            backTodayBg.cardElevation = 5f
            refreshDisplay()
            backTodayText.setOnClickListener(backTodayListener)
        }
        minusYear.setOnClickListener {
            currentYear = if (currentYear == 1){
                1
            }else{
                currentYear - 1
            }
            backTodayBg.setCardBackgroundColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            backTodayBg.cardElevation = 5f
            refreshDisplay()
            backTodayText.setOnClickListener(backTodayListener)
        }
        plusMonth.setOnClickListener {
            currentMonth = if(currentMonth == 12){
                12
            }else{
                currentMonth + 1
            }
            backTodayBg.setCardBackgroundColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            backTodayBg.cardElevation = 5f
            refreshDisplay()
            backTodayText.setOnClickListener(backTodayListener)
        }
        minusMonth.setOnClickListener {
            currentMonth = if(currentMonth == 1){
                1
            }else{
                currentMonth - 1
            }
            backTodayBg.setCardBackgroundColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            backTodayBg.cardElevation = 5f
            refreshDisplay()
            backTodayText.setOnClickListener(backTodayListener)
        }
        backTodayText.setOnClickListener(null)
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

    private fun refreshDisplay(){
        yearDisplay.text = "${currentYear}年"
        monthDisplay.text = "${currentMonth}月"
        val m = if(currentMonth < 10){
            "0$currentMonth"
        }else{
            "$currentMonth"
        }
        dayAdapter.dayList = dao.getRecordedDaysInMonth(
            gpId,
            "${currentYear}-$m"
        )
        dayAdapter.lastChosen = -1
        dayAdapter.notifyDataSetChanged()
    }
}