package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_detail)

        // 获取传递过来的数据
        val plantName = intent.getStringExtra("PLANT_NAME")
        val date = intent.getStringExtra("DATE")

        // 设置植物名称和日期
        val plantNameTextView = findViewById<TextView>(R.id.plant_name)
        plantNameTextView.setText(plantName)
        val dateTextView = findViewById<TextView>(R.id.date)
        dateTextView.setText(date)

        // 设置编辑日期的按钮点击事件
        val editDateButton = findViewById<Button>(R.id.datechange_button)
        editDateButton.setOnClickListener {
            // 打开日期选择界面
            val intent = Intent(this, DateSelectionActivity::class.java)
            startActivity(intent)
        }

        // 设置回到今天的按钮点击事件
        //val backToTodayButton = findViewById<Button>(R.id.back_to_today_button)
        //backToTodayButton.setOnClickListener {
          //  finish() // 返回到上一个活动
        //}

        // 设置增加标签的按钮点击事件
        //val tagsTextView = findViewById<TextView>(R.id.tags)
        //tagsTextView.setOnClickListener {
            // 打开标签选择界面
            //val intent = Intent(this, TagSelectionActivity::class.java)
            //startActivity(intent)
        //}

        // 设置增加图片的按钮点击事件
        val addImageButton = findViewById<Button>(R.id.add_image)
        addImageButton.setOnClickListener {
            // 打开图片选择界面
            val intent = Intent(this, ImageSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}