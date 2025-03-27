package com.example.plantingapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
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
        const val RESULT_TODO_UPDATED = 2
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
    private val enabledTodoList = mutableListOf<TodoItem>()
    private val disabledTodoList = mutableListOf<TodoItem>()
    private var isDeleting = false
    private var isAltering = false
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_to_do)
        dbHelper = DBHelper(this)
        initAll()
        addOnListeners()

        enabledCardViewLayout = findViewById(R.id.enabled_todos)
        disabledCardViewLayout = findViewById(R.id.invalid_todos)
        enabledCardViewLayout.layoutManager = LinearLayoutManager(this)
        disabledCardViewLayout.layoutManager = LinearLayoutManager(this)

        loadTodosFromDatabase()

        findViewById<ImageView>(R.id.new_todo).setOnClickListener {
            val intent = Intent(this, NewTodoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_TODO)
        }

        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener {
            finish()
        }
    }

    private fun loadTodosFromDatabase() {
        enabledTodoList.clear()
        disabledTodoList.clear()

        dbHelper.readableDatabase.use { db ->
            val projection = arrayOf("todoId", "todoName", "createTime", "endTime", "interval", "isEnabled")

            // Load enabled todos (not deleted)
            db.query(
                "todo",
                projection,
                "isEnabled = ? AND isDeleted = ?",
                arrayOf("1", "0"),
                null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val todoName = cursor.getString(cursor.getColumnIndexOrThrow("todoName"))
                    val createTime = cursor.getLong(cursor.getColumnIndexOrThrow("createTime"))
                    enabledTodoList.add(TodoItem(todoName, "创建于: ${formatDate(createTime)}"))
                }
            }

            // Load disabled todos (not deleted)
            db.query(
                "todo",
                projection,
                "isEnabled = ? AND isDeleted = ?",
                arrayOf("0", "0"),
                null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val todoName = cursor.getString(cursor.getColumnIndexOrThrow("todoName"))
                    val createTime = cursor.getLong(cursor.getColumnIndexOrThrow("createTime"))
                    disabledTodoList.add(TodoItem(todoName, "停用于 ${formatDate(createTime)}"))
                }
            }
        }

        // 根据列表是否为空来显示/隐藏提示文字
        val noEnabledTodosText = findViewById<TextView>(R.id.no_enabled_todos)
        noEnabledTodosText.visibility = if (enabledTodoList.isEmpty()) View.VISIBLE else View.GONE

        updateRecyclerViews()
    }

    private fun formatDate(time: Long): String {
        return SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date(time))
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
        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener { finish() }

        menu.setOnClickListener { showMenu() }
        optionLayer.setOnClickListener { hideMenu() }

        cancel.setOnClickListener {
            hideCancel()
            isDeleting = false
            isAltering = false
            updateRecyclerViews()
        }

        deleteTodo.setOnClickListener {
            hint.text = "点击红 X 即可删除待办"
            hint.setTextColor(ContextCompat.getColor(this, R.color.themeRed))
            hideMenu()
            cancel.setTextColor(ContextCompat.getColor(this, R.color.themeRed))
            showCancel()
            isDeleting = true
            isAltering = false
            updateRecyclerViews()
        }

        alterTodo.setOnClickListener {
            hint.text = "点击按钮以停用/复用待办"
            hint.setTextColor(ContextCompat.getColor(this, R.color.themeBlue))
            hideMenu()
            cancel.setTextColor(ContextCompat.getColor(this, R.color.themeBlue))
            showCancel()
            isAltering = true
            isDeleting = false
            updateRecyclerViews()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_TODO && resultCode == RESULT_OK) {
            loadTodosFromDatabase()
            setResult(RESULT_TODO_UPDATED)
        }
    }

    private fun deleteTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val realTitle = todoTitle.removePrefix("待办: ")

        if (dbHelper.softDeleteTodo(realTitle, 1) > 0) {
            enabledTodoList.removeIf { it.title == todoTitle }
            updateRecyclerViews()
            Toast.makeText(this, "待办事项已删除", Toast.LENGTH_SHORT).show()
            setResult(RESULT_TODO_UPDATED)
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val realTitle = todoTitle.removePrefix("待办: ")

        dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply { put("isEnabled", 0) }
            val whereClause = "todoName = ? AND isEnabled = ? AND isDeleted = ?"
            val whereArgs = arrayOf(realTitle, "1", "0")

            if (db.update("todo", values, whereClause, whereArgs) > 0) {
                val disabledDate = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date())
                enabledTodoList.removeIf { it.title == todoTitle }
                disabledTodoList.add(TodoItem(todoTitle, "停用于 $disabledDate"))
                updateRecyclerViews()
                Toast.makeText(this, "待办事项已停用", Toast.LENGTH_SHORT).show()
                setResult(RESULT_TODO_UPDATED)
            }
        }
    }

    private fun enableTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val realTitle = todoTitle.removePrefix("待办: ")

        dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply { put("isEnabled", 1) }
            val whereClause = "todoName = ? AND isEnabled = ? AND isDeleted = ?"
            val whereArgs = arrayOf(realTitle, "0", "0")

            if (db.update("todo", values, whereClause, whereArgs) > 0) {
                val details = todoView.findViewById<TextView>(R.id.todo_details).text.toString()
                disabledTodoList.removeIf { it.title == todoTitle }
                enabledTodoList.add(TodoItem(todoTitle, details.split("停用于 ")[1]))
                updateRecyclerViews()
                Toast.makeText(this, "待办事项已复用", Toast.LENGTH_SHORT).show()
                setResult(RESULT_TODO_UPDATED)
            }
        }
    }

    private fun deleteDisabledTodo(todoView: View) {
        val todoTitle = todoView.findViewById<TextView>(R.id.todo_title).text.toString()
        val realTitle = todoTitle.removePrefix("待办: ")

        if (dbHelper.softDeleteTodo(realTitle, 0) > 0) {
            disabledTodoList.removeIf { it.title == todoTitle }
            updateRecyclerViews()
            Toast.makeText(this, "已停用待办事项已删除", Toast.LENGTH_SHORT).show()
            setResult(RESULT_TODO_UPDATED)
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
        }
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
        addModuleAnimator.setFade(1f, 0f).start()
        menuAnimator.setFade(1f, 0f).start()
    }

    private fun hideCancel() {
        hint.text = getString(R.string.hint_subtitle)
        hint.setTextColor(ContextCompat.getColor(this, R.color.general_grey_wzc))
        cancelAnimator.setMoveDirection(2, cancelDisplacement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
            .start()
        addModuleAnimator.setFade(0f, 1f).start()
        menuAnimator.setFade(0f, 1f).start()
    }

    private fun updateRecyclerViews() {
        // 处理启用待办列表的显示状态
        val noEnabledTodosText = findViewById<TextView>(R.id.no_enabled_todos)
        noEnabledTodosText.visibility = if (enabledTodoList.isEmpty()) View.VISIBLE else View.GONE

        enabledCardViewLayout.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_todo_enabled_chl, parent, false)
                ) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val todoItem = enabledTodoList[position]
                holder.itemView.apply {
                    findViewById<TextView>(R.id.todo_title).text = todoItem.title
                    findViewById<TextView>(R.id.todo_details).text = todoItem.details

                    val deleteIcon = findViewById<ImageView>(R.id.delete_todo_icon)
                    val disableButton = findViewById<MaterialCardView>(R.id.invalid_todo_icon)

                    deleteIcon.visibility = if (isDeleting) View.VISIBLE else View.GONE
                    disableButton.visibility = if (isAltering) View.VISIBLE else View.GONE

                    deleteIcon.setOnClickListener { deleteTodo(holder.itemView) }
                    disableButton.setOnClickListener { disableTodo(holder.itemView) }
                }
            }

            override fun getItemCount(): Int = enabledTodoList.size
        }

        // 处理停用待办列表的显示状态
        val noDisabledTodosText = findViewById<TextView>(R.id.no_invalid_todos)
        noDisabledTodosText.visibility = if (disabledTodoList.isEmpty()) View.VISIBLE else View.GONE

        disabledCardViewLayout.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_todo_invalid_chl, parent, false)
                ) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val todoItem = disabledTodoList[position]
                holder.itemView.apply {
                    findViewById<TextView>(R.id.todo_title).text = todoItem.title
                    findViewById<TextView>(R.id.todo_details).text = todoItem.details

                    val enableButton = findViewById<MaterialCardView>(R.id.valid_todo_icon)
                    val deleteIcon = findViewById<ImageView>(R.id.delete_todo_icon)

                    enableButton.visibility = if (isAltering) View.VISIBLE else View.GONE
                    deleteIcon.visibility = if (isDeleting) View.VISIBLE else View.GONE

                    enableButton.setOnClickListener { enableTodo(holder.itemView) }
                    deleteIcon.setOnClickListener { deleteDisabledTodo(holder.itemView) }
                }
            }

            override fun getItemCount(): Int = disabledTodoList.size
        }
    }
}