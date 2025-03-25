package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.SearchLogGroupAdapter
import com.example.plantingapp.adapter.SearchToDoAdapter
import com.example.plantingapp.dao.SearchDAO
import com.example.plantingapp.item.DataExchange

class SearchActivity : BaseActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var logGroupAdapter: SearchLogGroupAdapter
    private lateinit var todoAdapter: SearchToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        dbHelper = DBHelper(this)

        setupRecyclerViews()
        setupSearch()
        setupBackButton()
    }

    private fun setupRecyclerViews() {
        val logRecyclerView = findViewById<RecyclerView>(R.id.log_list)
        val todoRecyclerView = findViewById<RecyclerView>(R.id.todo_list)

        logGroupAdapter = SearchLogGroupAdapter(emptyList()) { logGroup ->
            // 处理日志组点击事件
            val intent = Intent(this, LogActivity::class.java)
            intent.putExtra("log_group_id", logGroup.logGroupId)
            intent.putExtra("log_group_name", logGroup.groupName)
            startActivity(intent)
        }

        todoAdapter = SearchToDoAdapter(emptyList()) { todoItem ->
            // 处理待办事项点击事件（预留）
            val intent = Intent(this, MainSwitchActivity::class.java)
            intent.putExtra("todo_id", todoItem.todoId)
            intent.putExtra("todo_title", todoItem.todoName)
            startActivity(intent)
        }

        logRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = logGroupAdapter
        }

        todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = todoAdapter
        }
    }

    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.search_text)
        findViewById<ImageView>(R.id.Search).setOnClickListener {
            val keyword = searchEditText.text.toString().trim()
            performSearch(keyword)
        }
    }

    private fun performSearch(keyword: String) {
        val userId = DataExchange.USERID?.toIntOrNull() ?: return
        val dao = SearchDAO(this)


        val logGroups = dao.searchLogGroups(dbHelper, userId, keyword)
        val todos = dao.searchTodos(dbHelper, userId, keyword)

        logGroupAdapter.items = logGroups
        todoAdapter.items = todos

        logGroupAdapter.notifyDataSetChanged()
        todoAdapter.notifyDataSetChanged()

        // 处理空数据展示
        findViewById<LinearLayout>(R.id.relatedLog).visibility =
            if (logGroups.isEmpty()) View.GONE else View.VISIBLE
        findViewById<LinearLayout>(R.id.relatedAgency).visibility =
            if (todos.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.backToHome).setOnClickListener {
            finish()
        }
    }
}