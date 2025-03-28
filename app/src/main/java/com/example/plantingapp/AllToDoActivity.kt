package com.example.plantingapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.AllTodoAdapter
import com.example.plantingapp.animators.ExpandAnimator
import com.example.plantingapp.dao.ToDoDAO
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.TodoItem
import com.example.plantingapp.item.UniversalTodoItem
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AllToDoActivity : BaseActivity() {
    private lateinit var enabledCardViewLayout: RecyclerView
    private lateinit var disabledCardViewLayout: RecyclerView

    companion object {
        private const val REQUEST_CODE_NEW_TODO = 1
        const val RESULT_TODO_UPDATED = 2
    }

    private lateinit var optionLayer: LinearLayout
    private lateinit var cancel: TextView
    private lateinit var cancelModule: CardView
    private lateinit var addTodoModule: CardView
    private lateinit var addTodo: ImageView
    private lateinit var deleteTodo: TextView
    private lateinit var alterTodo: TextView
    private lateinit var menu: ImageButton
    private lateinit var hint: TextView
    private lateinit var optionAnimator: ExpandAnimator
    private lateinit var cancelAnimator: ExpandAnimator
    private lateinit var addModuleAnimator: ExpandAnimator
    private lateinit var menuAnimator: ExpandAnimator
    private val cancelDisplacement = 120f
    private val enabledTodoList = mutableListOf<TodoItem>()
    private val disabledTodoList = mutableListOf<TodoItem>()
    private var isDeleting = false
    private var isAltering = false
    private lateinit var dbHelper: DBHelper
    private lateinit var enabledTodo: MutableList<UniversalTodoItem>
    private lateinit var disabledTodo: MutableList<UniversalTodoItem>
    private lateinit var noEnabledHint:TextView
    private lateinit var noDisabledHint:TextView
    private lateinit var dao:ToDoDAO
    private lateinit var enabledAdapter:AllTodoAdapter
    private lateinit var disabledAdapter:AllTodoAdapter
    private lateinit var allGroup:MutableMap<Int,String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_to_do)
//        dbHelper = DBHelper(this)
        initAll()
        addOnListeners()

