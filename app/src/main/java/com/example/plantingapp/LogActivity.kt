package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.common.BaseConfig
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.plantingapp.adapter.AddedLabelAdapter
import com.example.plantingapp.adapter.LogPicAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.item.LabelItem
import com.example.plantingapp.item.LogPicItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class LogActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var logGroupName: TextView
    private lateinit var menuBtn: ImageButton
    private lateinit var logDate: TextView
    private lateinit var backToday: TextView
    private lateinit var datePicker: ImageButton
    private lateinit var logAnalyzer: ImageButton
    private lateinit var deleteLabelHint: TextView
    private lateinit var labels: RecyclerView
    private lateinit var noLabelHintText: TextView
    private lateinit var addLabel: View
    private lateinit var recordTemp: TextView
    private lateinit var deletePicHint: TextView
    private lateinit var logPics: RecyclerView
    private lateinit var logText: TextView
    private lateinit var addPic: View
    private lateinit var logOptions: LinearLayout
    private lateinit var deleteLabel: TextView
    private lateinit var deletePic: TextView
    private lateinit var cancelModule: CardView
    private lateinit var cancel: TextView
    private lateinit var backLine:View
    private lateinit var addLabelModule: RelativeLayout
    private lateinit var addPicModule: RelativeLayout

    private lateinit var labelFlexboxLayoutManager: FlexboxLayoutManager
    private lateinit var picFlexboxLayoutManager: FlexboxLayoutManager

    private lateinit var menuAnimator: FadeAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var cancelBackAnimator: ExpandAnimator
    private lateinit var optionsAnimator: FadeAnimator

    private val cancelMovement = 100f
    private var groupId = -1
    private val labelD: MutableList<LabelItem> = mutableListOf()
    private val picD: MutableList<LogPicItem> = mutableListOf()

    private lateinit var picAdapter: LogPicAdapter
    private lateinit var labelAdapter: AddedLabelAdapter

    private val datePickerListener = View.OnClickListener {
        datePickerLauncher.launch(
            Intent(
                this,
                DatePickerActivity::class.java
            ).putExtra(
                "log_group_id",
                intent.getIntExtra(
                    "log_group_id",
                    -1)
            )
        )
    }

    private val analyzeListener = View.OnClickListener {
        startActivity(
            Intent(this,LogDataAnalyzeActivity::class.java)
                .putExtra("log_group_id",
                    intent.getIntExtra("log_group_id",-1)
                )
        )
    }

    private val backTodayListener = View.OnClickListener {
        logDate.text = Utils.timestampToDateString(System.currentTimeMillis())
        // 这里可以添加逻辑来加载今天的日志数据
    }

    private val datePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedDate = result.data?.getStringExtra("selected_date")
            if (!selectedDate.isNullOrEmpty()) {
                if(selectedDate == Utils.timestampToDateString(System.currentTimeMillis())){
                    isToday(true)
                    backToday.setOnClickListener(null)
                }else{
                    isToday(false)
                    backToday.setOnClickListener(backTodayListener)
                }
                logDate.text = selectedDate // 将日期显示在 logDate 控件上
                // 这里可以添加数据库查询逻辑，根据日期获取日志数据
            }
        }
    }


    private lateinit var picturePickerLauncher: ImagePickerLauncher

    private val labelLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
