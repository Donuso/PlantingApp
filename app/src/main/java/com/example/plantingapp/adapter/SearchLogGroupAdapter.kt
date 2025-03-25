package com.example.plantingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.LogGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchLogGroupAdapter(
    var items: List<LogGroup>,
    private val onItemClick: (LogGroup) -> Unit
) : RecyclerView.Adapter<SearchLogGroupAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text)
        val timeView: TextView = view.findViewById(R.id.time)

        init {
            view.findViewById<View>(R.id.touch_layer).setOnClickListener {
                onItemClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_list_zhj, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.groupName
        holder.timeView.text = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
            .format(Date(item.createdTime))
    }

    override fun getItemCount() = items.size
}