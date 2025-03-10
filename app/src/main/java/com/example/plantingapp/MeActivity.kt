package com.example.plantingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.plantingapp.fragments.MeFragment

class MeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)
        // 创建 MeFragment 实例
        val meFragment = MeFragment()

        // 获取 FragmentManager
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        // 将 MeFragment 添加到容器中
        fragmentTransaction.add(R.id.fragment_container, meFragment)

        // 提交事务
        fragmentTransaction.commit()
    }
}