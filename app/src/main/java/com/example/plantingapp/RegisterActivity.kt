package com.example.plantingapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.VoiceInteractor.Prompt
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

class RegisterActivity : BaseActivity() {
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
    private lateinit var sharedPreferences: SharedPreferences
    // 临时存储自动登录状态
    private var tempAutoLoginState = false
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
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }
    private fun setupListeners() {
        // 自动登录按钮
        autoLogin.setOnClickListener {
            tempAutoLoginState = !tempAutoLoginState
            updateAutoLoginUI(tempAutoLoginState)
        }
        confirm.setOnClickListener {
            performRegistration()
        }
        toLogin.setOnClickListener {
            finish()
        }
    }

    private fun updateAutoLoginUI(isAutoLoginEnabled: Boolean) {
        // 背景色动画
        val startColor = if (isAutoLoginEnabled) R.color.white else R.color.themeDarkGreen
        val endColor = if (isAutoLoginEnabled) R.color.themeDarkGreen else R.color.white
        val backgroundAnimator = ObjectAnimator.ofArgb(
            autoLoginBG,
            "cardBackgroundColor",
            ContextCompat.getColor(this, startColor),
            ContextCompat.getColor(this, endColor)
        )
        backgroundAnimator.duration = 300 // 设置动画持续时间
        backgroundAnimator.start()

        // 文字颜色动画
        val textColor1Start = if (isAutoLoginEnabled) R.color.themeDarkGreen else R.color.white
        val textColor1End = if (isAutoLoginEnabled) R.color.white else R.color.themeDarkGreen
        val textColor2Start = if (isAutoLoginEnabled) R.color.general_grey_wzc else R.color.white
        val textColor2End = if (isAutoLoginEnabled) R.color.white else R.color.general_grey_wzc

        // 文字颜色1动画
        val textColor1Animator = ObjectAnimator.ofArgb(
            autoLoginText1,
            "textColor",
            ContextCompat.getColor(this, textColor1Start),
            ContextCompat.getColor(this, textColor1End)
        )
        textColor1Animator.duration = 300

        // 文字颜色2动画
        val textColor2Animator = ObjectAnimator.ofArgb(
            autoLoginText2,
            "textColor",
            ContextCompat.getColor(this, textColor2Start),
            ContextCompat.getColor(this, textColor2End)
        )
        textColor2Animator.duration = 300

        // 同时执行背景和文字颜色动画
        AnimatorSet().apply {
            playTogether(backgroundAnimator, textColor1Animator, textColor2Animator)
            start()
        }

        // 更新文字文本
        autoLoginText2.text = if (isAutoLoginEnabled) "打开" else "关闭"
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
        val Id = userDao.insertUser(username, password)
        if (Id > 0) {
            Toast.makeText(this,"注册成功！请登录",Toast.LENGTH_SHORT).show()
            // 跳转到登录页面
            val user_Id = userQuery.getUserIdByUsername(username)

            Toast.makeText(this,"已登录",Toast.LENGTH_SHORT).show()

            // 保存用户信息和自动登录状态
            sharedPreferences.edit().apply {
                putString("user_id", user_Id)
                putBoolean("auto_login", tempAutoLoginState)
                apply()
            }


            // 放入伴生类
            DataExchange.USERID = user_Id

            // 使用 SharedPreferences 存储数据
            sharedPreferences.edit().putString("user_id", user_Id).apply()

            Log.i("rg_id", DataExchange.USERID.toString())
            Log.i("rg_spid", sharedPreferences.getString("user_id", "default_id") ?: "null")

            startActivity(Intent(this, MainSwitchActivity::class.java))




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