//        loadTodosFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        val allTodo = dao.getTodosByUserId(DataExchange.USERID!!.toInt())
        allGroup = dao.getLogGroupsByUserIdWithMap(DataExchange.USERID!!.toInt())
        enabledTodo = dao.filterTodosByStatus(allTodo,UniversalTodoItem.STATUS_RUNNING)
        disabledTodo = dao.filterTodosByStatus(allTodo,UniversalTodoItem.STATUS_DISABLED)
        for(it in enabledTodo){
            if(it.logGroupId!=null){
                it.attachedGroupName = allGroup[it.logGroupId]
            }
        }
        for(it in disabledTodo){
            if(it.logGroupId!=null){
                it.attachedGroupName = allGroup[it.logGroupId]
            }
        }
        enabledAdapter = AllTodoAdapter(this,enabledTodo){d,mode ->
            when(mode){
                AllTodoAdapter.OPTION_REVERSE -> {
                    d.isEnabled = UniversalTodoItem.STATUS_DISABLED
                    disabledAdapter.dataList.add(d)
                    disabledAdapter.notifyItemInserted(
                        enabledAdapter.dataList.size - 1
                    )
                    disabledAdapter.notifyItemRangeChanged(0,disabledAdapter.dataList.size)
                    dao.updateTodo(d)
                    Toast.makeText(this,"已停用“${d.todoName}”待办",Toast.LENGTH_SHORT).show()
                    refreshListDisplay()
                }
                AllTodoAdapter.OPTION_DEL -> {
                    dao.deleteTodo(d.todoId)
                    Toast.makeText(this,"已删除“${d.todoName}”待办",Toast.LENGTH_SHORT).show()
                    refreshListDisplay()
                }
            }
        }
        disabledAdapter = AllTodoAdapter(this,disabledTodo){d,mode ->
            when(mode){
                AllTodoAdapter.OPTION_REVERSE -> {
                    d.isEnabled = UniversalTodoItem.STATUS_RUNNING
                    enabledAdapter.dataList.add(d)
                    enabledAdapter.notifyItemInserted(
                        enabledAdapter.dataList.size - 1
                    )
                    enabledAdapter.notifyItemRangeChanged(0,enabledAdapter.dataList.size)
                    dao.updateTodo(d)
                    refreshListDisplay()
                    Toast.makeText(this,"已复用“${d.todoName}”待办",Toast.LENGTH_SHORT).show()
                }
                AllTodoAdapter.OPTION_DEL -> {
                    dao.deleteTodo(d.todoId)
                    Toast.makeText(this,"已删除“${d.todoName}”待办",Toast.LENGTH_SHORT).show()
                    refreshListDisplay()
                }
            }
        }
        enabledCardViewLayout.adapter = enabledAdapter
        disabledCardViewLayout.adapter = disabledAdapter
        refreshListDisplay()
    }

    private fun initAll() {
        optionLayer = findViewById(R.id.options_layer)
        cancel = findViewById(R.id.cancel_text)
        cancelModule = findViewById(R.id.cancel_module)
        addTodoModule = findViewById(R.id.add_todo_module)
        addTodo = findViewById(R.id.new_todo)
        deleteTodo = findViewById(R.id.delete_todo)
        alterTodo = findViewById(R.id.alter_todos)
        menu = findViewById(R.id.menu)
        hint = findViewById(R.id.hint_subtitle)
        enabledCardViewLayout = findViewById(R.id.enabled_todos)
        disabledCardViewLayout = findViewById(R.id.invalid_todos)
        enabledCardViewLayout.layoutManager = LinearLayoutManager(this)
        disabledCardViewLayout.layoutManager = LinearLayoutManager(this)
        noEnabledHint = findViewById(R.id.no_enabled_todos)
        noDisabledHint = findViewById(R.id.no_invalid_todos)
        dao = ToDoDAO(this)

        optionAnimator = ExpandAnimator(this, optionLayer)
            .setIfFade(true)
            .setDuration(100)
        cancelAnimator = ExpandAnimator(this, cancelModule)
            .setDuration(500)
        addModuleAnimator = ExpandAnimator(this, addTodoModule)
            .setIfFade(true)
            .setDuration(200)
        menuAnimator = ExpandAnimator(this, menu)
            .setIfFade(true)
            .setDuration(200)
    }

    private fun showMenu() {
        optionAnimator.setFade(0f, 1f).start()
    }

    private fun hideMenu() {
        optionAnimator.setFade(1f, 0f).start()
    }

    private fun showCancel() {
        cancelAnimator.setMoveDirection(2, -cancelDisplacement)
            .setRateType(ExpandAnimator.iOSRatio)
            .setDuration(500)
            .start()
        addModuleAnimator.setFade(1f, 0f).start()
        menuAnimator.setFade(1f, 0f).start()
    }

    private fun hideCancel() {
        hint.text = getString(R.string.hint_subtitle)
        hint.setTextColor(ContextCompat.getColor(this, R.color.general_grey_wzc))
        cancelAnimator.setMoveDirection(2, cancelDisplacement)
            .setRateType(ExpandAnimator.linearRatio)
            .setDuration(150)
            .start()
        addModuleAnimator.setFade(0f, 1f).start()
        menuAnimator.setFade(0f, 1f).start()
    }

    private fun addOnListeners() {
        findViewById<ImageButton>(R.id.backtoDo).setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        menu.setOnClickListener { showMenu() }
        optionLayer.setOnClickListener { hideMenu() }

        cancel.setOnClickListener {
            hideCancel()
            modeSwitch(UniversalTodoItem.DISPLAY_NORMAL)
            findViewById<ScrollView>(R.id.scroll).setPadding(
                0,
                0,
                0,
                0
            )
        }

        deleteTodo.setOnClickListener {
            hint.text = "点击以删除待办"
            hint.setTextColor(ContextCompat.getColor(this, R.color.themeRed))
            hideMenu()
            modeSwitch(UniversalTodoItem.DISPLAY_DEL)
            cancel.setTextColor(ContextCompat.getColor(this, R.color.themeRed))
            showCancel()
            findViewById<ScrollView>(R.id.scroll).setPadding(
                0,
                0,
                0,
                Utils.dpToPx(this,100f)
            )
        }


        alterTodo.setOnClickListener {
            hint.text = "点击按钮以停用/复用待办"
            hint.setTextColor(ContextCompat.getColor(this, R.color.themeBlue))
            hideMenu()
            modeSwitch(UniversalTodoItem.DISPLAY_ALTER)
            cancel.setTextColor(ContextCompat.getColor(this, R.color.themeBlue))
            showCancel()
            findViewById<ScrollView>(R.id.scroll).setPadding(
                0,
                0,
                0,
                Utils.dpToPx(this,100f)
            )
        }

        findViewById<ImageView>(R.id.new_todo).setOnClickListener {
            startActivity(Intent(this, NewTodoActivity::class.java))
        }
    }

    private fun modeSwitch(mode:Int){
        when(mode){
            UniversalTodoItem.DISPLAY_NORMAL -> {
                for(it in enabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_NORMAL
                }
                for(it in disabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_NORMAL
                }
            }
            UniversalTodoItem.DISPLAY_ALTER -> {
                for(it in enabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_ALTER
                }
                for(it in disabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_ALTER
                }
            }
            UniversalTodoItem.DISPLAY_DEL -> {
                for(it in enabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_DEL
                }
                for(it in disabledAdapter.dataList){
                    it.displayMode = UniversalTodoItem.DISPLAY_DEL
                }
            }
        }
        enabledAdapter.notifyDataSetChanged()
        disabledAdapter.notifyDataSetChanged()
    }

    private fun refreshListDisplay(){
        if(enabledAdapter.dataList.size == 0){
            noEnabledHint.visibility = View.VISIBLE
            enabledCardViewLayout.visibility = View.GONE
        }else{
            noEnabledHint.visibility = View.GONE
            enabledCardViewLayout.visibility = View.VISIBLE
        }
        if(disabledAdapter.dataList.size == 0){
            noDisabledHint.visibility = View.VISIBLE
            disabledCardViewLayout.visibility = View.GONE
        }else{
            noDisabledHint.visibility = View.GONE
            disabledCardViewLayout.visibility = View.VISIBLE
        }
    }
}