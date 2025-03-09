package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NoteSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_select)

        val statusButton: Button = findViewById(R.id.status_button)
        val environmentButton: Button = findViewById(R.id.environment_button)
        val customButton: Button = findViewById(R.id.custom_button)
        val leafColorButton: Button = findViewById(R.id.leaf_color_button)
        val popupLayout: LinearLayout = findViewById(R.id.popup_layout)
        val popupCancelButton: Button = findViewById(R.id.popup_cancel_button)
        val popupConfirmButton: Button = findViewById(R.id.popup_confirm_button)

        val statusButtonGroup: LinearLayout = findViewById(R.id.status_button_group)
        val environmentButtonGroup: LinearLayout = findViewById(R.id.environment_button_group)
        val customButtonGroup: LinearLayout = findViewById(R.id.custom_button_group)

        // 设置叶片颜色按钮的点击事件
        leafColorButton.setOnClickListener {
            popupLayout.visibility = LinearLayout.VISIBLE

            // 设置SeekBar的监听器
            val leafColorSeekBar: SeekBar = findViewById(R.id.leaf_color_seekbar)
            val leafColorValue: TextView = findViewById(R.id.leaf_color_value)

            leafColorSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // 根据进度值更新状态文本
                    leafColorValue.text = when {
                        progress < 33 -> "较差"
                        progress < 67 -> "一般"
                        else -> "优良"
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 拖动开始时的逻辑（可选）
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // 拖动结束时的逻辑（可选）
                }
            })
        }

        // 设置取消按钮的点击事件
        popupCancelButton.setOnClickListener {
            popupLayout.visibility = LinearLayout.GONE
        }

        // 设置确认按钮的点击事件
        popupConfirmButton.setOnClickListener {
            // 获取用户选择的状态
            val leafColorSeekBar: SeekBar = findViewById(R.id.leaf_color_seekbar)
            val leafColorValue: TextView = findViewById(R.id.leaf_color_value)
            val selectedStatus = leafColorValue.text.toString()

            // 创建Intent并传递状态和天气信息
            val intent = Intent(this, LogDetailActivity::class.java)
            intent.putExtra("STATUS", selectedStatus)
            intent.putExtra("WEATHER", "晴") // 假设天气信息是固定的，或者从其他地方获取

            // 启动LogDetailActivity
            startActivity(intent)

            // 关闭弹出框
            popupLayout.visibility = LinearLayout.GONE
        }

        // 初始化按钮组的显示状态
        initializeButtonGroups()
        statusButton.setOnClickListener { switchButtonGroup(statusButtonGroup) }
        environmentButton.setOnClickListener { switchButtonGroup(environmentButtonGroup) }
        customButton.setOnClickListener { switchButtonGroup(customButtonGroup) }
    }

    private fun initializeButtonGroups() {
        // 默认显示状态按钮组，隐藏其他按钮组
        findViewById<LinearLayout>(R.id.status_button_group).visibility = LinearLayout.VISIBLE
        findViewById<LinearLayout>(R.id.environment_button_group).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.custom_button_group).visibility = LinearLayout.GONE
    }

    private fun switchButtonGroup(visibleGroup: LinearLayout) {
        // 隐藏所有按钮组
        findViewById<LinearLayout>(R.id.status_button_group).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.environment_button_group).visibility = LinearLayout.GONE
        findViewById<LinearLayout>(R.id.custom_button_group).visibility = LinearLayout.GONE

        // 显示选中的按钮组
        visibleGroup.visibility = LinearLayout.VISIBLE
    }
}