package com.example.plantingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.plantingapp.fragments.LogFragment
import com.example.plantingapp.fragments.MainFragment
import com.example.plantingapp.fragments.MeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
* 这是fragment的框架显示页
* */

class MainSwitchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main_switch)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_main->loadFragment(MainFragment())
                R.id.navigation_logs->loadFragment(LogFragment())
                R.id.navigation_todos -> loadFragment(TodoFragment())
                R.id.navigation_me->loadFragment(MeFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContent, fragment)
            .commit()
        return true
    }

    }

