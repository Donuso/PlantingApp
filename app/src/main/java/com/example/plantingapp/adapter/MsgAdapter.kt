package com.example.plantingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.animators.SpinAnimator
import com.example.plantingapp.item.MsgItem

class MsgAdapter(val msgItemList:List<MsgItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class LeftViewHolder(view:View):RecyclerView.ViewHolder(view){
        val leftMsg:TextView= view.findViewById(R.id.response)
        val finFlag:ImageView = view.findViewById(R.id.finish_flag)
//        private val spinAnimator: SpinAnimator = SpinAnimator(view.findViewById<ImageView>(R.id.loading_flag))
//            .setDuration(1000)
        private val fadeAnimator: FadeAnimator = FadeAnimator(view.findViewById<ImageView>(R.id.loading_flag))

        fun alterStatus(item: MsgItem){
            when(item.status){
                MsgItem.STATUS_START_LOADING -> {
//                    spinAnimator.start()
                }
                MsgItem.STATUS_LOAD_SUCCESSFULLY -> {
                    fadeAnimator.start(false)
//                    spinAnimator.stop()
                }
                MsgItem.STATUS_LOAD_FAILED -> {
                    finFlag.setImageResource(R.drawable.icon_loading_failed_chl)
                    fadeAnimator.start(false)
//                    spinAnimator.stop()
                }
            }
        }
    }
    inner class RightViewHolder(view:View):RecyclerView.ViewHolder(view){
        val rightMsg:TextView=view.findViewById(R.id.ask)
    }

    override fun getItemViewType(position: Int): Int {
        val msg=msgItemList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=if(viewType== MsgItem.TYPE_RECEIVED) {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_msg_left,parent,false)
        LeftViewHolder(view)
    }else{
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_msg_right,parent,false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg=msgItemList[position]
        when(holder){
            is LeftViewHolder -> {
                holder.leftMsg.text=msg.content
                holder.alterStatus(msgItemList[position])
            }
            is RightViewHolder ->holder.rightMsg.text=msg.content
            else->throw IllegalArgumentException()
        }
    }

    override fun getItemCount()=msgItemList.size

    fun stopAnimator(pos: Int, status: Int, content:String){
        msgItemList[pos].status = status
        msgItemList[pos].content = content
        notifyItemChanged(pos)
    }
    fun updateReceivedMessage(position: Int, newContent: String) {
        if (position < msgItemList.size) {
            val msgItem = msgItemList[position]
            msgItem.content = newContent
            notifyItemChanged(position)
        }
    }

//    fun updateReceivedMessage(position: Int, newContent: String) {
//        if (position in 0 until itemCount) {
//            val newItem = msgItemList[position].copy(content = newContent)
//            notifyItemChanged(position, newItem)
//        }
//    }
//
//    // 修改后的stopAnimator方法
//    fun stopAnimator(position: Int, status: Int, finalContent: String) {
//        if (position in 0 until itemCount) {
//            val item = msgItemList[position]
//            val updatedItem = item.copy(
//                status = status,
//                content = if (status == MsgItem.STATUS_LOAD_SUCCESSFULLY) finalContent else item.content
//            )
//            notifyItemChanged(position, updatedItem)
//        }
//    }
}