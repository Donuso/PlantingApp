package com.example.plantingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.R
import com.example.plantingapp.item.LabelItem
import com.google.android.material.card.MaterialCardView

class LabelAdapter(
    var labelList: MutableList<LabelItem>,
    private val context: Context,
    private val itemCallback: (LabelItem) -> Unit)
: RecyclerView.Adapter<LabelAdapter.LabelViewHolder>()
{
    inner class LabelViewHolder(v: View): RecyclerView.ViewHolder(v){
        val icon:ImageView = v.findViewById(R.id.label_icon)
        val name:TextView = v.findViewById(R.id.label_text)
        val delLayer:ImageView = v.findViewById(R.id.delete_layer)
        val actionLayer:View = v.findViewById(R.id.action_layer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return LabelViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.component_label_wzc, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return labelList.size
    }

    override fun onBindViewHolder(v: LabelViewHolder, pos: Int) {
        val d = labelList[pos]
        v.icon.setImageResource(d.tagIcon)
        v.name.text = d.tagName
        v.delLayer.setOnClickListener{
            labelList.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(0,labelList.size)
            // 数据库操作预留位
        }
        v.actionLayer.setOnClickListener{
            when(d.tagType){
                LabelItem.TYPE_STATUS -> {
                    showStatusDialog(d,pos)
                }
                LabelItem.TYPE_DATA -> {
                    showDataDialog(d,pos)
                }
            }
        }
        when(d.status){
            LabelItem.MODE_DEL -> {
                v.delLayer.visibility = View.VISIBLE
            }
            LabelItem.MODE_DISPLAY -> {
                v.delLayer.visibility = View.GONE
            }
        }
    }

    private fun showStatusDialog(d: LabelItem, pos: Int) {
        var lastChosen = -1
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_status_label_wzc, null)

        // 获取带 id 的控件引用
        val labelTitle: TextView = dialogView.findViewById(R.id.label_title)
        val extraInput: EditText = dialogView.findViewById(R.id.extra_input)
        val cancel: TextView = dialogView.findViewById(R.id.cancel)
        val makeSure: TextView = dialogView.findViewById(R.id.make_sure)

        // 状态选择按钮
        val status1Bg: MaterialCardView = dialogView.findViewById(R.id.status_1_bg)
        val status1Text: TextView = dialogView.findViewById(R.id.status_1_text)
        val status2Bg: MaterialCardView = dialogView.findViewById(R.id.status_2_bg)
        val status2Text: TextView = dialogView.findViewById(R.id.status_2_text)
        val status3Bg: MaterialCardView = dialogView.findViewById(R.id.status_3_bg)
        val status3Text: TextView = dialogView.findViewById(R.id.status_3_text)
        val status4Bg: MaterialCardView = dialogView.findViewById(R.id.status_4_bg)
        val status4Text: TextView = dialogView.findViewById(R.id.status_4_text)
        val status5Bg: MaterialCardView = dialogView.findViewById(R.id.status_5_bg)
        val status5Text: TextView = dialogView.findViewById(R.id.status_5_text)

        val texts = listOf(
            status1Text,status2Text,status3Text,status4Text,status5Text
        )
        val bgs = listOf(
            status1Bg,status2Bg,status3Bg,status4Bg,status5Bg
        )

        // 设置标题（可选）
        labelTitle.text = d.tagName

        // 创建并显示对话框
        val dialog = AlertDialog.Builder(context, R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        status1Text.setOnClickListener {
            d.valStatus = 1
            texts[0].setTextColor(
                getStatusColor(6)
            )
            bgs[0].setCardBackgroundColor(
                getStatusColor(0)
            )
            if(lastChosen != -1){
                texts[lastChosen].setTextColor(
                    getStatusColor(lastChosen)
                )
                bgs[lastChosen].setCardBackgroundColor(
                    getStatusColor(6)
                )
            }
            lastChosen = 0
        }

        status2Text.setOnClickListener {
            // 处理状态 2 的选择逻辑
            d.valStatus = 2
            texts[1].setTextColor(
                getStatusColor(6)
            )
            bgs[1].setCardBackgroundColor(
                getStatusColor(1)
            )
            if(lastChosen != -1){
                texts[lastChosen].setTextColor(
                    getStatusColor(lastChosen)
                )
                bgs[lastChosen].setCardBackgroundColor(
                    getStatusColor(6)
                )
            }
            lastChosen = 1
        }

        status3Text.setOnClickListener {
            // 处理状态 3 的选择逻辑
            d.valStatus = 3
            texts[2].setTextColor(
                getStatusColor(6)
            )
            bgs[2].setCardBackgroundColor(
                getStatusColor(2)
            )
            if(lastChosen != -1){
                texts[lastChosen].setTextColor(
                    getStatusColor(lastChosen)
                )
                bgs[lastChosen].setCardBackgroundColor(
                    getStatusColor(6)
                )
            }
            lastChosen = 2
        }

        status4Text.setOnClickListener {
            // 处理状态 4 的选择逻辑
            d.valStatus = 4
            texts[3].setTextColor(
                getStatusColor(6)
            )
            bgs[3].setCardBackgroundColor(
                getStatusColor(3)
            )
            if(lastChosen != -1){
                texts[lastChosen].setTextColor(
                    getStatusColor(lastChosen)
                )
                bgs[lastChosen].setCardBackgroundColor(
                    getStatusColor(6)
                )
            }
            lastChosen = 3
        }

        status5Text.setOnClickListener {
            // 处理状态 5 的选择逻辑
            d.valStatus = 5
            texts[4].setTextColor(
                getStatusColor(6)
            )
            bgs[4].setCardBackgroundColor(
                getStatusColor(4)
            )
            if(lastChosen != -1){
                texts[lastChosen].setTextColor(
                    getStatusColor(lastChosen)
                )
                bgs[lastChosen].setCardBackgroundColor(
                    getStatusColor(6)
                )
            }
            lastChosen = 4
        }


        // 设置取消按钮的点击监听器
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        // 设置确认按钮的点击监听器
        makeSure.setOnClickListener {
            if(d.valStatus == null){
                Toast.makeText(context,"尚未选择状态",Toast.LENGTH_SHORT).show()
            }else if(extraInput.text.toString().length > 15){
                Toast.makeText(context,"额外描述超出字数限制",Toast.LENGTH_SHORT).show()
            }else{
                itemCallback(d.apply {
                    if(extraInput.text.isNotEmpty()){
                        this.hint = extraInput.text.toString()
                    }
                })
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDataDialog(d:LabelItem,pos:Int){
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_new_data_label_wzc, null)
        val title = v.findViewById<TextView>(R.id.label_title)
        val data = v.findViewById<EditText>(R.id.unit_input)
        val unit = v.findViewById<TextView>(R.id.unit_name)
        val hint = v.findViewById<EditText>(R.id.extra_input)
        val cancel = v.findViewById<TextView>(R.id.cancel)
        val confirm = v.findViewById<TextView>(R.id.make_sure)

        val dialog = AlertDialog.Builder(context, R.style.CustomDialogTheme)
            .setView(v)
            .create()

        title.text = d.tagName
        unit.text = d.valUnit
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        confirm.setOnClickListener {
            if (data.text.toString().isEmpty()) {
                Toast.makeText(context, "您尚未输入数值内容", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    // 尝试将输入转换为 Double 类型
                    val inputValue = data.text.toString().toDouble()

                    // 检查额外描述的长度
                    if (hint.text.toString().length > 15) {
                        Toast.makeText(context, "额外描述长度超出字数限制", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        // 如果转换成功且额外描述验证通过，则更新 LabelItem 对象
                        if (hint.text.toString().isNotEmpty()) {
                            d.hint = hint.text.toString()
                        }
                        d.valData = inputValue // 正确赋值给 d.valData
                        itemCallback(d)
                        dialog.dismiss()
                    }
                } catch (e: NumberFormatException) {
                    // 捕获转换异常并显示 Toast
                    Toast.makeText(context, "输入的数据类型应为数字", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun getStatusColor(t:Int) :Int {
        var color = when(t){
            0 -> R.color.themeDarkGreen
            1 -> R.color.status_2_wzc
            2 -> R.color.status_3_wzc
            3 -> R.color.status_4_wzc
            4 -> R.color.status_5_wzc
            else -> R.color.white
        }
        return ContextCompat.getColor(context,color)
    }

}