package com.example.plantingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.plantingapp.fragments.LogFragment
import com.example.plantingapp.fragments.MainFragment
import com.example.plantingapp.fragments.MeFragment
import com.example.plantingapp.fragments.TodoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainSwitchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_switch)

        // 初始化底部导航栏
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 设置默认显示的Fragment
        if (savedInstanceState == null) {
            // 默认加载 MainFragment
            replaceFragment(MainFragment())
        }

        // 为底部导航栏设置点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_main -> {
                    replaceFragment(MainFragment())
                    true
                }
                R.id.navigation_logs -> {
                    replaceFragment(LogFragment())
                    true
                }
                R.id.navigation_me -> {
                    replaceFragment(MeFragment())
                    true
                }
                R.id.navigation_todos -> {
                    replaceFragment(TodoFragment())
                    true
                }
                else -> false
            }
        }
    }

    // 替换Fragment的通用方法
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContent, fragment)
            .commit()
    }
}
