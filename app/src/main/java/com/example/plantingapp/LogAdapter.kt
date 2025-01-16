package com.example.plantingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private val logs: List<String>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val textView = holder.view.findViewById<TextView>(R.id.log_text)
        textView.text = logs[position]
    }

    override fun getItemCount() = logs.size
}
