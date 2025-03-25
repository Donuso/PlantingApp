package com.example.plantingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.LabelItem

class AddedLabelAdapter(
    var labelList: MutableList<LabelItem>,
    private val context:Context,
    private val itemCallback: (LabelItem) -> Unit
) : RecyclerView.Adapter<AddedLabelAdapter.LabelViewHolder>() {

    inner class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelIcon = itemView.findViewById<ImageView>(R.id.label_icon)
        val labelName = itemView.findViewById<TextView>(R.id.name)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.delete_label)
        val labelHint = itemView.findViewById<TextView>(R.id.label_hint)
        val contents = itemView.findViewById<TextView>(R.id.label_contents)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_label_wzc, parent, false) // 使用正确的布局文件
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(h: LabelViewHolder, position: Int) {
        val d = labelList[position]

        // 设置图标，假设 tagIcon 是 drawable 资源 ID
        h.labelIcon.setImageResource(d.tagIcon)

        // 设置标签名称
        h.labelName.text = d.tagName

        when(d.tagType){
            LabelItem.TYPE_STATUS -> {
                h.contents.text = getStatusText(d.valStatus!!)
                h.contents.setTextColor(
                    getStatusColor(d.valStatus!!)
                )
            }
            LabelItem.TYPE_DATA -> {
                h.contents.setTextColor(ContextCompat.getColor(context,R.color.mass_text_grey_wzc))
                h.contents.text = "${d.valData} "+d.valUnit
            }
        }

        // 设置备注，如果有的话
        if (d.hint != null) {
            h.labelHint.text = "备注：" + d.hint
            h.labelHint.visibility = View.VISIBLE
        } else {
            h.labelHint.visibility = View.GONE
        }

        when(d.status){
            LabelItem.MODE_DISPLAY -> {
                h.contents.visibility = View.VISIBLE
                h.deleteButton.visibility = View.GONE
            }
            LabelItem.MODE_DEL -> {
                h.contents.visibility = View.GONE
                h.deleteButton.visibility = View.VISIBLE
            }
        }

        h.deleteButton.setOnClickListener {
            itemCallback(d)
            labelList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(0,labelList.size)
        }
    }

    override fun getItemCount(): Int {
        return labelList.size
    }

    private fun getStatusText(t:Int) = when(t){
        1 -> "好"
        2 -> "较好"
        3 -> "一般"
        4 -> "较差"
        5 -> "差"
        else -> "错误"
    }

    private fun getStatusColor(t:Int) :Int {
        var color = when(t){
            1 -> R.color.themeDarkGreen
            2 -> R.color.status_2_wzc
            3 -> R.color.status_3_wzc
            4 -> R.color.status_4_wzc
            5 -> R.color.status_5_wzc
            else -> R.color.black
        }
        return ContextCompat.getColor(context,color)
    }

}