package com.example.plantingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.LabelAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.dao.LabelDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LabelItem
import com.example.plantingapp.utils.LabelItemConverter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class NewLabelActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var helpBtn: ImageButton
    private lateinit var menuBtn: ImageButton
    private lateinit var recentlyUsedLabel: RecyclerView
    private lateinit var preventCover: CardView
    private lateinit var statusBg: CardView
    private lateinit var dataBg: CardView
    private lateinit var diyBg: CardView
    private lateinit var statusText: TextView
    private lateinit var dataText: TextView
    private lateinit var diyText: TextView
    private lateinit var allLabels: RecyclerView
    private lateinit var temporarilyNoDiyLabels: TextView
    private lateinit var addDiyModule : FrameLayout
    private lateinit var addDiyCover: View //触摸层
    private lateinit var menuOptions: LinearLayout
    private lateinit var delCustomLabel: TextView
    private lateinit var cancelModule: CardView
    private lateinit var cancelText: TextView
    private lateinit var recentlyNoUsedLabelHint:TextView
    private lateinit var deleteLabelHint:TextView

    private lateinit var menuAnimator: FadeAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var cancelBackAnimator: ExpandAnimator
    private lateinit var optionAnimator: FadeAnimator

    private lateinit var recentLabelsManager: LinearLayoutManager
    private lateinit var allLabelsManager: FlexboxLayoutManager
    private lateinit var recentAdapter:LabelAdapter
    private lateinit var allAdapter: LabelAdapter

    private var green = -1
    private var white = -1
    private val elevation = 5f
    private val cancelMovement = 100f

    private lateinit var customLabels: MutableList<LabelItem>
    private lateinit var recentlyUsedLabels: MutableList<LabelItem>
    private lateinit var dao:LabelDAO
    private lateinit var sp:SharedPreferences

    private val newCustomLabelGetter = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val newLabel = result.data?.getStringExtra("label_data")
            var toastStr = "添加自定义标签失败"
            newLabel?.let{
                val label = LabelItemConverter.jsonToLabel(newLabel)
                customLabels.add(label)
                allAdapter.notifyItemInserted(customLabels.size - 1)
                toastStr = "添加\"${label.tagName}\"标签成功"
                temporarilyNoDiyLabels.visibility = View.GONE
                dao.addCustomTag(label)
            }
            Toast.makeText(this,toastStr,Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_label_wzc)

        getColors()
        initAllViews()
        initAnimators()
        addOnListeners()
        preData()
    }

    private fun preData(){
        dao = LabelDAO(this)
        allLabelsManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        recentLabelsManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        allAdapter = LabelAdapter(Constants.defStatusLabels.toMutableList(), this) { it,t ->
            when(t){
                LabelItem.MODE_DISPLAY -> {
                    addRecentlyUsedLabel(it)
                    Log.v("returnedLabel",it.toString())
                    val i = Intent().putExtra("label_data", LabelItemConverter.labelToJson(it))
                    setResult(RESULT_OK, i)
                    finish()
                }
                LabelItem.MODE_DEL -> {
                    dao.deleteCustomTag(DataExchange.USERID!!,it.tagId)
                    deleteRecentlyUsedLabel(it)
                }
            }

        }
        recentlyUsedLabel.layoutManager = recentLabelsManager
        allLabels.layoutManager = allLabelsManager
        allLabels.adapter = allAdapter

        sp = getSharedPreferences("${DataExchange.USERID}_prefs", Context.MODE_PRIVATE)
        val text = sp.getString("recentlyUsedLabels", null)
        recentlyUsedLabels = if (text == null) {
            mutableListOf()
        } else {
            LabelItemConverter.jsonToList(text)
        }

        if (recentlyUsedLabels.size == 0) {
            recentlyNoUsedLabelHint.visibility = View.VISIBLE
        } else {
            recentlyNoUsedLabelHint.visibility = View.GONE
            recentAdapter = LabelAdapter(recentlyUsedLabels, this) { it ,t->
                when(t){
                    LabelItem.MODE_DISPLAY -> {
                        Log.v("returnedLabel",it.toString())
                        val i = Intent().putExtra("label_data", LabelItemConverter.labelToJson(it))
                        setResult(RESULT_OK, i)
                        finish()
                    }
                    LabelItem.MODE_DEL -> {
                        dao.deleteCustomTag(DataExchange.USERID!!,it.tagId)
                    }
                }

            }
            recentlyUsedLabel.adapter = recentAdapter
        }
    }

    private fun addRecentlyUsedLabel(labelItem: LabelItem) {
        // 使用 HashSet 进行查重
        val existingKeys = HashSet<String>()
        recentlyUsedLabels.forEach {
            existingKeys.add(generateKey(it))
        }

        val newKey = generateKey(labelItem)
        if (!existingKeys.contains(newKey)) {
            if (recentlyUsedLabels.size == 4) {
                recentlyUsedLabels.removeAt(3)
            }
            recentlyUsedLabels.add(0, labelItem)

            // 更新 SharedPreferences
            with(sp.edit()) {
                putString("recentlyUsedLabels", LabelItemConverter.listToJson(recentlyUsedLabels))
                apply()
            }
        }
    }

    private fun deleteRecentlyUsedLabel(l: LabelItem) {
        for(it in recentlyUsedLabels){
            if(l.isCustom && l.tagId == it.tagId && l.isCustom == it.isCustom && l.tagType == it.tagType){
                recentlyUsedLabels.remove(it)
                break
            }
        }
        recentAdapter.notifyDataSetChanged()
        // 更新 SharedPreferences（即使列表为空也需要保存，保持数据一致性）
        with(sp.edit()) {
            putString("recentlyUsedLabels", LabelItemConverter.listToJson(recentlyUsedLabels))
            apply()
        }
    }

    private fun generateKey(labelItem: LabelItem): String {
        // 使用 tagId, tagType, isCustom 生成唯一键
        return "${labelItem.tagId}_${labelItem.tagType}_${labelItem.isCustom}"
    }

    private fun getColors(){
        green = ContextCompat.getColor(this,R.color.themeDarkGreen)
        white = ContextCompat.getColor(this,R.color.white)
    }

    private fun initAllViews(){
        backBtn = findViewById(R.id.back_btn)
        helpBtn = findViewById(R.id.help_btn)
        menuBtn = findViewById(R.id.menu_btn)

        recentlyUsedLabel = findViewById(R.id.recently_used_label)
        preventCover = findViewById(R.id.prevent_cover)

        // 初始化按钮背景卡片及其内部的文本
        statusBg = findViewById(R.id.status_bg)
        statusText = findViewById(R.id.status_text)
        dataBg = findViewById(R.id.data_bg)
        dataText = findViewById(R.id.data_text)
        diyBg = findViewById(R.id.diy_bg)
        diyText = findViewById(R.id.diy_text)

        // 初始化 RecyclerView 和相关视图
        allLabels = findViewById(R.id.all_labels)
        temporarilyNoDiyLabels = findViewById(R.id.temporarily_no_diy_labels)
        addDiyModule = findViewById(R.id.add_diy_module)
        addDiyCover = findViewById(R.id.add_diy_cover)

        // 初始化菜单选项
        menuOptions = findViewById(R.id.menu_options)
        delCustomLabel = findViewById(R.id.del_custom_lable)

        // 初始化底部取消模块
        cancelModule = findViewById(R.id.cancel_module)
        cancelText = findViewById(R.id.cancel_text)

        recentlyNoUsedLabelHint = findViewById(R.id.no_label_hint_text)
        deleteLabelHint = findViewById(R.id.delete_label_hint)
    }

    private fun initAnimators(){
        menuAnimator = FadeAnimator(menuBtn)
            .setDuration(100)
        cancelAnimator = ExpandAnimator(this,cancelModule)
            .setMoveDirection(2,-cancelMovement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
        optionAnimator = FadeAnimator(menuOptions)
            .setDuration(100)
        cancelBackAnimator = ExpandAnimator(this,cancelModule)
            .setMoveDirection(2,cancelMovement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
    }

    private fun addOnListeners(){
        preventCover.setOnClickListener { }
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        statusText.setOnClickListener {
            switchShownLabel(SHOW_STATUS)
        }
        dataText.setOnClickListener {
            switchShownLabel(SHOW_DATA)
        }
        diyText.setOnClickListener {
            switchShownLabel(SHOW_CUSTOM)
        }
        menuBtn.setOnClickListener {
            optionAnimator.start(true)
        }
        menuOptions.setOnClickListener {
            optionAnimator.start(false)
        }
        delCustomLabel.setOnClickListener {
            if(customLabels.size == 0){
                Toast.makeText(this,"您尚未添加任何自定义标签",Toast.LENGTH_SHORT).show()
                optionAnimator.start(false)
            }else{
                diyText.setOnClickListener(null)
                statusText.setOnClickListener(null)
                dataText.setOnClickListener(null)
                optionAnimator.start(false)
                cancelAnimator.start()
                menuAnimator.start(false)
                preventCover.visibility = View.VISIBLE
                deleteLabelHint.visibility = View.VISIBLE
                addDiyModule.visibility = View.GONE
                statusText.setTextColor(
                    ContextCompat.getColor(this,R.color.general_grey_wzc)
                )
                dataText.setTextColor(
                    ContextCompat.getColor(this,R.color.general_grey_wzc)
                )
                statusBg.cardElevation = 0f
                dataBg.cardElevation = 0f
                alterDelMode(LabelItem.MODE_DEL)
            }
        }
        cancelText.setOnClickListener {
            cancelBackAnimator.start()
            menuAnimator.start(true)
            preventCover.visibility = View.GONE
            deleteLabelHint.visibility = View.GONE
            addDiyModule.visibility = View.VISIBLE
            statusText.setOnClickListener {
                switchShownLabel(SHOW_STATUS)
            }
            dataText.setOnClickListener {
                switchShownLabel(SHOW_DATA)
            }
            diyText.setOnClickListener {
                switchShownLabel(SHOW_CUSTOM)
            }
            statusText.setTextColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            dataText.setTextColor(
                ContextCompat.getColor(this,R.color.themeDarkGreen)
            )
            statusBg.cardElevation = elevation
            dataBg.cardElevation = elevation
            alterDelMode(LabelItem.MODE_DISPLAY)
            if(customLabels.size == 0){
                temporarilyNoDiyLabels.visibility = View.VISIBLE
            }
        }
        addDiyCover.setOnClickListener {
            newCustomLabelGetter.launch(
                Intent(this,NewPersonalLabelActivity::class.java)
            )
        }
    }

    private fun switchShownLabel(t:Int){
        when(t){
            SHOW_STATUS -> {
                statusText.setTextColor(white)
                statusBg.setCardBackgroundColor(green)
                dataText.setTextColor(green)
                dataBg.setCardBackgroundColor(white)
                diyText.setTextColor(green)
                diyBg.setCardBackgroundColor(white)
                addDiyModule.visibility = View.GONE
                addDiyCover.visibility = View.GONE
                temporarilyNoDiyLabels.visibility = View.GONE
                if(menuBtn.visibility == View.VISIBLE){
                    menuAnimator.start(false)
                }
                allAdapter.labelList = Constants.defStatusLabels.toMutableList()
                allAdapter.notifyDataSetChanged()
            }
            SHOW_DATA -> {
                statusText.setTextColor(green)
                statusBg.setCardBackgroundColor(white)
                dataText.setTextColor(white)
                dataBg.setCardBackgroundColor(green)
                diyText.setTextColor(green)
                diyBg.setCardBackgroundColor(white)
                addDiyModule.visibility = View.GONE
                addDiyCover.visibility = View.GONE
                temporarilyNoDiyLabels.visibility = View.GONE
                if(menuBtn.visibility == View.VISIBLE){
                    menuAnimator.start(false)
                }
                allAdapter.labelList = Constants.defDataLabels.toMutableList()
                allAdapter.notifyDataSetChanged()
            }
            SHOW_CUSTOM -> {
                statusText.setTextColor(green)
                statusBg.setCardBackgroundColor(white)
                dataText.setTextColor(green)
                dataBg.setCardBackgroundColor(white)
                diyText.setTextColor(white)
                diyBg.setCardBackgroundColor(green)
                addDiyModule.visibility = View.VISIBLE
                addDiyCover.visibility = View.VISIBLE
                customLabels = dao.getAllCustomTags(DataExchange.USERID!!).toMutableList()
                allAdapter.labelList = customLabels
                if(menuBtn.visibility == View.GONE){
                    menuAnimator.start(true)
                }
                if(customLabels.size == 0){
                    temporarilyNoDiyLabels.visibility = View.VISIBLE
                }else{
                    temporarilyNoDiyLabels.visibility = View.GONE
                }
                allAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun alterDelMode(s:Int){
        when(s){
            LabelItem.MODE_DEL -> {
                for(it in allAdapter.labelList){
                    it.status = LabelItem.MODE_DEL
                }
            }
            LabelItem.MODE_DISPLAY -> {
                for(it in allAdapter.labelList){
                    it.status = LabelItem.MODE_DISPLAY
                }
            }
        }
        allAdapter.notifyDataSetChanged()
    }

    companion object {
        val SHOW_STATUS = 1
        val SHOW_DATA = 2
        val SHOW_CUSTOM = 3
    }

}