package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class allToDo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_to_do)

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

    private fun setupDeleteIconListeners() {
        // 待办 1 删除图标点击事件
        findViewById<ImageView>(R.id.delete_todo1).setOnClickListener {
            deleteTodo(R.id.todo1)
        }
        /*// 待办 2 删除图标点击事件
        findViewById<ImageView>(R.id.delete_todo2).setOnClickListener {
            deleteTodo(R.id.todo2)
        }
        // 待办 3 删除图标点击事件
        findViewById<ImageView>(R.id.delete_todo3).setOnClickListener {
            deleteTodo(R.id.todo3)
        }
        // 待办 4 删除图标点击事件
        findViewById<ImageView>(R.id.delete_todo4).setOnClickListener {
            deleteTodo(R.id.todo4)
        }
        // 待办 5 删除图标点击事件（假设待办 5 也有删除图标）
        findViewById<ImageView>(R.id.delete_todo5).setOnClickListener {
            deleteTodo(R.id.todo5)
        }*/
    }

    private fun deleteTodo(todoId: Int) {
        // 找到包含待办事项的父布局
        val todoView = findViewById<View>(todoId)
        val parentLayout = todoView.parent as? LinearLayout
        if (parentLayout != null) {
            // 隐藏整个待办事项的布局来模拟删除操作
            parentLayout.visibility = View.GONE
            Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_about -> {
                    showDeleteIcons()

                    true
                }
                R.id.action_settings -> {
                    showDisableIcons()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showDeleteIcons() {
        findViewById<ImageView>(R.id.delete_todo1).visibility = View.VISIBLE
        // 可以继续添加其他待办的删除图标显示逻辑
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.VISIBLE
    }

    private fun showDisableIcons() {
        findViewById<ImageView>(R.id.tingyong).visibility = View.VISIBLE
        // 可以继续添加其他待办的停用图标显示逻辑
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.VISIBLE
    }

    private fun hideDeleteIcons() {
        findViewById<ImageView>(R.id.delete_todo1).visibility = View.GONE
        // 可以继续添加其他待办的删除图标隐藏逻辑
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.GONE
    }

    private fun hideDisableIcons() {
        findViewById<ImageView>(R.id.tingyong).visibility = View.GONE
        // 可以继续添加其他待办的停用图标隐藏逻辑
        findViewById<Button>(R.id.buttonCancelDelete).visibility = View.GONE
    }
}