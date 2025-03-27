package com.example.plantingapp.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import androidx.work.WorkerParameters
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.DBHelper
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R
import com.example.plantingapp.item.DataExchange
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.material.card.MaterialCardView

class TodoFragment : Fragment() {
    private lateinit var mainPageTodosRecyclerView: RecyclerView
    private lateinit var finishedTodoRecyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var finishedTodoAdapter: TodoAdapter
    private val todoList = mutableListOf<String>()
    private val finishedTodoList = mutableListOf<String>()
    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPrefs: SharedPreferences

    private val newTodoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshDataAndScrollToTop()
        }
    }

    private val allTodoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK || result.resultCode == AllToDoActivity.RESULT_TODO_UPDATED) {
            loadTodosFromDatabase()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DBHelper(requireContext())
        sharedPrefs = requireContext().getSharedPreferences("${DataExchange.USERID}_prefs", Context.MODE_PRIVATE)
        setupDailyRefreshWorker()
    }

    private fun setupDailyRefreshWorker() {
        val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .build()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = PeriodicWorkRequestBuilder<DailyTodoRefreshWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "daily_todo_refresh",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        initViews(view)
        checkAndUpdateTodoList() // 在初始化视图之后调用
        return view
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdateTodoList()
    }

    private fun initViews(view: View) {
        mainPageTodosRecyclerView = view.findViewById(R.id.main_page_todos)
        finishedTodoRecyclerView = view.findViewById(R.id.finish_todo)

        mainPageTodosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        finishedTodoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        todoAdapter = TodoAdapter(todoList, false, ::handleTodoStatusChange)
        finishedTodoAdapter = TodoAdapter(finishedTodoList, true, ::handleTodoStatusChange)

        mainPageTodosRecyclerView.adapter = todoAdapter
        finishedTodoRecyclerView.adapter = finishedTodoAdapter

        view.findViewById<ImageView>(R.id.new_todo).setOnClickListener {
            newTodoLauncher.launch(Intent(requireContext(), NewTodoActivity::class.java))
        }

        view.findViewById<TextView>(R.id.manage_all_todos).setOnClickListener {
            allTodoLauncher.launch(Intent(requireContext(), AllToDoActivity::class.java))
        }

        updateNoTodoReminderVisibility()
    }

    private fun refreshDataAndScrollToTop() {
        loadTodosFromDatabase()
        if (todoList.isNotEmpty()) {
            mainPageTodosRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun checkAndUpdateTodoList() {
        when (sharedPrefs.getString("todo_status", null)) {
            "outdated" -> {
                val todoJson = sharedPrefs.getString("today_todos", null)
                if (todoJson != null) {
                    val gson = Gson()
                    val todos = gson.fromJson(todoJson, Array<String>::class.java).toMutableList()
                    todoList.clear()
                    todoList.addAll(todos)
                    todoAdapter.notifyDataSetChanged()
                    sharedPrefs.edit().putString("todo_status", "latest").apply()
                    updateNoTodoReminderVisibility()
                }
            }
            null -> {
                loadTodosFromDatabase()
                sharedPrefs.edit().apply {
                    putString("todo_status", "latest")
                    // 计算今日待办并保存到SP
                    val todayTodosJson = Gson().toJson(todoList)
                    putString("today_todos", todayTodosJson)
                    apply()
                }
            }
            else -> loadTodosFromDatabase()
        }
    }

    private fun loadTodosFromDatabase() {
        todoList.clear()
        finishedTodoList.clear()

        dbHelper.readableDatabase.use { db ->
            db.query(
                "todo",
                arrayOf("todoName", "isEnabled"),
                "isEnabled IN (0,1) AND isDeleted = 0", // 包含停用状态（假设 isEnabled 为 0）
                null, null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("todoName"))
                    when (cursor.getInt(cursor.getColumnIndexOrThrow("isEnabled"))) {
                        0, 1 -> todoList.add(name)
                        2 -> finishedTodoList.add(name)
                    }
                }
            }
        }

        todoAdapter.notifyDataSetChanged()
        finishedTodoAdapter.notifyDataSetChanged()
        updateNoTodoReminderVisibility()
    }

    private fun handleTodoStatusChange(todoName: String, isFinished: Boolean) {
        dbHelper.writableDatabase.use { db ->
            db.execSQL(
                "UPDATE todo SET isEnabled = ? WHERE todoName = ?",
                arrayOf(if (isFinished) 2 else 1, todoName)
            )
        }

        updateFinishedCountInSP(isFinished)

        if (isFinished) {
            moveItem(todoName, todoList, finishedTodoList)
        } else {
            moveItem(todoName, finishedTodoList, todoList)
        }
    }

    private fun updateFinishedCountInSP(isFinished: Boolean) {
        val currentCount = sharedPrefs.getInt("todo_finished_count", 0)
        sharedPrefs.edit().apply {
            putInt("todo_finished_count", if (isFinished) currentCount + 1 else currentCount - 1)
            apply()
        }
        android.util.Log.d("TodoFragment", "Updated todo_finished_count: ${sharedPrefs.getInt("todo_finished_count", 0)}")
    }

    private fun moveItem(item: String, from: MutableList<String>, to: MutableList<String>) {
        from.indexOf(item).takeIf { it != -1 }?.let { position ->
            from.removeAt(position)
            to.add(0, item)
            todoAdapter.notifyDataSetChanged()
            finishedTodoAdapter.notifyDataSetChanged()
            updateNoTodoReminderVisibility()
        }
    }

    private fun updateNoTodoReminderVisibility() {
        view?.findViewById<TextView>(R.id.no_todo_reminder)?.visibility =
            if (todoList.isEmpty() && finishedTodoList.isEmpty()) View.VISIBLE else View.GONE
    }

    private inner class TodoAdapter(
        private val items: MutableList<String>,
        private val isFinished: Boolean,
        private val onStatusChange: (String, Boolean) -> Unit
    ) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TodoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_todos_main_page_chl, parent, false)
            )

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
            private val statusIndicator: MaterialCardView = itemView.findViewById(R.id.statusIndicator)
            private val checkIcon: ImageView = itemView.findViewById(R.id.checkIcon)

            fun bind(todoName: String) {
                todoTextView.text = todoName
                checkIcon.visibility = if (isFinished) View.VISIBLE else View.GONE

                statusIndicator.apply {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    strokeWidth = 1
                    setOnClickListener { onStatusChange(todoName, !isFinished) }
                }
            }
        }
    }
}

class DailyTodoRefreshWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val dbHelper = DBHelper(applicationContext)
        val userId = DataExchange.USERID ?: return Result.failure()

        // 计算今日待办
        val todayTodos = mutableListOf<String>()
        dbHelper.readableDatabase.use { db ->
            val cursor = db.query(
                "todo",
                arrayOf("todoName"),
                "isEnabled IN (0,1) AND isDeleted = 0", // 包含停用状态（假设 isEnabled 为 0）
                null, null, null, null
            )
            while (cursor.moveToNext()) {
                todayTodos.add(cursor.getString(cursor.getColumnIndexOrThrow("todoName")))
            }
            cursor.close()
        }

        // 保存到SharedPreferences
        val prefs = applicationContext.getSharedPreferences("${userId}_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("today_todos", Gson().toJson(todayTodos))
            putString("todo_status", "outdated")
            putInt("todo_finished_count", 0) // 重置每日完成计数
            apply()
        }

        return Result.success()
    }
}