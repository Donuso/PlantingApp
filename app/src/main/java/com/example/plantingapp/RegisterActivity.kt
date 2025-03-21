package com.example.plantingapp

import android.app.VoiceInteractor.Prompt
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.plantingapp.dao.UserDAO

class RegisterActivity : AppCompatActivity() {
    private lateinit var SetUsername: EditText
    private lateinit var SetPassword:EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var PromptInformation: TextView
    private lateinit var autoLogin: View
    private lateinit var autoLoginBG: CardView
    private lateinit var autoLoginText1: TextView
    private lateinit var autoLoginText2: TextView
    private lateinit var confirm: View
    private lateinit var toLogin: View
    private lateinit var userQuery:UserDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initAll()
        setupListeners()
    }
    private fun initAll() {
        SetUsername = findViewById(R.id.setUsername)
        SetPassword = findViewById(R.id.setPassword)
        ConfirmPassword = findViewById(R.id.confirmPassword)
        PromptInformation = findViewById(R.id.prompt)
        autoLogin = findViewById(R.id.touch_layer_automatic_login)
        autoLoginBG = findViewById(R.id.cardViewAutoLogin)
        autoLoginText1 = findViewById(R.id.autoLogin)
        autoLoginText2 = findViewById(R.id.transfer)
        confirm = findViewById(R.id.confirmRegister)
        toLogin = findViewById(R.id.gotoLogin)
        userQuery = UserDAO(this)
    }
    private fun setupListeners() {
        confirm.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val username = SetUsername.text.toString().trim()
        val password = SetPassword.text.toString().trim()
        val confirmPwd = ConfirmPassword.text.toString().trim()

        // 检查用户名是否存在
        val userDao = userQuery
        if (userDao.isUsernameExist(username)) {
            showToast("用户名已存在")
            return
        }

        // 验证用户名
        if (!isValidUsername(username)) {
            showToast("用户名必须包含中文或英文，且长度至少5位")
            return
        }

        // 验证密码
        if (!isValidPassword(password)) {
            showToast("密码至少8位，需包含字母，只能使用数字和字母")
            return
        }

        // 确认密码一致性
        if (password != confirmPwd) {
            showToast("两次输入的密码不一致")
            return
        }



        // 插入新用户
        val userId = userDao.insertUser(username, password)
        if (userId > 0) {
            Toast.makeText(this,"注册成功！请登录",Toast.LENGTH_SHORT).show()
            // 跳转到登录页面
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show()
        }
    }

    // 验证用户名格式
    private fun isValidUsername(username: String): Boolean {
        val pattern = "^(?=.*[a-zA-Z\\u4e00-\\u9fa5])[a-zA-Z\\u4e00-\\u9fa50-9]{5,}\$".toRegex()
        return username.matches(pattern)
    }

    // 验证密码格式
    private fun isValidPassword(password: String): Boolean {
        val pattern = "^(?=.*[A-Za-z])[A-Za-z0-9]{8,}\$".toRegex()
        return password.matches(pattern)
    }

    // 显示Toast提示
    private fun showToast(message: String) {
        PromptInformation.text = message
        PromptInformation.visibility = View.VISIBLE
    }
}