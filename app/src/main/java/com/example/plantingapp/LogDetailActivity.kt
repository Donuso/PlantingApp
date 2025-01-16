package com.example.plantingapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_detail)

        val logDetail = findViewById<TextView>(R.id.log_detail)
        val logText = intent.getStringExtra("LOG_TEXT")
        logDetail.text = logText
    }
}
