package com.example.plantingapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.plantingapp.adapter.AddedLabelAdapter
import com.example.plantingapp.adapter.LogPicAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.dao.LabelDAO
import com.example.plantingapp.dao.LogDAO
import com.example.plantingapp.item.LabelItem
import com.example.plantingapp.utils.LabelItemConverter
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
    private lateinit var line: View
    private lateinit var rootView :RelativeLayout

    private lateinit var labelFlexboxLayoutManager: FlexboxLayoutManager
    private lateinit var picFlexboxLayoutManager: FlexboxLayoutManager

    private lateinit var menuAnimator: FadeAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var cancelBackAnimator: ExpandAnimator
    private lateinit var optionsAnimator: FadeAnimator
    private lateinit var picturePickerLauncher: ImagePickerLauncher // 该项初始化被放置到下方的统一初始化区域


    private val cancelMovement = 100f
    private var groupId = -1
    private var logId = -1
    private lateinit var labelD: MutableList<LabelItem>
    private lateinit var picD: MutableList<LogPicItem>
    private var chosenTime:String? = Utils.timestampToDateString(System.currentTimeMillis())
    private val weatherSPName = "weather_info" //  tem1  tem2
    private var temp:String? = null
    private lateinit var imm:InputMethodManager

    private lateinit var picAdapter: LogPicAdapter
    private lateinit var labelAdapter: AddedLabelAdapter
    private lateinit var daoLog: LogDAO
    private lateinit var daoLabel: LabelDAO


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
            ).putExtra(
                "chosenTime",
                chosenTime
            )
        )
    }

    private val datePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedDate = result.data?.getStringExtra("selected_date")
            if (!selectedDate.isNullOrEmpty()) {
                chosenTime = selectedDate
                temp = null
                reLoadingLog()
                Toast.makeText(this,"已加载这一天的日志",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"发生错误：获取日期失败",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val analyzeListener = View.OnClickListener {
        startActivity(
            Intent(this,LogDataAnalyzeActivity::class.java)
                .putExtra("log_group_id",
                    intent.getIntExtra("log_group_id",-1)
                )
        )
    }

    private val tempListener = View.OnClickListener {
        val sp = getSharedPreferences(weatherSPName,MODE_PRIVATE)
        val tem1 = sp.getString("tem1",null)
        val tem2 = sp.getString("tem2",null)
        if(tem1 == null || tem2 == null){
            Toast.makeText(this,"无法加载天气",Toast.LENGTH_SHORT).show()
        }else{
            temp = "当日温度   $tem1℃ ~ $tem2℃"
            recordTemp.text = temp
            daoLog.updateWeatherTemperatureRange(logId,temp!!)
            daoLog.updateLastModified(groupId)
        }
    }

    private val backTodayListener = View.OnClickListener {
        chosenTime = Utils.timestampToDateString(
            System.currentTimeMillis()
        )
        logDate.text = Utils.dateStringToCH(
            chosenTime!!
        )
        isToday(true)
        reLoadingLog()
        Toast.makeText(this,"已返回到今日日志",Toast.LENGTH_SHORT).show()
    }


    private val labelLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val label = result.data?.getStringExtra("label_data")
            if (!label.isNullOrEmpty()) {
                val d = LabelItemConverter.jsonToLabel(label)
                var sameLabelIndex = -1
                for(i in 0..<labelD.size){
                    if(!labelD[i].isCustom && !d.isCustom && labelD[i].tagId == d.tagId && labelD[i].tagType == d.tagType){
                        sameLabelIndex = i
                        // 类间，两种不同的内置标签类型的比较，因两种类型的内置标签共用一套id
                    }
                    if(labelD[i].isCustom && d.isCustom && labelD[i].tagId == d.tagId){
                        sameLabelIndex = i
                        // 数据库的自定义标签，直接使用id进行比较（数据库内存储的自定义标签的类型不共用id）
                    }
                }
                if(sameLabelIndex != -1){
                    labelD[sameLabelIndex] = d
                    labelAdapter.notifyItemChanged(sameLabelIndex)
                    daoLabel.updateLogTag(logId,d)
                    daoLog.updateLastModified(groupId)

                }else{
                    labelD.add(d)
                    labelAdapter.notifyItemInserted(labelD.size - 1)
                    daoLabel.insertLogTag(logId,d)
                    daoLog.updateLastModified(groupId)
                }
                Log.v("newAddLabel",d.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_wzc)
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        daoLog = LogDAO(this)
        daoLabel = LabelDAO(this)

        initAllViews()

        firstLoadingLog()

        initAllControllersAndAnimators()

        addOnListeners()

        initTemp()

    }

    override fun onPause() {
        super.onPause()
        if(logText.text.toString().isEmpty()){
            daoLog.updateLogText(logId,null)
        }else{
            daoLog.updateLogText(logId,logText.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        reLoadingLog()
    }

    //首次进入加载
    private fun firstLoadingLog(){
        groupId = intent.getIntExtra("log_group_id",-1)
        logGroupName.text = intent.getStringExtra("log_group_name")
        if(groupId == -1){
            Toast.makeText(this,"数据加载错误：未知日志组",Toast.LENGTH_SHORT).show()
        }else{
            loadingTime()
            preData()
        }
    }

    //二次及之后进入加载 (date picker)
    private fun reLoadingLog(){
        if(groupId != -1){
            loadingTime()
            preData()
            initTemp()
        }else{
            Toast.makeText(this,"数据加载错误：未知日志组",Toast.LENGTH_SHORT).show()
        }
    }

    // !!含有数据库操作的方法-直接更换adapter的数据容器
    private fun preData(){
        logId = daoLog.checkAndInsertLog(this,groupId,chosenTime!!).toInt()
        if(logId == 0){
            Toast.makeText(this,"数据加载错误：日志索引不正确",Toast.LENGTH_SHORT).show()
        }else{
            labelD = daoLog.getAllLabels(logId)
            picD = daoLog.getAllPics(logId)

            picAdapter = LogPicAdapter(picD,this){ d ->
                daoLog.deletePic(d)
                daoLog.updateLastModified(groupId)
            }
            labelAdapter = AddedLabelAdapter(labelD,this) {d ->
                daoLog.deleteLabel(d)
                daoLog.updateLastModified(groupId)
            }

            logPics.adapter = picAdapter
            labels.adapter = labelAdapter
            logText.text = daoLog.getLogText(logId)
            temp = daoLog.getWeatherTemperatureRange(logId)
        }
    }

    private fun initAllViews() {
        // Initialize Toolbar buttons
        backBtn = findViewById(R.id.back_btn)
        logGroupName = findViewById(R.id.log_group_name)
        menuBtn = findViewById(R.id.menu_btn)
        line = findViewById(R.id.dividing_line_3)

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
        rootView = findViewById(R.id.root)

    }

    private fun initAllControllersAndAnimators(){
        labelFlexboxLayoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        picFlexboxLayoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

        logPics.layoutManager = picFlexboxLayoutManager
        labels.layoutManager = labelFlexboxLayoutManager

        menuAnimator = FadeAnimator(menuBtn)
            .setDuration(100)
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
                    val d = LogPicItem(logId, uri.toString())
                    picD.add(d)
                    Log.v("storedUri",uri.toString())
                    picAdapter.notifyItemInserted(picD.size - 1)
                    daoLog.addPic(logId,uri.toString())
                    daoLog.updateLastModified(groupId)
                }
            }
        }
    }

    private fun addOnListeners(){
        backBtn.setOnClickListener {
            imm.hideSoftInputFromWindow(logText.windowToken, 0)
            onBackPressedDispatcher.onBackPressed()
        }
        rootView.setOnClickListener {
            imm.hideSoftInputFromWindow(logText.windowToken, 0)
        }
        menuBtn.setOnClickListener {
            imm.hideSoftInputFromWindow(logText.windowToken, 0)
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
                    language = "zh-rCN"
                    imageTitle = "选择一张或多张图片"
                    doneButtonText = "完成"
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
                line.visibility = View.GONE
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
            line.visibility = View.VISIBLE
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


    private fun loadingTime(){
        if(chosenTime == null){
            isToday(true)
            chosenTime = Utils.timestampToDateString(System.currentTimeMillis())
        }else if(chosenTime == Utils.timestampToDateString(System.currentTimeMillis())){
            isToday(true)
        }else{
            isToday(false)
        }
        logDate.text = Utils.dateStringToCH(chosenTime!!)
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
                backToday.setOnClickListener(null)
            }
            false -> {
                backToday.setTextColor(
                    ContextCompat.getColor(this,R.color.themeDarkGreen)
                )
                backLine.setBackgroundColor(
                    ContextCompat.getColor(this,R.color.themeDarkGreen)
                )
                backToday.setOnClickListener(backTodayListener)
            }
        }
    }

    private fun initTemp(){
        if(temp == null || temp!!.isEmpty()){
            recordTemp.text = "点击记录今日温度"
            if(chosenTime == Utils.timestampToDateString(System.currentTimeMillis())){
                recordTemp.setOnClickListener(tempListener)
            }else{
                recordTemp.setOnClickListener{
                    Toast.makeText(this,"您无法更新过去的天气哦",Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            recordTemp.text = temp
        }
    }


    companion object {
        val TYPE_PIC = 1
        val TYPE_Label = 2
    }

}