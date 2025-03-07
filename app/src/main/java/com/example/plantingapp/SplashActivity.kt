package com.example.plantingapp

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //添加下划线
        val underline:TextView = findViewById(R.id.text_underline)
        underline.paintFlags = underline.paintFlags or Paint.UNDERLINE_TEXT_FLAG

    }
}