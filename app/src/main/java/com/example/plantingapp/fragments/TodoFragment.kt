package com.example.plantingapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.plantingapp.AllToDoActivity
import com.example.plantingapp.NewTodoActivity
import com.example.plantingapp.R

/*
* 此fragment对应“待办”的入口
* */


class TodoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置新增任务按钮的点击事件
        view.findViewById<Button>(R.id.buttonnewtodo).setOnClickListener {
            // 跳转到目标界面（假设为 NewTaskActivity）
            val intent = Intent(requireContext(), NewTodoActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.buttonmanagealltodo).setOnClickListener {
            // 跳转到目标界面（假设为 NewTaskActivity）
            val intent = Intent(requireContext(), AllToDoActivity::class.java)
            startActivity(intent)
        }
    }

}