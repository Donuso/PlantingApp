package com.example.plantingapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R
import com.example.plantingapp.SearchActivity
import com.example.plantingapp.adapter.TodayTodoAdapter
import com.example.plantingapp.converter.TodoConverter
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.TodayTodoItem
import com.example.plantingapp.utils.TodayTodoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            todoAdapter.notifyDataSetChanged()
        }
        if(todoAdapter.todoList.size == 0){
            noTodoReminder.visibility = View.VISIBLE
            todayTodos.visibility = View.GONE
        }else{
            noTodoReminder.visibility = View.GONE
            todayTodos.visibility = View.VISIBLE
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            TodayTodoManager.calculateTodayTodos(requireContext(),DataExchange.USERID!!.toInt())
            val dataStr = requireContext().
                getSharedPreferences(
                    "${DataExchange.USERID}_prefs",
                    Context.MODE_PRIVATE).
                getString("today_todos","")
            if(dataStr.isNullOrEmpty()){
                Toast.makeText(requireContext(),"首页待办加载失败",Toast.LENGTH_SHORT).show()
            }else{
                todoAdapter.todoList = TodoConverter.fromJsonToList(dataStr)
                todoAdapter.notifyDataSetChanged()
            }
            if(todoAdapter.todoList.size == 0){
                noTodoReminder.visibility = View.VISIBLE
                todayTodos.visibility = View.GONE
            }else{
                noTodoReminder.visibility = View.GONE
                todayTodos.visibility = View.VISIBLE
            }
        }else{
            with(requireContext().getSharedPreferences("${DataExchange.USERID}_prefs", Context.MODE_PRIVATE).edit()){
                putString("today_todos",TodoConverter.toJson(todoAdapter.todoList))
                apply()
            }
        }
    }
}