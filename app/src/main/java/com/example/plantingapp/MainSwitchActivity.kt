package com.example.plantingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantingapp.fragments.LogFragment
import com.example.plantingapp.fragments.MainFragment
import com.example.plantingapp.fragments.MeFragment
import com.example.plantingapp.fragments.TodoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.plantingapp.fragments.WeatherFragment

/*
* 这是fragment的框架显示页
* */

class MainSwitchActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView: BottomNavigationView

//    private var originalStatusBarColor: Int = 0xffffff
//    private var alterStatusBarColor: Int = 0xffffff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_switch)
//        originalStatusBarColor = ContextCompat.getColor(this, R.color.themeDarkGreen)
//        alterStatusBarColor = ContextCompat.getColor(this, R.color.themeLightGreen)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        switchFragment(MainFragment())
        setBottomNavigationListener()

    }

    private fun setBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main -> {
                    switchFragment(MainFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_logs -> {
                    switchFragment(LogFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_todos -> {
                    switchFragment(TodoFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_me -> {
                    switchFragment(MeFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContent, fragment)
            .commit()
    }
}
