package com.example.plantingapp.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.DBHelper
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R
import com.google.android.material.card.MaterialCardView

class TodoFragment : Fragment() {
    private lateinit var mainPageTodosRecyclerView: RecyclerView
    private lateinit var finishedTodoRecyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var finishedTodoAdapter: TodoAdapter
    private val todoList = mutableListOf<String>()
    private val finishedTodoList = mutableListOf<String>()
    private lateinit var dbHelper: DBHelper

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        initViews(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        loadTodosFromDatabase()
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
    }

    private fun refreshDataAndScrollToTop() {
        loadTodosFromDatabase()
        if (todoList.isNotEmpty()) {
            mainPageTodosRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun loadTodosFromDatabase() {
        todoList.clear()
        finishedTodoList.clear()

        dbHelper.readableDatabase.use { db ->
            db.query(
                "todo",
                arrayOf("todoName", "isEnabled"),
                "isEnabled IN (1,2)",
                null, null, null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("todoName"))
                    when (cursor.getInt(cursor.getColumnIndexOrThrow("isEnabled"))) {
                        1 -> todoList.add(name)
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

        if (isFinished) {
            moveItem(todoName, todoList, finishedTodoList)
        } else {
            moveItem(todoName, finishedTodoList, todoList)
        }
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