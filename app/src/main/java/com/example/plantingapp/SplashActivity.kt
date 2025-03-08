package com.example.plantingapp

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    // 定义一个Handler对象，用于处理来自其他线程的消息
    val myHandler=object : Handler(){
        // 重写handleMessage方法，用于接收并处理消息
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 判断消息的类型，如果是0x101，则执行跳转逻辑
            if (msg.what==0x101){
                // 创建一个Intent对象，用于跳转到LoginActivity，
                val intent= Intent(this@SplashActivity,AppHomeShow::class.java)
                startActivity(intent)
            }
        }
    };
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 开启一个新的线程
        thread {
            // 让线程休眠5秒，模拟加载过程
            Thread.sleep(5000)
            // 创建一个Message对象
            val msg= Message()
            // 设置Message的类型为0x101
            msg.what=0x101
            // 通过Handler发送消息，以便在主线程中处理
            myHandler.sendMessage(msg)
        }
    }
}