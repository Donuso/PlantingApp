package com.example.plantingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }

    // 当用户添加新的日志时，更新适配器数据并通知 RecyclerView
    fun addLog(newLog: String) {
        logs.add(newLog)
        recyclerView.adapter?.notifyItemInserted(logs.size - 1)
    }
}

