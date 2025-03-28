package com.example.plantingapp.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.TodayTodoItem
import com.google.android.material.card.MaterialCardView

class TodayTodoAdapter(
    private val context: Context,
    var todoList: MutableList<TodayTodoItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<TodayTodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_todos_main_page_new, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(v: TodoViewHolder, position: Int) {
        v.todoTextView.text = todoList[position].todoName
        when(todoList[position].status){
            TodayTodoItem.STATUS_UNFINISHED -> {
                Log.v("loading UNFINISHED",todoList[position].toString())
                v.statusIndicator.apply {
                    strokeColor = ContextCompat.getColor(context,R.color.mass_text_grey_wzc)
                    strokeWidth = 1
                    setCardBackgroundColor(
                        ContextCompat.getColor(context,R.color.white)
                    )
                }
            }
            TodayTodoItem.STATUS_FINISHED -> {
                Log.v("loading FINISHED",todoList[position].toString())
                v.statusIndicator.apply {
                    strokeWidth = 0
                    setCardBackgroundColor(
                        ContextCompat.getColor(context,R.color.themeDarkGreen)
                    )
                }
            }
        }
        when(todoList[position].attachedGroupName){
            null -> {
                v.todoLogGroup.visibility = View.GONE
            }
            else -> {
                v.todoLogGroup.visibility = View.VISIBLE
                v.todoLogGroup.text = "关联于 ${todoList[position].attachedGroupName}"
            }
        }
        v.touchLayer.setOnClickListener {
            switchMode(v,position)
        }
    }

    override fun getItemCount() = todoList.size

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusIndicator: MaterialCardView = itemView.findViewById(R.id.statusIndicator)
        val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
        val todoLogGroup: TextView = itemView.findViewById(R.id.todo_log_group)
        val touchLayer: View = itemView.findViewById(R.id.todo_touch_layer)
    }

    private fun switchMode(v:TodoViewHolder,position: Int){
        when(todoList[position].status){
            TodayTodoItem.STATUS_UNFINISHED -> {
                todoList[position].status = TodayTodoItem.STATUS_FINISHED
                notifyItemChanged(position)
                onItemClick(TodayTodoItem.STATUS_FINISHED)
            }
            TodayTodoItem.STATUS_FINISHED -> {
                todoList[position].status = TodayTodoItem.STATUS_UNFINISHED
                notifyItemChanged(position)
                onItemClick(TodayTodoItem.STATUS_UNFINISHED)
            }
        }
    }

}