package com.example.plantingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.plantingapp.animators.ExpandAnimator

class AllToDoActivity : BaseActivity() {

    private lateinit var optionLayer: LinearLayout
    private lateinit var cancel: TextView
    private lateinit var cancelModule: CardView
    private lateinit var addTodoModule: CardView
    private lateinit var addTodo: ImageView
    private lateinit var deleteTodo: TextView
    private lateinit var alterTodo: TextView
    private lateinit var menu: ImageButton
    private lateinit var hint:TextView

    private lateinit var optionAnimator: ExpandAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var addModuleAnimator: ExpandAnimator
    private lateinit var menuAnimator: ExpandAnimator

    private val cancelDisplacement = 120f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_to_do)

        initAll()
        addOnListeners()

    }

    private fun initAll(){
        optionLayer = findViewById(R.id.options_layer)
        cancel = findViewById(R.id.cancel_text)
        cancelModule = findViewById(R.id.cancel_module)
        addTodoModule = findViewById(R.id.add_todo_module)
        addTodo = findViewById(R.id.new_todo)
        deleteTodo = findViewById(R.id.delete_todo)
        alterTodo = findViewById(R.id.alter_todos)
        menu = findViewById(R.id.menu)
        hint = findViewById(R.id.hint_subtitle)

        optionAnimator = ExpandAnimator(this,optionLayer)
            .setIfFade(true)
            .setDuration(100)
        cancelAnimator = ExpandAnimator(this,cancelModule)
            .setDuration(500)
        addModuleAnimator = ExpandAnimator(this,addTodoModule)
            .setIfFade(true)
            .setDuration(200)
        menuAnimator = ExpandAnimator(this,menu)
            .setIfFade(true)
            .setDuration(200)
    }

    private fun addOnListeners(){
        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener {
            finish()
        }
        menu.setOnClickListener {
            showMenu()
        }
        optionLayer.setOnClickListener{
            hideMenu()
        }
        cancel.setOnClickListener{
            hideCancel()
        }
        deleteTodo.setOnClickListener{
            hint.text = "点击待办即可删除"
            hint.setTextColor(
                ContextCompat.getColor(
                this,
                R.color.themeRed
            ))
            hideMenu()
            cancel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeRed
                )
            )
            showCancel()
        }
        alterTodo.setOnClickListener{
            hint.text = "点击按钮以停用/复用待办"
            hint.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeBlue
                ))
            hideMenu()
            cancel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeBlue
                )
            )
            showCancel()
        }
    }

//    private fun setupDeleteIconListeners() {
//        // 待办 1 删除图标点击事件
//        findViewById<ImageView>(R.id.delete_todo1).setOnClickListener {
//            deleteTodo(R.id.todo1)
//        }
//        /*// 待办 2 删除图标点击事件
//        findViewById<ImageView>(R.id.delete_todo2).setOnClickListener {
//            deleteTodo(R.id.todo2)
//        }
//        // 待办 3 删除图标点击事件
//        findViewById<ImageView>(R.id.delete_todo3).setOnClickListener {
//            deleteTodo(R.id.todo3)
//        }
//        // 待办 4 删除图标点击事件
//        findViewById<ImageView>(R.id.delete_todo4).setOnClickListener {
//            deleteTodo(R.id.todo4)
//        }
//        // 待办 5 删除图标点击事件（假设待办 5 也有删除图标）
//        findViewById<ImageView>(R.id.delete_todo5).setOnClickListener {
//            deleteTodo(R.id.todo5)
//        }*/
//    }

//    private fun deleteTodo(todoId: Int) {
//        // 找到包含待办事项的父布局
//        val todoView = findViewById<View>(todoId)
//        val parentLayout = todoView.parent as? LinearLayout
//        if (parentLayout != null) {
//            // 隐藏整个待办事项的布局来模拟删除操作
//            parentLayout.visibility = View.GONE
//            Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun showMenu() { optionAnimator.setFade(0f,1f).start() }

    private fun hideMenu() { optionAnimator.setFade(1f,0f).start() }

    private fun showCancel(){
        cancelAnimator.setMoveDirection(2,-cancelDisplacement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
            .start()
        addModuleAnimator.setFade(1f,0f)
            .start()
        menuAnimator.setFade(1f,0f)
            .start()
    }

    private fun hideCancel(){
        hint.text = getString(R.string.hint_subtitle)
        hint.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.general_grey_wzc
            )
        )
        cancelAnimator.setMoveDirection(2,cancelDisplacement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
            .start()
        addModuleAnimator.setFade(0f,1f)
            .start()
        menuAnimator.setFade(0f,1f)
            .start()
    }

//    private fun showDeleteIcons() {
//        findViewById<ImageView>(R.id.delete_todo1).visibility = View.VISIBLE
//        // 可以继续添加其他待办的删除图标显示逻辑
//        findViewById<CardView>(R.id.cancel_module).visibility = View.VISIBLE
//    }
//
//    private fun showDisableIcons() {
//        findViewById<ImageView>(R.id.tingyong).visibility = View.VISIBLE
//        // 可以继续添加其他待办的停用图标显示逻辑
//        findViewById<CardView>(R.id.cancel_module).visibility = View.VISIBLE
//    }
//
//    private fun hideDeleteIcons() {
//        findViewById<ImageView>(R.id.delete_todo1).visibility = View.GONE
//        // 可以继续添加其他待办的删除图标隐藏逻辑
//        findViewById<CardView>(R.id.cancel_module).visibility = View.VISIBLE
//    }
//
//    private fun hideDisableIcons() {
//        findViewById<ImageView>(R.id.tingyong).visibility = View.GONE
//        // 可以继续添加其他待办的停用图标隐藏逻辑
//        findViewById<CardView>(R.id.cancel_module).visibility = View.VISIBLE
//    }
}