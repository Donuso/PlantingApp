package com.example.plantingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.item.DayItem
import com.example.plantingapp.R

class DayAdapter(
    var dayList: MutableList<DayItem>,
    private val context: Context
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    var lastChosen = -1

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayBg = itemView.findViewById<CardView>(R.id.day_bg)
        val dayTimeText = itemView.findViewById<TextView>(R.id.day_time_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_wzc, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayItem = dayList[position]

        // 设置文本
        holder.dayTimeText.text = dayItem.day.toString()

        // 根据状态设置样式
        when (dayItem.status) {
            DayItem.STATUS_CHOSEN -> {
                lastChosen = position
                setBg(
                    holder,
                    ContextCompat.getColor(context, R.color.themeDarkGreen),
                    5f,
                    ContextCompat.getColor(context, R.color.white)
                )
            }
            DayItem.STATUS_HAS_RECORD -> {
                setBg(
                    holder,
                    ContextCompat.getColor(context, R.color.white),
                    0f,
                    ContextCompat.getColor(context, R.color.mass_text_grey_wzc)
                )
            }
            DayItem.STATUS_NO_RECORD -> {
                setBg(
                    holder,
                    ContextCompat.getColor(context, R.color.white),
                    0f,
                    ContextCompat.getColor(context, R.color.general_grey_wzc)
                )
            }
        }

        holder.dayTimeText.setOnClickListener{
            if(lastChosen == -1){
                dayItem.status = DayItem.STATUS_CHOSEN
                lastChosen = position
                notifyItemChanged(position)
            }else if(lastChosen != position){
                dayList[lastChosen].status = when(dayList[lastChosen].hasRecord){
                    true -> DayItem.STATUS_HAS_RECORD
                    false -> DayItem.STATUS_NO_RECORD
                }
                dayItem.status = DayItem.STATUS_CHOSEN
                notifyItemChanged(lastChosen)
                notifyItemChanged(position)
                lastChosen = position
            }
        }
    }

    override fun getItemCount() = dayList.size

    private fun setBg(v:DayViewHolder,bgColor:Int,elevation:Float,txtColor:Int){
        v.dayBg.apply {
            setCardBackgroundColor(
                bgColor
            )
            cardElevation = elevation
        }
        v.dayTimeText.setTextColor(
            txtColor
        )
    }
}