package com.example.plantingapp.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R

class TodoFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_NEW_TODO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置新增任务按钮的点击事件

        view.findViewById<ImageView>(R.id.new_todo).setOnClickListener {
            // 跳转到目标界面（假设为 NewTaskActivity）
            val intent = Intent(requireContext(), NewTodoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_TODO)
        }
        view.findViewById<TextView>(R.id.manage_all_todos).setOnClickListener {
            // 跳转到目标界面（假设为 NewTaskActivity）
            val intent = Intent(requireContext(), AllToDoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_TODO && resultCode == RESULT_OK) {
            // 启动AllToDoActivity并传递数据
            val intent = Intent(requireContext(), AllToDoActivity::class.java)
            intent.putExtra("todo_content", data?.getStringExtra("todo_content"))
            intent.putExtra("selected_date", data?.getStringExtra("selected_date"))
            intent.putExtra("reminder_interval", data?.getStringExtra("reminder_interval"))
            startActivity(intent)
        }
    }
}