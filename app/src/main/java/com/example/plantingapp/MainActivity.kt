package com.example.plantingapp

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val logs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化日志列表
        logs.addAll(listOf("日志1", "日志2", "日志3"))

        recyclerView = findViewById(R.id.log_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LogAdapter(logs)

        // 获取Button和ImageView的实例
        val addButton = findViewById<Button>(R.id.add_button)
        val moreOptionsImageView = findViewById<ImageView>(R.id.more_options)

        // 为Button设置点击事件监听器
        addButton.setOnClickListener {
            // 在ImageView上添加灰色阴影效果
            moreOptionsImageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        }
    }

    // 当用户添加新的日志时，更新适配器数据并通知 RecyclerView
    fun addLog(newLog: String) {
        logs.add(newLog)
        recyclerView.adapter?.notifyItemInserted(logs.size - 1)
    }
}