package com.example.plantingapp

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val logs = mutableListOf<String>()  // 可变列表
    private lateinit var adapter: LogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化日志列表
        logs.addAll(listOf("日志1", "日志2", "日志3"))

        recyclerView = findViewById(R.id.log_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LogAdapter(logs)
        recyclerView.adapter = adapter

        // 获取Button和ImageView的实例
        val addButton = findViewById<Button>(R.id.add_button)
        val moreOptionsImageView = findViewById<ImageView>(R.id.more_options)

        // 为Button设置点击事件监听器
        addButton.setOnClickListener {
            showAddLogDialog()
        }

        // 菜单栏，为ImageView设置点击事件监听器
        moreOptionsImageView.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        // 处理设置选项
                        true
                    }
                    R.id.action_about -> {
                        // 处理关于选项
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        // 添加长按事件监听器
        addOnItemTouchListener()
    }

    private fun addOnItemTouchListener() {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                val view = recyclerView.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val position = recyclerView.getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        showDeleteButton(position)
                    }
                }
            }
        })

        recyclerView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun showDeleteButton(position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? LogAdapter.LogViewHolder
        viewHolder?.deleteButton?.visibility = View.VISIBLE
    }

    private fun showAddLogDialog() {
        val builder = AlertDialog.Builder(this, R.style.RoundedDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_log, null)
        builder.setView(dialogView)

        val logNameEditText = dialogView.findViewById<EditText>(R.id.log_name)
        val logNoteEditText = dialogView.findViewById<EditText>(R.id.log_note)
        /*val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)*/

        builder.setPositiveButton("确认") { _, _ ->
            val logName = logNameEditText.text.toString()
            val logNote = logNoteEditText.text.toString()
            val newLog = if (logNote.isEmpty()) logName else "$logName: $logNote"
            addLog(newLog)
        }

        builder.setNegativeButton("取消", null)
        val dialog = builder.show()

        /*confirmButton.setOnClickListener {
            dialog.dismiss()
        }*/
    }

    fun addLog(newLog: String) {
        logs.add(newLog)  // 添加新日志到列表
        adapter.updateLogs(logs)  // 通知适配器添加了一个新项
    }
}