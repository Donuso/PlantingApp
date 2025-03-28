package com.example.plantingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.LogActivity
import com.example.plantingapp.R
import com.example.plantingapp.Utils
import com.example.plantingapp.dao.LogDAO
import com.example.plantingapp.dao.LogGroupDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LogGroupItem


class LogGroupAdapter(
    private val context: Context,
    var groups: MutableList<LogGroupItem>,
    private val itemCallback: (LogGroupItem,Int) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class LogGroup(v: View):RecyclerView.ViewHolder(v){
        val plantPhoto: ImageView = v.findViewById(R.id.plantPhoto)
        val plantName: TextView = v.findViewById(R.id.plantName)
        val groupDays: TextView = v.findViewById(R.id.group_days)
        val groupLastModifiedTime: TextView = v.findViewById(R.id.group_last_modified_time)
        val logGroupHint: TextView = v.findViewById(R.id.log_group_hint)
        val groupEnterButton: ImageButton = v.findViewById(R.id.group_enter_button)

        // 请在后期添加cover的变化模式
        fun basicEdit(d:LogGroupItem,pos:Int){
            plantName.text = d.gpName
            if(d.hint == null){
                logGroupHint.visibility = View.GONE
                logGroupHint.text = ""
            }else{
                if(d.hint!!.isNotEmpty()){
                    logGroupHint.visibility = View.VISIBLE
                    logGroupHint.text = "备注：" + d.hint
                }else{
                    logGroupHint.visibility = View.GONE
                    logGroupHint.text = ""
                }
            }
            when(d.status){
                LogGroupItem.MODE_OPEN -> {
                    with(context.getSharedPreferences("${DataExchange.USERID}_prefs",Context.MODE_PRIVATE).edit()){
                        putInt("group_id",d.gpId)
                        putLong("group_created_time",d.createTime)
                        putString("group_name",d.gpName)
                        apply()
                    }
                    LogDAO(context).updateLastModified(d.gpId)
                    groupEnterButton.setImageResource(R.drawable.icon_activator_wzc)
                    groupEnterButton.setOnClickListener {
                        context.startActivity(
                            Intent(context,LogActivity::class.java)
                                .putExtra("log_group_id",d.gpId)
                                .putExtra("log_group_name",d.gpName)
                        )
                    }
                }
                LogGroupItem.MODE_EDIT -> {
                    groupEnterButton.setImageResource(R.drawable.icon_edit_wzc)
                    groupEnterButton.setOnClickListener {
                        showEditDialog(d, pos)
                    }
                }
                LogGroupItem.MODE_DEL -> {
                    groupEnterButton.setImageResource(R.drawable.icon_delete_wzc)
                    groupEnterButton.setOnClickListener {
                        showDeleteDialog(d, pos)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder{
        return LogGroup(
            LayoutInflater.from(parent.context).inflate(R.layout.item_log_group_wzc,parent,false)
        )
    }

    override fun onBindViewHolder(v: RecyclerView.ViewHolder, pos: Int){
        val d = groups[pos]
        when(v){
            is LogGroup -> {
                v.basicEdit(d,pos)
                val days = Utils.daysBetweenNowAndTimestamp(d.createTime).toInt()
                if(days <= 0){
                    v.groupDays.text = "今日开始种植"
                }else{
                    v.groupDays.text = "已种植${days}天"
                }
                if(d.lastModified == -1L || d.lastModified == null){
                    v.groupLastModifiedTime.text = "最近未查看"
                }else{
                    val days = Utils.daysBetweenNowAndTimestamp(d.lastModified!!)
                    if(days == 0L){
                        v.groupLastModifiedTime.text = "今日内查看"
                    }else{
                        v.groupLastModifiedTime.text = "${days}天前查看"
                    }
                }
                when(d.coverType){
                    LogGroupItem.RES_COVER -> {
                        v.plantPhoto.setImageResource(d.coverRes)
                    }
                    LogGroupItem.URI_COVER -> {
                        //等待更新后的补全
                    }
                }
            }
        }
    }

    override fun getItemCount()=groups.size

    private fun showEditDialog(item: LogGroupItem, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_log_group_edit_wzc, null)
        val alterNameInput: EditText = dialogView.findViewById(R.id.alter_name_input)
        val alterNoteInput: EditText = dialogView.findViewById(R.id.alter_note_input)
        val cancelButton: TextView = dialogView.findViewById(R.id.cancel)
        val makeSureButton: TextView = dialogView.findViewById(R.id.make_sure)

        // 设置初始值
        alterNameInput.setText(item.gpName)
        alterNoteInput.setText(item.hint ?: "")

        val dialog = AlertDialog.Builder(context,R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        makeSureButton.setOnClickListener {
            val newName = alterNameInput.text.toString()
            val newHint = alterNoteInput.text.toString()

            if(newName.isEmpty()) {
                Toast.makeText(context, "分组名不能为空", Toast.LENGTH_SHORT).show()
            }else if (newName.length > 10){
                Toast.makeText(context, "名称超出10个字符", Toast.LENGTH_SHORT).show()
            }else if( newHint.length > 15) {
                // 弹出 Toast 提示
                Toast.makeText(context, "备注超出15个字符", Toast.LENGTH_SHORT).show()
            }else {
                item.gpName = newName
                item.hint = newHint.ifEmpty { null }
                item.lastModified = System.currentTimeMillis()
                dialog.dismiss()
                itemCallback(groups[position],LogGroupDAO.OPT_ALT)
                notifyItemChanged(position)
                notifyItemRangeChanged(0,groups.size)
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(item: LogGroupItem, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_log_group_delete_wzc, null)
        val deleteText: TextView = dialogView.findViewById(R.id.delete_text)
        val cancelButton: TextView = dialogView.findViewById(R.id.cancel)
        val makeSureButton: TextView = dialogView.findViewById(R.id.make_sure)

        deleteText.text = "您真的要删除 ${item.gpName} 分组吗？"

        val dialog = AlertDialog.Builder(context,R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        makeSureButton.setOnClickListener {
            dialog.dismiss()
            itemCallback(groups[position],LogGroupDAO.OPT_DEL)
            groups.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(0,groups.size)
        }

        dialog.show()
    }

}