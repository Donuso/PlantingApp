package com.example.plantingapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private var logs: List<String>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    // ViewHolder内部类，用于缓存视图
    class LogViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val logTextView: TextView = view.findViewById(R.id.log_text)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val arrowTextView: TextView = view.findViewById(R.id.arrow) // 获取箭头TextView// 获取删除按钮
    }
// 设置箭头点击事件

    // 创建新的ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    // 将数据绑定到ViewHolder的视图上
    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logText = logs[position]
        holder.logTextView.text = logText
        holder.arrowTextView.setOnClickListener { onLogItemClicked(holder, position) }

        // 设置整个日志项的点击事件
        holder.view.setOnClickListener { onLogItemClicked(holder, position) }
        // 设置删除按钮的点击事件
        holder.deleteButton.setOnClickListener {
            deleteLog(position)
        }

        // 长按日志项显示删除按钮
        holder.view.setOnLongClickListener {
            holder.deleteButton.visibility = View.VISIBLE
            true
        }

        // 点击日志项隐藏删除按钮
        holder.view.setOnClickListener {
            holder.deleteButton.visibility = View.GONE
        }
    }

    // 返回数据集的大小
    override fun getItemCount() = logs.size

    // 更新适配器数据的方法
    fun updateLogs(newLogs: List<String>) {
        logs = newLogs
        notifyDataSetChanged() // 通知数据集发生变化
    }

    // 删除日志的方法
    private fun deleteLog(position: Int) {
        if (position in logs.indices) {
            logs = logs.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, logs.size)
        }
    }
    private fun onLogItemClicked(holder: LogAdapter.LogViewHolder, position: Int) {
        val context = holder.view.context
        val intent = Intent(context, LogDetailActivity::class.java)
        intent.putExtra("LOG_TEXT", logs[position]) // 传递日志文本到详情页
        context.startActivity(intent)
    }

}

// 点击事件的处理逻辑

