package com.example.plantingapp

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.plantingapp.fragments.LogFragment
import com.example.plantingapp.fragments.MainFragment
import com.example.plantingapp.fragments.MeFragment
import com.example.plantingapp.fragments.TodoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
* 这是fragment的框架显示页
* */

class MainSwitchActivity : BaseActivity() {

    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView: BottomNavigationView

    private var mainFragment: MainFragment = MainFragment()
    private var logFragment: LogFragment = LogFragment()
    private var todoFragment: TodoFragment = TodoFragment()
    private var meFragment: MeFragment = MeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_switch)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.frameLayoutContent, mainFragment)
        transaction.show(mainFragment)
        transaction.commit()
        setBottomNavigationListener()
    }

    private fun switchFragment(tag: String) {
        val transaction = fragmentManager.beginTransaction()
        when (tag) {
            "main" -> {
                transaction.show(mainFragment)
                logFragment.let {
                    if(logFragment.isAdded)
                        transaction.hide(it)
                }
                todoFragment.let {
                    if(todoFragment.isAdded)
                        transaction.hide(it)
                }
                meFragment.let {
                    if(meFragment.isAdded)
                        transaction.hide(it)
                }
                transaction.commit()
            }
            "logs" -> {
                if(!logFragment.isAdded)
                    transaction.add(R.id.frameLayoutContent, logFragment)
                transaction.show(logFragment)
                mainFragment.let { transaction.hide(it)
                }
                todoFragment.let {
                    if(todoFragment.isAdded)
                        transaction.hide(it)
                }
                meFragment.let {
                    if(meFragment.isAdded)
                        transaction.hide(it)
                }
                transaction.commit()
            }
            "todos" -> {
                if(!todoFragment.isAdded)
                    transaction.add(R.id.frameLayoutContent, todoFragment)
                transaction.show(todoFragment)
                mainFragment.let {
                    transaction.hide(it)
                }
                logFragment.let {
                    if(logFragment.isAdded)
                        transaction.hide(it)
                }
                meFragment.let {
                    if(meFragment.isAdded)
                        transaction.hide(it)
                }
                transaction.commit()
            }
            "me" -> {
                if(!meFragment.isAdded)
                    transaction.add(R.id.frameLayoutContent, meFragment)
                transaction.show(meFragment)
                mainFragment.let {
                    transaction.hide(it)
                }
                logFragment.let {
                    if(logFragment.isAdded)
                        transaction.hide(it)
                }
                todoFragment.let {
                    if(todoFragment.isAdded)
                        transaction.hide(it)
                }
                transaction.commit()
            }
        }

    }

    private fun setBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main -> {
                    switchFragment("main")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_logs -> {
                    switchFragment("logs")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_todos -> {
                    switchFragment("todos")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_me -> {
                    switchFragment( "me")
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
}
