package com.example.plantingapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat.startActivity
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.fragments.MainFragment
import com.example.plantingapp.item.DataExchange
import kotlin.concurrent.thread

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val handler = MyHandler(this)
    private lateinit var sharedPreferences: SharedPreferences

    private class MyHandler(val activity: SplashActivity) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x100 -> {
                    // 读取 auto_login 字段
                    val autoLogin = activity.sharedPreferences.getBoolean("auto_login", false)
                    val userId = activity.sharedPreferences.getString("user_id","default_id") ?: "null"
                    Log.i("SplashActivity", "auto_login: $autoLogin")
                    if (autoLogin) {
                        DataExchange.USERID =userId
                        // 如果 auto_login 为 true，跳转到 MainSwitchActivity
                        activity.startActivity(
                            Intent(
                                activity,
                                MainSwitchActivity::class.java
                            )
                        )

                    } else {
                        // 否则，跳转到 LoginActivity
                        activity.startActivity(
                            Intent(
                                activity,
                                LoginActivity::class.java
                            )
                        )
                    }
                    activity.finish() // 结束当前 Activity
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
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
                    this.what = 0x100
                }
            )
        }
    }
}