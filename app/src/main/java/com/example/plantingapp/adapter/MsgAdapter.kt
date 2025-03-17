package com.example.plantingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.MsgItem

class MsgAdapter(val msgItemList:List<MsgItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class LeftViewHolder(view:View):RecyclerView.ViewHolder(view){
        val leftMsg:TextView=view.findViewById(R.id.leftmsg)
    }
    inner class RightViewHolder(view:View):RecyclerView.ViewHolder(view){
        val rightMsg:TextView=view.findViewById(R.id.rightmsg)
    }

    override fun getItemViewType(position: Int): Int {
        val msg=msgItemList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=if(viewType== MsgItem.TYPE_RECEIVED) {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.msg_left_item,parent,false)
        LeftViewHolder(view)
    }else{
        val view =LayoutInflater.from(parent.context).inflate(R.layout.msg_right_item,parent,false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg=msgItemList[position]
        when(holder){
            is LeftViewHolder ->holder.leftMsg.text=msg.content
            is RightViewHolder ->holder.rightMsg.text=msg.content
            else->throw IllegalArgumentException()
        }
    }

    override fun getItemCount()=msgItemList.size
}