//            val labelName = result.data?.getStringExtra("selected_label") // 假设返回的是标签名称
//            if (!labelName.isNullOrEmpty()) {
//
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_wzc)

        preData()

        initAllViews()

        initAllControllersAndAnimators()

        addOnListeners()

        firstLoadingLog()


    }

    private fun initAllViews() {
        // Initialize Toolbar buttons
        backBtn = findViewById(R.id.back_btn)
        logGroupName = findViewById(R.id.log_group_name)
        menuBtn = findViewById(R.id.menu_btn)

        // Initialize Date section
        logDate = findViewById(R.id.log_date)
        backToday = findViewById(R.id.back_today)
        backLine = findViewById(R.id.back_line)
        datePicker = findViewById(R.id.date_picker)
        logAnalyzer = findViewById(R.id.log_analyzer)

        // Initialize Labels section
        deleteLabelHint = findViewById(R.id.delete_label_hint)
        labels = findViewById(R.id.labels)
        noLabelHintText = findViewById(R.id.no_label_hint_text)
        addLabel = findViewById(R.id.add_label)


        // Initialize Temperature recording section
        recordTemp = findViewById(R.id.record_temp)

        // Initialize Pictures section
        deletePicHint = findViewById(R.id.delete_pic_hint)
        logPics = findViewById(R.id.log_pics)
        logText = findViewById(R.id.log_text)
        addPic = findViewById(R.id.add_pic)

        // Initialize Options menu
        logOptions = findViewById(R.id.log_options)
        deleteLabel = findViewById(R.id.delete_label)
        deletePic = findViewById(R.id.delete_pic)

        cancelModule = findViewById(R.id.cancel_module)
        cancel = findViewById(R.id.cancel_text)

        addPicModule = findViewById(R.id.add_pic_module)
        addLabelModule = findViewById(R.id.add_label_module)

    }

    private fun initAllControllersAndAnimators(){
        labelFlexboxLayoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        picFlexboxLayoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

        logPics.layoutManager = picFlexboxLayoutManager
        labels.layoutManager = labelFlexboxLayoutManager
        picAdapter = LogPicAdapter(picD,this)
//        picAdapter.setHasStableIds(true)
        labelAdapter = AddedLabelAdapter(labelD,this)
//        labelAdapter.setHasStableIds(true)
        logPics.adapter = picAdapter
        labels.adapter = labelAdapter

        menuAnimator = FadeAnimator(menuBtn)
            .setDuration(200)
        optionsAnimator = FadeAnimator(logOptions)
            .setDuration(100)
        cancelAnimator = ExpandAnimator(this,cancelModule)
            .setMoveDirection(2,-cancelMovement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
        cancelBackAnimator = ExpandAnimator(this,cancelModule)
            .setMoveDirection(2,cancelMovement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)

        picturePickerLauncher = registerImagePicker { result: List<Image> ->
            for(it in result){
                val originalUri = it.uri
                val storedUri = Utils.storeSinglePicture(this, originalUri)
                storedUri?.let { uri ->
                    // 使用存储后的URI进行操作
                    picD.add(
                        LogPicItem(
                            1,
                            uri.toString()
                        )
                    )
                    Log.v("storedUri",uri.toString())
                    picAdapter.notifyItemInserted(picD.size - 1)
                }
            }

        }
    }

    private fun addOnListeners(){
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        menuBtn.setOnClickListener {
            optionsAnimator.start(true)
        }
        logOptions.setOnClickListener {
            optionsAnimator.start(false)
        }
        datePicker.setOnClickListener(datePickerListener)
        logAnalyzer.setOnClickListener(analyzeListener)
        backToday.setOnClickListener(backTodayListener)
        addPic.setOnClickListener {
            picturePickerLauncher
                .launch(
                    config = ImagePickerConfig {
                    isShowCamera = false
                    arrowColor = R.color.themeDarkGreen
                    }
                )
        }
        addLabel.setOnClickListener {
            labelLauncher.launch(
                Intent(this,NewLabelActivity::class.java)
            )
        }
        deleteLabel.setOnClickListener {
            optionsAnimator.start(false)
            if(labelD.size == 0){
                Toast.makeText(this,"暂未添加标签",Toast.LENGTH_SHORT).show()
            }else{
                deleteLabelHint.visibility = View.VISIBLE
                addPicModule.visibility = View.GONE
                addLabelModule.visibility = View.GONE
                datePicker.setOnClickListener(null)
                logAnalyzer.setOnClickListener(null)
                backToday.setOnClickListener(null)
                menuAnimator.start(false)
                alterLabelMode(LabelItem.MODE_DEL)
                cancelAnimator.start()
            }

        }
        deletePic.setOnClickListener {
            optionsAnimator.start(false)
            if(picD.size == 0){
                Toast.makeText(this,"暂未添加图片",Toast.LENGTH_SHORT).show()
            }else{
                optionsAnimator.start(false)
                deletePicHint.visibility = View.VISIBLE
                addPicModule.visibility = View.GONE
                addLabelModule.visibility = View.GONE
                datePicker.setOnClickListener(null)
                logAnalyzer.setOnClickListener(null)
                backToday.setOnClickListener(null)
                menuAnimator.start(false)
                alterPicMode(LogPicItem.MODE_DEL)
                cancelAnimator.start()
            }
        }
        cancel.setOnClickListener {
            if(deletePicHint.visibility == View.VISIBLE){
                deletePicHint.visibility = View.GONE
                backToNormal(TYPE_PIC)
            }else if(deleteLabelHint.visibility == View.VISIBLE) {
                deleteLabelHint.visibility = View.GONE
                backToNormal(TYPE_Label)
            }
            addPicModule.visibility = View.VISIBLE
            addLabelModule.visibility = View.VISIBLE
            cancelBackAnimator.start()
            datePicker.setOnClickListener(datePickerListener)
            logAnalyzer.setOnClickListener(analyzeListener)
            backToday.setOnClickListener(backTodayListener)
            menuAnimator.start(true)
        }
    }

    private fun alterPicMode(mode:Int){
        when(mode){
            LogPicItem.MODE_DISPLAY -> {
                for(it in picD){
                    it.status = LogPicItem.MODE_DISPLAY
                }
            }
            LogPicItem.MODE_DEL -> {
                for(it in picD){
                    it.status = LogPicItem.MODE_DEL
                }
            }
        }
        picAdapter.notifyDataSetChanged()
    }

    private fun alterLabelMode(mode:Int){
        when(mode){
            LabelItem.MODE_DISPLAY -> {
                for(it in labelD){
                    it.status = LabelItem.MODE_DISPLAY
                }
            }
            LabelItem.MODE_DEL -> {
                for(it in labelD){
                    it.status = LabelItem.MODE_DEL
                }
            }
        }
        labelAdapter.notifyDataSetChanged()
    }

    private fun backToNormal(type:Int){
        when(type){
            TYPE_PIC -> {
                alterPicMode(LogPicItem.MODE_DISPLAY)
            }
            TYPE_Label -> {
                alterLabelMode(LabelItem.MODE_DISPLAY)
            }
        }
    }

    // 数据加载入口 含数据库
    private fun firstLoadingLog(){
        groupId = intent.getIntExtra("log_group_id",-1)
        logGroupName.text = intent.getStringExtra("log_group_name")
        if(groupId == -1){
            Toast.makeText(this,"数据加载错误：未知日志组",Toast.LENGTH_SHORT).show()
        }else{
            logDate.text = Utils.dateStringToCH(Utils.timestampToDateString(System.currentTimeMillis()))
            isToday(true)
        }
    }

    private fun isToday(isToday:Boolean){
        when(isToday){
            true -> {
                backToday.setTextColor(
                    ContextCompat.getColor(this,R.color.general_grey_wzc)
                )
                backLine.setBackgroundColor(
                    ContextCompat.getColor(this,R.color.general_grey_wzc)
                )
            }
            false -> {
                backToday.setTextColor(
                    ContextCompat.getColor(this,R.color.themeDarkGreen)
                )
                backLine.setBackgroundColor(
                    ContextCompat.getColor(this,R.color.themeDarkGreen)
                )
            }
        }
    }

    private fun preData(){
        labelD.add(
            LabelItem(
                tagId = 1,
                tagName = "叶片状态",
                tagType = LabelItem.TYPE_STATUS,
                tagIcon = R.drawable.icon_main,
                isCustom = false,
                valStatus = 1,
                hint = "长得非常好，我很喜欢"
            )
        )
        labelD.add(
            LabelItem(
                tagId = 1,
                tagName = "生长高度",
                tagType = LabelItem.TYPE_DATA,
                tagIcon = R.drawable.icon_main,
                valUnit = "cm",
                isCustom = false,
                valData = 10.1
            )
        )
    }

    companion object {
        val TYPE_PIC = 1
        val TYPE_Label = 2
    }

}