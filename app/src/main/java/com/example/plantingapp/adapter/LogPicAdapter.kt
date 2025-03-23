package com.example.plantingapp.adapter

import android.R.attr.data
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantingapp.R
import com.example.plantingapp.item.LogPicItem
import java.io.File


class LogPicAdapter(
    var items: MutableList<LogPicItem>,
    private val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder绑定视图
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.pic)
        val deleteIcon: ImageButton = itemView.findViewById(R.id.delete_pic_icon)

        fun load(d:LogPicItem){
            Log.v("startloadingpicture","1")
            Glide.with(context)
                .load(Uri.parse(d.uriString))
                .into(pic)
            Log.v("successful","2")

            when(d.status){
                LogPicItem.MODE_DISPLAY -> {
                    deleteIcon.visibility = View.GONE
                }
                LogPicItem.MODE_DEL -> {
                    deleteIcon.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_pic_wzc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> {
                val item = items[position]
                holder.load(item)
                // 删除按钮点击事件
                holder.deleteIcon.setOnClickListener {
                    // 1. 从列表移除
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(0,items.size)

                    // 2. 删除物理文件
                    val file = File(Uri.parse(item.uriString).path ?: "")
                    if (file.exists()) {
                        file.delete()
                    }

                }

            }
        }
    }

    override fun getItemCount(): Int = items.size



}