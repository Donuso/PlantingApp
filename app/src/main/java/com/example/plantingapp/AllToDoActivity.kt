package com.example.plantingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.item.TodoItem
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AllToDoActivity : AppCompatActivity() {
    private lateinit var enabledCardViewLayout: RecyclerView
    private lateinit var disabledCardViewLayout: RecyclerView
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
    private lateinit var hint: TextView
    private lateinit var optionAnimator: ExpandAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var addModuleAnimator: ExpandAnimator
    private lateinit var menuAnimator: ExpandAnimator
    private val cancelDisplacement = 120f
    // 数据列表
    private val enabledTodoList = mutableListOf<TodoItem>()
    private val disabledTodoList = mutableListOf<TodoItem>()
    private var isDeleting = false // 标记是否处于删除模式
    private var isAltering = false // 标记是否处于停用/复用模式

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_to_do)
        initAll()
        addOnListeners()
        // 初始化 RecyclerView
        enabledCardViewLayout = findViewById(R.id.enabled_todos)
        disabledCardViewLayout = findViewById(R.id.invalid_todos)
        enabledCardViewLayout.layoutManager = LinearLayoutManager(this)
        disabledCardViewLayout.layoutManager = LinearLayoutManager(this)
        // 设置新增任务按钮的点击事件
        findViewById<ImageView>(R.id.new_todo).setOnClickListener {
            val intent = Intent(this, NewTodoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_TODO)
        }
        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener {
            finish() // 结束当前 Activity，返回上一个界面
        }
    }

    private fun initAll() {
        optionLayer = findViewById(R.id.options_layer)
        cancel = findViewById(R.id.cancel_text)
        cancelModule = findViewById(R.id.cancel_module)
        addTodoModule = findViewById(R.id.add_todo_module)
        addTodo = findViewById(R.id.new_todo)
        deleteTodo = findViewById(R.id.delete_todo)
        alterTodo = findViewById(R.id.alter_todos)
        menu = findViewById(R.id.menu)
        hint = findViewById(R.id.hint_subtitle)
        optionAnimator = ExpandAnimator(this, optionLayer)
            .setIfFade(true)
            .setDuration(100)
        cancelAnimator = ExpandAnimator(this, cancelModule)
            .setDuration(500)
        addModuleAnimator = ExpandAnimator(this, addTodoModule)
            .setIfFade(true)
            .setDuration(200)
        menuAnimator = ExpandAnimator(this, menu)
            .setIfFade(true)
            .setDuration(200)
    }

    private fun addOnListeners() {
        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener {
            finish()
        }
        menu.setOnClickListener {
            showMenu()
        }
        optionLayer.setOnClickListener {
            hideMenu()
        }
        cancel.setOnClickListener {
            hideCancel()
            if (isDeleting) {
                isDeleting = false
            } else if (isAltering) {
                isAltering = false
            }
            updateRecyclerViews()
        }
        deleteTodo.setOnClickListener {
            hint.text = "点击红 X 即可删除待办"
            hint.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeRed
                )
            )
            hideMenu()
            cancel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeRed
                )
            )
            showCancel()
            isDeleting = true
            isAltering = false
            updateRecyclerViews()
        }
        alterTodo.setOnClickListener {
            hint.text = "点击按钮以停用/复用待办"
            hint.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeBlue
                )
            )
            hideMenu()
            cancel.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.themeBlue
                )
            )
            showCancel()
            isAltering = true
            isDeleting = false
            updateRecyclerViews()
        }
    }

    private fun addNewTodo(todoContent: String, selectedDate: String) {
        val todoItem = TodoItem("待办: $todoContent", "创建于: $selectedDate")
        enabledTodoList.add(todoItem)
        Log.d("AllToDoActivity", "添加新待办事项: ${todoItem.title}, ${todoItem.details}")
        updateRecyclerViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_TODO && resultCode == RESULT_OK) {
            val todoContent = data?.getStringExtra("todo_content")
            val selectedDate = data?.getStringExtra("selected_date")
            if (todoContent != null && selectedDate != null) {
                Log.d("AllToDoActivity", "接收到新待办事项: $todoContent, $selectedDate")
                addNewTodo(todoContent, selectedDate)
            }
        }
    }

    private fun deleteTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val todoDetails = todoView.findViewById<TextView>(R.id.todo_details).text.toString()
        // 从已启用列表中移除
        val todoItem = TodoItem(todoTitle, todoDetails)
        enabledTodoList.remove(todoItem)
        updateRecyclerViews()
        Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
    }

    private fun disableTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val todoDetails = todoView.findViewById<TextView>(R.id.todo_details).text.toString()
        // 获取停用日期
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val disabledDate = sdf.format(Date())
        // 从已启用列表中移除
        val todoItem = TodoItem(todoTitle, todoDetails)
        enabledTodoList.remove(todoItem)
        // 添加到已停用列表
        val disabledTodoItem = TodoItem(todoTitle, "停用于 $disabledDate")
        disabledTodoList.add(disabledTodoItem)
        updateRecyclerViews()
        Toast.makeText(this, "待办事项已停用", Toast.LENGTH_SHORT).show()
    }

    private fun enableTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val todoDetails = todoView.findViewById<TextView>(R.id.todo_details).text.toString()
        // 从已停用列表中移除
        val todoItem = TodoItem(todoTitle, todoDetails)
        disabledTodoList.remove(todoItem)
        // 添加到已启用列表
        val enabledTodoItem = TodoItem(todoTitle, todoDetails.split("停用于 ")[1])
        enabledTodoList.add(enabledTodoItem)
        updateRecyclerViews()
        Toast.makeText(this, "待办事项已复用", Toast.LENGTH_SHORT).show()
    }

    private fun showMenu() {
        optionAnimator.setFade(0f, 1f).start()
    }

    private fun hideMenu() {
        optionAnimator.setFade(1f, 0f).start()
    }

    private fun showCancel() {
        cancelAnimator.setMoveDirection(2, -cancelDisplacement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
            .start()
        addModuleAnimator.setFade(1f, 0f)
            .start()
        menuAnimator.setFade(1f, 0f)
            .start()
    }

    private fun hideCancel() {
        hint.text = getString(R.string.hint_subtitle)
        hint.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.general_grey_wzc
            )
        )
        cancelAnimator.setMoveDirection(2, cancelDisplacement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
            .start()
        addModuleAnimator.setFade(0f, 1f)
            .start()
        menuAnimator.setFade(0f, 1f)
            .start()
    }

    private fun updateRecyclerViews() {
        val enabledAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_enabled_chl, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val todoItem = enabledTodoList[position]
                holder.itemView.findViewById<TextView>(R.id.todo_title).text = todoItem.title
                holder.itemView.findViewById<TextView>(R.id.todo_details).text = todoItem.details
                val deleteIcon = holder.itemView.findViewById<ImageView>(R.id.delete_todo_icon)
                val disableButton = holder.itemView.findViewById<MaterialCardView>(R.id.invalid_todo_icon)

                if (isDeleting) {
                    deleteIcon.visibility = View.VISIBLE
                    deleteIcon.setOnClickListener {
                        deleteTodo(holder.itemView)
                    }
                } else {
                    deleteIcon.visibility = View.GONE
                }

                if (isAltering) {
                    disableButton.visibility = View.VISIBLE
                    disableButton.setOnClickListener {
                        disableTodo(holder.itemView)
                    }
                } else {
                    disableButton.visibility = View.GONE
                }
            }

            override fun getItemCount(): Int {
                return enabledTodoList.size
            }
        }
        enabledCardViewLayout.adapter = enabledAdapter

        val disabledAdapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_invalid_chl, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val todoItem = disabledTodoList[position]
                holder.itemView.findViewById<TextView>(R.id.todo_title).text = todoItem.title
                holder.itemView.findViewById<TextView>(R.id.todo_details).text = todoItem.details
                val enableButton = holder.itemView.findViewById<MaterialCardView>(R.id.valid_todo_icon)

                if (isAltering) {
                    enableButton.visibility = View.VISIBLE
                    enableButton.setOnClickListener {
                        enableTodo(holder.itemView)
                    }
                } else {
                    enableButton.visibility = View.GONE
                }
            }

            override fun getItemCount(): Int {
                return disabledTodoList.size
            }
        }
        disabledCardViewLayout.adapter = disabledAdapter
    }
}