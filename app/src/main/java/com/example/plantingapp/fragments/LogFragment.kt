package com.example.plantingapp.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.SearchActivity
import com.example.plantingapp.R
import com.example.plantingapp.adapter.LogGroupAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.animators.FadeAnimator
import com.example.plantingapp.dao.LogGroupDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LogGroupItem
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
* 此fragment对应“日志”的入口
* */

class LogFragment : Fragment() {

    private lateinit var groupView : RecyclerView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var groups: MutableList<LogGroupItem>
    private lateinit var menu: ImageButton
    private lateinit var addGroup: ImageView
    private lateinit var search:View
    private lateinit var options: LinearLayout
    private lateinit var optionDel:TextView
    private lateinit var optionAlt:TextView
    private lateinit var addBG:CardView
    private lateinit var hint:TextView
    private lateinit var title:TextView
    private lateinit var searchModule:CardView
    private lateinit var dao:LogGroupDAO

    private var bottomNavigation:BottomNavigationView? = null
    private var cancelModule:CardView? = null
    private var cancel:TextView? = null

    private lateinit var menuAnimator: FadeAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var cancelBackAnimator: ExpandAnimator
    private lateinit var optionAnimator: FadeAnimator
//    private lateinit var searchAnimator: FadeAnimator
//    private lateinit var hintAnimator: FadeAnimator

    private lateinit var groupAdapter: LogGroupAdapter

