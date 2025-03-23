package com.example.plantingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat.startActivity
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.fragments.MainFragment
import kotlin.concurrent.thread

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val handler = MyHandler(this)

    private class MyHandler(val activity: SplashActivity) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x100 -> { }
                0x101 -> {
                    activity.startActivity(
                        Intent(
                            activity,
                            LoginActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        val iconAnimator = ExpandAnimator(this,findViewById<ImageView>(R.id.icon))
        iconAnimator.setDuration(600)
            .setRateType(ExpandAnimator.iOSRatio)
            .setFade(0f,1f)
            .setMoveDirection(2,-200f)
            .start()
        thread {
            Thread.sleep(600)
            handler.sendMessage(
                Message().apply {
                    this.what = 0x101
                }
            )
        }
    }
}