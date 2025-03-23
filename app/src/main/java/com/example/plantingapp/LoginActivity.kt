package com.example.plantingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.plantingapp.dao.UserDAO
import com.example.plantingapp.item.DataExchange

class LoginActivity : BaseActivity() {

    private lateinit var userName: EditText
    private lateinit var userPwd: EditText
    private lateinit var autoLogin: View
    private lateinit var autoLoginBG: CardView
    private lateinit var autoLoginText1: TextView
    private lateinit var autoLoginText2: TextView
    private lateinit var confirm: View
    private lateinit var userQuery: UserDAO
    private lateinit var toRegister: View
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initAll()
        addOnListeners()

        toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        // 自动登录按钮
        autoLogin.setOnClickListener {
            toggleAutoLogin(true)
        }
    }

    private fun initAll() {
        userName = findViewById(R.id.inputUsername)
        userPwd = findViewById(R.id.inputPassword)
        confirm = findViewById(R.id.confirmLogin)
        toRegister = findViewById(R.id.gotoRegister)
        autoLogin = findViewById(R.id.touch_layer_automatic_login)
        autoLoginBG = findViewById(R.id.cardViewAutoLogin)
        autoLoginText1 = findViewById(R.id.autoLogin)
        autoLoginText2 = findViewById(R.id.transfer)
        userQuery = UserDAO(this)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // 初始化 auto_login 为 false（仅在首次运行时需要）
        if (!sharedPreferences.contains("auto_login")) {
            sharedPreferences.edit().putBoolean("auto_login", false).apply()
        }
    }

    private fun toggleAutoLogin(enable: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("auto_login", enable)
            apply()
        }

        if (enable) {
            autoLoginBG.setCardBackgroundColor(ContextCompat.getColor(this, R.color.themeDarkGreen))
            autoLoginText1.setTextColor(ContextCompat.getColor(this, R.color.white))
            autoLoginText2.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            // 可选：恢复原始颜色
        }
    }

    private fun addOnListeners() {
        confirm.setOnClickListener {
            val result = userQuery.verifyUser(userName.text.toString(), userPwd.text.toString())
            Toast.makeText(
                this,
                when (result) {
                    UserDAO.USER_NOT_EXIST -> "用户不存在，请检查"
                    UserDAO.INCORRECT_PASSWORD -> "密码不正确，请检查密码"
                    UserDAO.AUTH_SUCCESS -> "登录成功"
                    else -> "未知错误"
                },
                Toast.LENGTH_SHORT
            ).show()

            if (result == UserDAO.AUTH_SUCCESS) {
                val userId = userQuery.getUserIdByUsername(userName.text.toString())

                // 放入伴生类
                DataExchange.USERID = userId

                // 使用 SharedPreferences 存储数据
                sharedPreferences.edit().putString("user_id", userId).apply()

                // 启动新的 Activity
                startActivity(Intent(this, MainSwitchActivity::class.java))
                Log.i("id", DataExchange.USERID.toString())
                Log.i("spid", sharedPreferences.getString("user_id", "default_id") ?: "null")
            }
        }
    }
}