    private val cancelMovement = 100f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables(view)
        addOnListenersAndAnimators()
    }

    override fun onResume() {
        super.onResume()
        preData()
        groupAdapter = LogGroupAdapter(requireContext(),groups){ d,opt ->
            when(opt){
                LogGroupDAO.OPT_ALT -> {
                    try{
                        dao.updateLogGroup(d)
                        with(requireContext().getSharedPreferences("${DataExchange.USERID}_prefs", Context.MODE_PRIVATE).edit()){
                            putString("group_name",d.gpName)
                            apply()
                        }
                    }catch (e:IllegalArgumentException){
                        Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                    }
                }
                LogGroupDAO.OPT_DEL -> {
                    dao.deleteLogGroup(d)
                    with(requireContext().getSharedPreferences("${DataExchange.USERID}_prefs", Context.MODE_PRIVATE).edit()){
                        putString("group_name",null)
                        putInt("group_id",-1)
                        putLong("group_created_time",0L)
                        apply()
                    }
                }
            }
        }
        groupView.layoutManager = viewManager
        groupView.adapter = groupAdapter
    }

    private fun initVariables(v: View){
        groupView = v.findViewById(R.id.log_group_list)
        menu = v.findViewById(R.id.menu_btn)
        addGroup = v.findViewById(R.id.add_log_group)
        search = v.findViewById(R.id.search_touch_area)
        addBG = v.findViewById(R.id.add_log_group_display)
        options = v.findViewById(R.id.log_group_options)
        optionDel = v.findViewById(R.id.delete_log_group)
        optionAlt = v.findViewById(R.id.edit_log_group)
        hint = v.findViewById(R.id.log_group_alter_text)
        title = v.findViewById(R.id.title_log)
        searchModule = v.findViewById(R.id.search_area)

        bottomNavigation = activity?.findViewById(R.id.bottomNavigationView)
        cancel = activity?.findViewById(R.id.cancel_text)
        cancelModule = activity?.findViewById(R.id.cancel_module)
        dao = LogGroupDAO(requireContext())

    }

    private fun addOnListenersAndAnimators(){
        menuAnimator = FadeAnimator(menu)
            .setDuration(200)
        cancelAnimator = ExpandAnimator(requireContext(),cancelModule!!)
            .setMoveDirection(2,-cancelMovement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
        optionAnimator = FadeAnimator(options)
            .setDuration(100)
        cancelBackAnimator = ExpandAnimator(requireContext(),cancelModule!!)
            .setMoveDirection(2,cancelMovement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
//        searchAnimator = FadeAnimator(searchModule)
//            .setDuration(150)
//        hintAnimator = FadeAnimator(hint)
//            .setDuration(150)

        menu.setOnClickListener {
            optionAnimator.start(true)
        }

        addGroup.setOnClickListener {
            // 创建 Dialog 实例
            val dialog = Dialog(requireContext(),R.style.CustomDialogTheme)
            dialog.setContentView(R.layout.dialog_new_log_group_wzc) // 设置自定义布局

            // 获取对话框中的视图
            val newNameInput = dialog.findViewById<EditText>(R.id.new_name_input)
            val newNoteInput = dialog.findViewById<EditText>(R.id.new_note_input)
            val cancelButton = dialog.findViewById<TextView>(R.id.cancel)
            val makeSureButton = dialog.findViewById<TextView>(R.id.make_sure)

            // 设置取消按钮点击事件
            cancelButton.setOnClickListener {
                dialog.dismiss() // 关闭对话框
            }

            // 设置确定按钮点击事件
            makeSureButton.setOnClickListener {
                val name = newNameInput.text.toString()
                val note = newNoteInput.text.toString()
                if(name.isEmpty()) {
                    Toast.makeText(requireContext(), "分组名不能为空", Toast.LENGTH_SHORT).show()
                }else if (name.length > 10){
                    Toast.makeText(requireContext(), "名称超出10个字符", Toast.LENGTH_SHORT).show()
                }else if( note.length > 15) {
                    // 弹出 Toast 提示
                    Toast.makeText(requireContext(), "备注超出15个字符", Toast.LENGTH_SHORT).show()
                } else {
                    // 在这里处理获取到的内容，例如保存到数据库或更新 UI
                    try{
                        var hint:String? = null
                        if(note.isNotEmpty()){
                            hint = note
                        }
                        val it = packageGroup(name,hint)
                        val newGPID = dao.insertLogGroup(it)
                        groups.add(it.apply {
                            gpId = newGPID.toInt()
                        })
                        groupAdapter.notifyItemInserted(groups.size-1)
                        groupAdapter.notifyItemRangeChanged(0,groups.size)
                        dialog.dismiss() // 关闭对话框
                    } catch (e:IllegalArgumentException){
                        Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 显示对话框
            dialog.show()
        }

        optionDel.setOnClickListener{
            if(groups.size == 0){
                optionAnimator.start(false)
                Toast.makeText(requireContext(),"您尚未添加任何日志组",Toast.LENGTH_SHORT).show()
            }else{
                addBG.visibility = View.GONE
                bottomNavigation?.visibility = View.GONE
                searchModule.visibility = View.GONE
//            searchAnimator.start(false)
                cancel?.setTextColor(ContextCompat.getColor(requireContext(),R.color.themeRed))
                optionAnimator.start(false)
                menuAnimator.start(false)
                title.setTextColor(ContextCompat.getColor(requireContext(),R.color.white0_5_zhj))
                cancelAnimator.start()
                hint.text = getString(R.string.log_group_delete)
                hint.visibility = View.VISIBLE
//            hintAnimator.start(true)
                switchToDel()
            }
        }

        optionAlt.setOnClickListener {
            if(groups.size == 0){
                optionAnimator.start(false)
                Toast.makeText(requireContext(),"您尚未添加任何日志组",Toast.LENGTH_SHORT).show()
            }else {
                addBG.visibility = View.GONE
                bottomNavigation?.visibility = View.GONE
                searchModule.visibility = View.GONE
                cancel?.setTextColor(ContextCompat.getColor(requireContext(),R.color.themeBlue))
                optionAnimator.start(false)
                menuAnimator.start(false)
                title.setTextColor(ContextCompat.getColor(requireContext(),R.color.white0_5_zhj))
                cancelAnimator.start()
                hint.text = getString(R.string.log_group_alter)
                hint.visibility = View.VISIBLE
                switchToEdit()
            }
        }

        options.setOnClickListener {
            optionAnimator.start(false)
        }

        cancel?.setOnClickListener {
            hint.visibility = View.GONE
            searchModule.visibility = View.VISIBLE
            cancelBackAnimator.start()
            menuAnimator.start(true)
            title.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            bottomNavigation?.visibility = View.VISIBLE
            addBG.visibility = View.VISIBLE
            backToNormal()
        }

        search.setOnClickListener{
            requireContext().startActivity(
                Intent(activity,SearchActivity::class.java)
            )
        }
    }

    private fun switchToEdit(){
        for(d in groups){
            d.status = LogGroupItem.MODE_EDIT
        }
        groupAdapter.notifyDataSetChanged()
    }

    private fun switchToDel(){
        for(d in groups){
            d.status = LogGroupItem.MODE_DEL
        }
        groupAdapter.notifyDataSetChanged()
    }

    private fun backToNormal(){
        for(d in groups){
            d.status = LogGroupItem.MODE_OPEN
        }
        groupAdapter.notifyDataSetChanged()
    }

    private fun preData(){
        groups = dao.getAllLogGroups()
    }

    private fun packageGroup(name:String,hint:String?,gpId:Int = 0) : LogGroupItem{
        return LogGroupItem(
                gpId = gpId,
                gpName = name,
                createTime = System.currentTimeMillis(),
                lastModified = -1L,
                coverUri = null,
                hint = hint,
                coverType = LogGroupItem.RES_COVER,
                coverRes = R.drawable.icon_main
            )
    }

}