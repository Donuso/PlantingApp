package com.example.plantingapp

import android.content.Context
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.plantingapp.animators.ExpandAnimator

class AllToDoActivity : AppCompatActivity() {
    private lateinit var enabledCardViewLayout: LinearLayout
    private lateinit var disabledCardViewLayout: LinearLayout

    companion object {
        private const val REQUEST_CODE_NEW_TODO = 1
    }

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

        enabledCardViewLayout = findViewById(R.id.enabled_todos_layout)
        disabledCardViewLayout = findViewById(R.id.disabled_todos_layout)

        // 接收从TodoFragment传递过来的数据
        val todoContent = intent.getStringExtra("todo_content")
        val selectedDate = intent.getStringExtra("selected_date")
        val reminderInterval = intent.getStringExtra("reminder_interval")

        if (todoContent != null && selectedDate != null && reminderInterval != null) {
            addNewTodo(todoContent, selectedDate, reminderInterval)
        }

        // 设置新增任务按钮的点击事件
        findViewById<Button>(R.id.buttonnewtodo1).setOnClickListener {
            val intent = Intent(this, NewTodoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_TODO)
        }

        findViewById<Button>(R.id.backtoDo).setOnClickListener {
            finish() // 结束当前Activity，返回上一个界面

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
        val disableIcon = newTodoView.findViewById<ImageView>(R.id.tingyong)

        todoTitle.text = "待办: $todoContent"
        todoDetails.text = "创建于: $selectedDate"
        reminderIntervalDisplay.text = "提醒时间间隔: $reminderInterval 天"

        deleteIcon.setOnClickListener {
            deleteTodo(newTodoView)
        }

        disableIcon.setOnClickListener {
            disableTodo(newTodoView)
        }

        // 将新的待办布局添加到已启用的待办列表中
        enabledCardViewLayout.addView(newTodoView)
    }

    private fun deleteTodo(todoView: View) {
        enabledCardViewLayout.removeView(todoView)
        Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
    }

    private fun disableTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title)
        val todoDetails = todoView.findViewById<TextView>(R.id.todo_details)
        val reminderIntervalDisplay = todoView.findViewById<TextView>(R.id.reminder_interval_display)

        // 获取停用日期
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val disabledDate = sdf.format(Date())

        // 从已启用列表中移除
        enabledCardViewLayout.removeView(todoView)

        // 创建新的停用待办布局
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val disabledTodoView = inflater.inflate(R.layout.disabled_todo_item_layout, disabledCardViewLayout, false)

        val disabledTodoTitle = disabledTodoView.findViewById<TextView>(R.id.disabled_todo_title)
        val disabledTodoDetails = disabledTodoView.findViewById<TextView>(R.id.disabled_todo_details)
        val disabledReminderIntervalDisplay = disabledTodoView.findViewById<TextView>(R.id.disabled_reminder_interval_display)
        val disabledDateDisplay = disabledTodoView.findViewById<TextView>(R.id.disabled_date_display)

        disabledTodoTitle.text = todoTitle.text
        disabledTodoDetails.text = todoDetails.text
        disabledReminderIntervalDisplay.text = reminderIntervalDisplay.text
        disabledDateDisplay.text = "停用于 $disabledDate"

        // 添加到已停用列表中
        disabledCardViewLayout.addView(disabledTodoView)

        Toast.makeText(this, "待办事项已停用", Toast.LENGTH_SHORT).show()
    }


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
}