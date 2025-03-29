package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.plantingapp.dao.LabelDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LabelItem
import com.example.plantingapp.utils.LabelItemConverter

class NewPersonalLabelActivity : BaseActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var helpBtn: ImageButton
    private lateinit var statusBg: CardView
    private lateinit var statusIcon: ImageView
    private lateinit var statusText: TextView
    private lateinit var dataBg: CardView
    private lateinit var dataIcon: ImageView
    private lateinit var dataText: TextView
    private lateinit var labelName: EditText
    private lateinit var unit: EditText
    private lateinit var cancelModule: CardView
    private lateinit var cancelText: TextView
    private lateinit var unitModule: LinearLayout
    private lateinit var bg: ConstraintLayout

    private lateinit var dao:LabelDAO

    private val returnLabel = LabelItem(
        isCustom = true,
        tagType = LabelItem.TYPE_STATUS,
        tagId = -1,
        tagIcon = R.drawable.icon_app_zhj,
        tagName = ""
    )
    private var green: Int = -1
    private var white: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_persenal_label_wzc)

        initViews()
        initColors()
        dao = LabelDAO(this)
        addOnListeners()
    }

    private fun initViews(){
        backBtn = findViewById(R.id.back_btn)
        helpBtn = findViewById(R.id.help_btn) // 备用
        statusBg = findViewById(R.id.status_bg)
        statusIcon = findViewById(R.id.status_icon)
        statusText = findViewById(R.id.status_text_touch)
        dataBg = findViewById(R.id.data_bg)
        dataIcon = findViewById(R.id.data_icon)
        dataText = findViewById(R.id.data_text)
        labelName = findViewById(R.id.label_name)
        unit = findViewById(R.id.unit)
        cancelModule = findViewById(R.id.cancel_module) //备用
        cancelText = findViewById(R.id.cancel_text)
        unitModule = findViewById(R.id.unit_module)
        bg = findViewById(R.id.main)
    }

    private fun initColors(){
        green = ContextCompat.getColor(this,R.color.themeDarkGreen)
        white = ContextCompat.getColor(this,R.color.white)
    }

    private fun addOnListeners(){
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        statusText.setOnClickListener {
            if(returnLabel.tagType != LabelItem.TYPE_STATUS){
                statusBg.setCardBackgroundColor(green)
                statusIcon.setImageResource(R.drawable.icon_status_white_wzc)
                statusText.setTextColor(white)
                dataBg.setCardBackgroundColor(white)
                dataIcon.setImageResource(R.drawable.icon_data_green_wzc)
                dataText.setTextColor(green)
                unitModule.visibility = View.GONE
            }
            returnLabel.tagType = LabelItem.TYPE_STATUS
        }
        dataText.setOnClickListener {
            if(returnLabel.tagType == LabelItem.TYPE_STATUS){
                statusBg.setCardBackgroundColor(white)
                statusIcon.setImageResource(R.drawable.icon_status_green_wzc)
                statusText.setTextColor(green)
                dataBg.setCardBackgroundColor(green)
                dataIcon.setImageResource(R.drawable.icon_data_white_wzc)
                dataText.setTextColor(white)
                unitModule.visibility = View.VISIBLE
            }
            returnLabel.tagType = LabelItem.TYPE_DATA
        }
        cancelText.setOnClickListener {
            if(labelName.text.toString().isEmpty()){
                Toast.makeText(this,"标签名不可为空",Toast.LENGTH_SHORT).show()
            }else if(labelName.text.toString().length > 8) {
                Toast.makeText(this, "标签名超出字数限制", Toast.LENGTH_SHORT).show()
            }else if(dao.isTagNameExist(labelName.text.toString(),DataExchange.USERID!!)){
                Toast.makeText(this, "您已创建了同名的自定义标签", Toast.LENGTH_SHORT).show()
            }else{
                returnLabel.tagName = labelName.text.toString()
                when(returnLabel.tagType){
                    LabelItem.TYPE_STATUS -> {
                        setResult(
                            RESULT_OK,
                            Intent().apply {
                                putExtra("label_data",LabelItemConverter.labelToJson(returnLabel))
                            }
                        )
                        finish()
                    }
                    LabelItem.TYPE_DATA -> {
                        if(unit.text.toString().isEmpty()){
                            Toast.makeText(this,"单位名不可为空",Toast.LENGTH_SHORT).show()
                        }else if(unit.text.toString().length > 10){
                            Toast.makeText(this,"单位名超出字数限制",Toast.LENGTH_SHORT).show()
                        }else{
                            returnLabel.valUnit = unit.text.toString()
                            setResult(
                                RESULT_OK,
                                Intent().apply {
                                    putExtra("label_data",LabelItemConverter.labelToJson(returnLabel))
                                }
                            )
                            finish()
                        }
                    }
                }
            }
        }
        bg.setOnClickListener{
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(labelName.windowToken, 0)
            val imm2 = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm2.hideSoftInputFromWindow(unit.windowToken, 0)
        }

    }


}