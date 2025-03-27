package com.example.plantingapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R
import com.example.plantingapp.Utils
import com.example.plantingapp.converter.TodoConverter
import com.example.plantingapp.dao.ToDoDAO
import com.example.plantingapp.item.UniversalTodoItem
import java.text.SimpleDateFormat
import java.util.*

class AllTodoAdapter(
    private val context: Context,
    val dataList: MutableList<UniversalTodoItem>,
    private val itemCallback: (UniversalTodoItem,Int) -> Unit // 点击事件回调
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutRes = when (viewType) {
            UniversalTodoItem.STATUS_RUNNING -> R.layout.item_todo_enabled_chl
            UniversalTodoItem.STATUS_DISABLED -> R.layout.item_todo_invalid_chl
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return LayoutInflater.from(context).inflate(layoutRes, parent, false).let { view ->
            when (viewType) {
                UniversalTodoItem.STATUS_RUNNING -> EnabledViewHolder(view)
                UniversalTodoItem.STATUS_DISABLED -> DisabledViewHolder(view)
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun onBindViewHolder(v: RecyclerView.ViewHolder, pos: Int) {
        var d = dataList[pos]
        when(v){
            is EnabledViewHolder -> {
                v.titleTv.text = d.todoName
                v.detailTv.text = "创建于 " + formatCreateTime(d.createTime)
                if(d.logGroupId != null && d.logGroupId!! >= 1){
                    Log.v("ALL todo adapter","loading group id ${d.logGroupId}")
                    v.group.visibility = View.VISIBLE
                    v.group.text = "关联于 ${d.attachedGroupName}"
                }else{
                    v.group.visibility = View.GONE
                }
                when(d.displayMode){
                    UniversalTodoItem.DISPLAY_NORMAL -> {
                        v.deleteIv.visibility = View.GONE
                        v.invalidBtn.visibility = View.GONE
                        v.touchLayer.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    NewTodoActivity::class.java
                                ).apply {
                                    putExtra("alter_todo",TodoConverter.toJson(d))
                                }
                            )
                        }
                    }
                    UniversalTodoItem.DISPLAY_ALTER -> {
                        v.deleteIv.visibility = View.GONE
                        v.invalidBtn.visibility = View.VISIBLE
                        v.touchLayer.setOnClickListener {
                            val tempd = d
                            dataList.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(0,dataList.size)
                            itemCallback(tempd, OPTION_REVERSE)
                        }
                    }
                    UniversalTodoItem.DISPLAY_DEL -> {
                        v.deleteIv.visibility = View.VISIBLE
                        v.invalidBtn.visibility = View.GONE
                        v.touchLayer.setOnClickListener {
                            val tempd = d
                            dataList.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(0,dataList.size)
                            itemCallback(tempd, OPTION_DEL)
                        }
                    }
                }
            }
            is DisabledViewHolder -> {
                v.titleTv.text = d.todoName
                v.detailTv.text = "创建于 " + formatCreateTime(d.createTime)
                if(d.logGroupId != null && d.logGroupId!! >= 1){
                    v.group.visibility = View.VISIBLE
                    v.group.text = "关联于 ${d.attachedGroupName}"
                }else{
                    v.group.visibility = View.GONE
                }
                when(d.displayMode){
                    UniversalTodoItem.DISPLAY_NORMAL -> {
                        v.deleteIv.visibility = View.GONE
                        v.enableBtn.visibility = View.GONE
                        v.touchLayer.setOnClickListener {
                            context.startActivity(
                                Intent(
                                    context,
                                    NewTodoActivity::class.java
                                ).apply {
                                    putExtra("alter_todo",TodoConverter.toJson(d))
                                }
                            )
                        }
                    }
                    UniversalTodoItem.DISPLAY_ALTER -> {
                        v.deleteIv.visibility = View.GONE
                        v.enableBtn.visibility = View.VISIBLE
                        v.touchLayer.setOnClickListener {
                            val tempd = d
                            dataList.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(0,dataList.size)
                            itemCallback(tempd, OPTION_REVERSE)
                        }
                    }
                    UniversalTodoItem.DISPLAY_DEL -> {
                        v.deleteIv.visibility = View.VISIBLE
                        v.enableBtn.visibility = View.GONE
                        v.touchLayer.setOnClickListener {
                            val tempd = d
                            dataList.removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(0,dataList.size)
                            itemCallback(tempd, OPTION_DEL)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = dataList.size

    override fun getItemViewType(position: Int): Int {
        return dataList[position].isEnabled
    }

    inner class EnabledViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.todo_title)
        val detailTv: TextView = itemView.findViewById(R.id.todo_details)
        val deleteIv: ImageView = itemView.findViewById(R.id.delete_todo_icon)
        val invalidBtn: TextView = itemView.findViewById(R.id.invalid)
        val touchLayer:View = itemView.findViewById(R.id.touch_edit_layer)
        val group:TextView = itemView.findViewById(R.id.todo_group)

    }

    inner class DisabledViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.todo_title)
        val detailTv: TextView = itemView.findViewById(R.id.todo_details)
        val deleteIv: ImageView = itemView.findViewById(R.id.delete_todo_icon)
        val enableBtn: TextView = itemView.findViewById(R.id.enable)
        val touchLayer:View = itemView.findViewById(R.id.touch_edit_layer)
        val group:TextView = itemView.findViewById(R.id.todo_group)

    }

    private fun formatCreateTime(timestamp: Long): String {
        return SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date(timestamp))
    }

    companion object{
        const val OPTION_REVERSE = 0
        const val OPTION_DEL = 1
    }
}