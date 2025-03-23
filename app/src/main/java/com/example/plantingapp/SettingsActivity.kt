package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : BaseActivity() {

    private lateinit var backButton:ImageButton
    private lateinit var logOut:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initAllViews()
        addOnListeners()
    }

    private fun initAllViews(){
        backButton = findViewById(R.id.back_btn)
        logOut = findViewById(R.id.log_out)
    }

    private fun addOnListeners(){
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        logOut.setOnClickListener {
            sendBroadcast(
                Intent("com.example.plantingapp.LOGOUT")
            )
        }
    }
}