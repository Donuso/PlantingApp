package com.example.plantingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AllToDoActivity : AppCompatActivity() {
    private lateinit var enabledCardViewLayout: LinearLayout

    companion object {
        private const val REQUEST_CODE_NEW_TODO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_to_do)

        enabledCardViewLayout = findViewById(R.id.enabled_todos_layout)

        // 设置新增任务按钮的点击事件
        findViewById<Button>(R.id.buttonnewtodo1).setOnClickListener {
            // 创建一个意图，用于启动 NewToDoActivity
            val intent = Intent(this, NewTodoActivity::class.java)
            // 启动 NewToDoActivity 并等待结果
            startActivityForResult(intent, REQUEST_CODE_NEW_TODO)
        }

        findViewById<Button>(R.id.backtoDo).setOnClickListener {
            finish() // 结束当前Activity，返回上一个界面
        }

        val menuCaidan = findViewById<ImageView>(R.id.menu_caidan)
        menuCaidan.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // 为每个删除图标添加点击监听器
        setupDeleteIconListeners()

        // 处理取消按钮的点击事件
        findViewById<Button>(R.id.buttonCancelDelete).setOnClickListener {
            hideDeleteIcons()
            hideDisableIcons()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_TODO && resultCode == RESULT_OK) {
            val todoContent = data?.getStringExtra("todo_content")
            val selectedDate = data?.getStringExtra("selected_date")
            val reminderInterval = data?.getStringExtra("reminder_interval")

            if (todoContent != null && selectedDate != null && reminderInterval != null) {
                addNewTodo(todoContent, selectedDate, reminderInterval)
            }
        }
    }

    private fun addNewTodo(todoContent: String, selectedDate: String, reminderInterval: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val newTodoView = inflater.inflate(R.layout.todo_item_layout, enabledCardViewLayout, false)

        val todoTitle = newTodoView.findViewById<TextView>(R.id.todo_title)
        val todoDetails = newTodoView.findViewById<TextView>(R.id.todo_details)
        val reminderIntervalDisplay = newTodoView.findViewById<TextView>(R.id.reminder_interval_display)
        val deleteIcon = newTodoView.findViewById<ImageView>(R.id.delete_todo)

        todoTitle.text = "待办: $todoContent"
        todoDetails.text = "创建于: $selectedDate"
        reminderIntervalDisplay.text = "提醒时间间隔: $reminderInterval 天"

        deleteIcon.setOnClickListener {
            deleteTodo(newTodoView)
        }

        // 将新的待办布局添加到已启用的待办列表中
        enabledCardViewLayout.addView(newTodoView)
    }

    private fun deleteTodo(todoView: View) {
        enabledCardViewLayout.removeView(todoView)
        Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
    }

    private fun setupDeleteIconListeners() {
        // 可以遍历所有待办事项的删除图标添加点击事件
    }

    private fun showPopupMenu(view: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_menu_layout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 设置点击事件
        popupView.findViewById<Button>(R.id.action_settings).setOnClickListener {
            showDisableIcons()
            popupWindow.dismiss()
        }

        popupView.findViewById<Button>(R.id.action_about).setOnClickListener {
            showDeleteIcons()
            popupWindow.dismiss()
        }

        // 显示 PopupWindow
        popupWindow.showAsDropDown(view)
    }

    private fun showDeleteIcons() {
        // 显示所有删除图标
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.VISIBLE
    }

    private fun showDisableIcons() {
        // 显示所有停用图标
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.VISIBLE
    }

    private fun hideDeleteIcons() {
        // 隐藏所有删除图标
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.GONE
    }

    private fun hideDisableIcons() {
        // 隐藏所有停用图标
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.GONE
    }
}