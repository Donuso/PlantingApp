package com.example.plantingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.TodoItem

class TodoAdapter(private val todoList: MutableList<TodoItem>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_enabled_chl, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = todoList[position]
        holder.todoTitle.text = todoItem.title
        holder.todoDetails.text = todoItem.details
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle: TextView = itemView.findViewById(R.id.todo_title)
        val todoDetails: TextView = itemView.findViewById(R.id.todo_details)
    }
}