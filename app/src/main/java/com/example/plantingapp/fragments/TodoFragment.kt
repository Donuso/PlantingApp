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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.DBHelper
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R
import com.example.plantingapp.SearchActivity
import com.example.plantingapp.adapter.TodayTodoAdapter
import com.example.plantingapp.converter.TodoConverter
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.TodayTodoItem
import com.example.plantingapp.utils.TodayTodoManager
import com.google.android.material.card.MaterialCardView

class TodoFragment : Fragment() {

    private lateinit var todayTodos:RecyclerView
    private lateinit var search:View
    private lateinit var todoAdapter: TodayTodoAdapter
    private lateinit var allTodos: TextView
    private lateinit var newTodo: ImageView
    private lateinit var sp:SharedPreferences
    private lateinit var noTodoReminder:TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        sp = requireContext().getSharedPreferences("${DataExchange.USERID}_prefs",Context.MODE_PRIVATE)
        initViews(view)
        addOnListeners()
        initAdapter()
        return view
    }

    private fun initViews(v:View){
        search = v.findViewById(R.id.search_touch_area)
        allTodos = v.findViewById(R.id.manage_all_todos)
        newTodo = v.findViewById(R.id.new_todo)
        noTodoReminder = v.findViewById(R.id.no_todo_reminder)
        todayTodos = v.findViewById(R.id.main_page_todos)
    }

    private fun addOnListeners(){
        search.setOnClickListener {
            startActivity(
                Intent(requireContext(),SearchActivity::class.java)
            )
        }
        allTodos.setOnClickListener {
            startActivity(
                Intent(requireContext(),AllToDoActivity::class.java)
            )
        }
        newTodo.setOnClickListener{
            startActivity(
                Intent(requireContext(),NewTodoActivity::class.java)
            )
        }
    }

    private fun initAdapter(){
        todoAdapter = TodayTodoAdapter(
            requireContext(),
            mutableListOf(),
            ){ opt ->
            when(opt){
                TodayTodoItem.STATUS_FINISHED -> {
                    with(sp.edit()){
                        putInt(
                            "todo_finished_count",
                            sp.getInt("todo_finished_count",0) + 1
                        )
                        apply()
                    }
                }
                TodayTodoItem.STATUS_UNFINISHED -> {
                    with(sp.edit()){
                        putInt(
                            "todo_finished_count",
                            sp.getInt("todo_finished_count",0) - 1
                        )
                        apply()
                    }
                }
            }
        }
        todayTodos.layoutManager = LinearLayoutManager(requireContext())
        todayTodos.adapter = todoAdapter
    }

    private fun reloadData(){
        TodayTodoManager.calculateTodayTodos(requireContext(),DataExchange.USERID!!.toInt())
        val dataStr = sp.getString("today_todos","")
        if(dataStr.isNullOrEmpty()){
            Toast.makeText(requireContext(),"首页待办加载失败",Toast.LENGTH_SHORT).show()
        }else{
            todoAdapter.todoList = TodoConverter.fromJsonToList(dataStr)
        }
        if(todoAdapter.todoList.size == 0){
            noTodoReminder.visibility = View.VISIBLE
            todayTodos.visibility = View.GONE
        }else{
            noTodoReminder.visibility = View.GONE
            todayTodos.visibility = View.VISIBLE
            todoAdapter.notifyDataSetChanged()
        }
    }

    private fun storeData(){
        with(sp.edit()){
            putString("today_todos",TodoConverter.toJson(todoAdapter.todoList))
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        storeData()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

//    override fun onHiddenChanged(hidden: Boolean) {
//        super.onHiddenChanged(hidden)
//        if(!hidden){
//            TodayTodoManager.calculateTodayTodos(requireContext(),DataExchange.USERID!!.toInt())
//            val dataStr = requireContext().
//                getSharedPreferences(
//                    "${DataExchange.USERID}_prefs",
//                    Context.MODE_PRIVATE).
//                getString("today_todos","")
//            if(dataStr.isNullOrEmpty()){
//                Toast.makeText(requireContext(),"首页待办加载失败",Toast.LENGTH_SHORT).show()
//            }else{
//                todoAdapter.todoList = TodoConverter.fromJsonToList(dataStr)
//            }
//            if(todoAdapter.todoList.size == 0){
//                noTodoReminder.visibility = View.VISIBLE
//                todayTodos.visibility = View.GONE
//            }else{
//                noTodoReminder.visibility = View.GONE
//                todayTodos.visibility = View.VISIBLE
//                todoAdapter.notifyDataSetChanged()
//            }
//        }
//    }


//    private lateinit var mainPageTodosRecyclerView: RecyclerView
////    private lateinit var finishedTodoRecyclerView: RecyclerView
////    private lateinit var todoAdapter: TodoAdapter
////    private lateinit var finishedTodoAdapter: TodoAdapter
////    private val todoList = mutableListOf<String>()
////    private val finishedTodoList = mutableListOf<String>()
////    private lateinit var dbHelper: DBHelper
//    private lateinit var search:View
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        dbHelper = DBHelper(requireContext())
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_todo, container, false)
//        initViews(view)
//        return view
//    }

//    private val newTodoLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            refreshDataAndScrollToTop()
//        }
//    }
//
//    private val allTodoLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK || result.resultCode == AllToDoActivity.RESULT_TODO_UPDATED) {
//            loadTodosFromDatabase()
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        loadTodosFromDatabase()
//    }

//    private fun initViews(view: View) {
//        mainPageTodosRecyclerView = view.findViewById(R.id.main_page_todos)
//        search = view.findViewById(R.id.search_touch_area)
//
//        mainPageTodosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        todoAdapter = TodoAdapter(todoList, false, ::handleTodoStatusChange)
//
//        mainPageTodosRecyclerView.adapter = todoAdapter
//
//
//
//        search.setOnClickListener {
//            startActivity(Intent(requireContext(),SearchActivity::class.java))
//        }
//        view.findViewById<ImageView>(R.id.new_todo).setOnClickListener {
//            newTodoLauncher.launch(Intent(requireContext(), NewTodoActivity::class.java))
//        }
//
//        view.findViewById<TextView>(R.id.manage_all_todos).setOnClickListener {
//            allTodoLauncher.launch(Intent(requireContext(), AllToDoActivity::class.java))
//        }
//    }

//    private fun refreshDataAndScrollToTop() {
//        loadTodosFromDatabase()
//        if (todoList.isNotEmpty()) {
//            mainPageTodosRecyclerView.smoothScrollToPosition(0)
//        }
//    }

//    private fun loadTodosFromDatabase() {
//        todoList.clear()
////        finishedTodoList.clear()
//
//        dbHelper.readableDatabase.use { db ->
//            db.query(
//                "todo",
//                arrayOf("todoName", "isEnabled"),
//                "isEnabled IN (1,2)",
//                null, null, null, null
//            )?.use { cursor ->
//                while (cursor.moveToNext()) {
//                    val name = cursor.getString(cursor.getColumnIndexOrThrow("todoName"))
//                    when (cursor.getInt(cursor.getColumnIndexOrThrow("isEnabled"))) {
//                        1 -> todoList.add(name)
////                        2 -> finishedTodoList.add(name)
//                    }
//                }
//            }
//        }
//
//        todoAdapter.notifyDataSetChanged()
////        finishedTodoAdapter.notifyDataSetChanged()
//        updateNoTodoReminderVisibility()
//    }

//    private fun handleTodoStatusChange(todoName: String, isFinished: Boolean) {
//        dbHelper.writableDatabase.use { db ->
//            db.execSQL(
//                "UPDATE todo SET isEnabled = ? WHERE todoName = ?",
//                arrayOf(if (isFinished) 2 else 1, todoName)
//            )
//        }

//        if (isFinished) {
//            moveItem(todoName, todoList, finishedTodoList)
//        } else {
//            moveItem(todoName, finishedTodoList, todoList)
//        }
//    }

//    private fun moveItem(item: String, from: MutableList<String>, to: MutableList<String>) {
//        from.indexOf(item).takeIf { it != -1 }?.let { position ->
//            from.removeAt(position)
//            to.add(0, item)
//            todoAdapter.notifyDataSetChanged()
//            finishedTodoAdapter.notifyDataSetChanged()
//            updateNoTodoReminderVisibility()
//        }
//    }

//    private fun updateNoTodoReminderVisibility() {
//        view?.findViewById<TextView>(R.id.no_todo_reminder)?.visibility =
//            if (todoList.isEmpty() && finishedTodoList.isEmpty()) View.VISIBLE else View.GONE
//    }

//    private inner class TodoAdapter(
//        private val items: MutableList<String>,
//        private val isFinished: Boolean,
//        private val onStatusChange: (String, Boolean) -> Unit
//    ) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
//            TodoViewHolder(
//                LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_todos_main_page_chl, parent, false)
//            )
//
//        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
//            holder.bind(items[position])
//        }
//
//        override fun getItemCount() = items.size
//
//        inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            private val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
//            private val statusIndicator: MaterialCardView = itemView.findViewById(R.id.statusIndicator)
//            private val checkIcon: ImageView = itemView.findViewById(R.id.checkIcon)
//
//            fun bind(todoName: String) {
//                todoTextView.text = todoName
//                checkIcon.visibility = if (isFinished) View.VISIBLE else View.GONE
//
//                statusIndicator.apply {
//                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
//                    strokeWidth = 1
//                    setOnClickListener { onStatusChange(todoName, !isFinished) }
//                }
//            }
//        }
//    }
}