package com.example.plantingapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImageSelectionActivity : AppCompatActivity() {
    private lateinit var yearText: TextView
    private lateinit var previousYear: TextView
    private lateinit var nextYear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_selection)

        yearText = findViewById(R.id.year_text)
        previousYear = findViewById(R.id.previous_year)
        nextYear = findViewById(R.id.next_year)

        previousYear.setOnClickListener {
            val currentYear = yearText.text.toString().trim().replace("年", "").toInt()
            if (currentYear > 2000) {
                yearText.text = (currentYear - 1).toString() + "年"
            }
        }

        nextYear.setOnClickListener {
            val currentYear = yearText.text.toString().trim().replace("年", "").toInt()
            if (currentYear < 2099) {
                yearText.text = (currentYear + 1).toString() + "年"
            }
        }
    }
}