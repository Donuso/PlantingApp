package com.example.plantingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.plantingapp.dao.UserDAO

class LoginActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userPwd: EditText
    private lateinit var autoLogin: View
    private lateinit var autoLoginBG: CardView
    private lateinit var autoLoginText1: TextView
    private lateinit var autoLoginText2: TextView
    private lateinit var confirm: TextView
    private lateinit var userQuery:UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initAll()
        addOnListeners()
    }

    private fun initAll(){
        userName = findViewById(R.id.inputUsername)
        userPwd = findViewById(R.id.inputPassword)
        confirm = findViewById(R.id.confirmLogin)
        userQuery = UserDAO(this)

    }

    private fun addOnListeners(){
        confirm.setOnClickListener{
            val result = userQuery.verifyUser(userName.text.toString(),userPwd.text.toString())
            Toast.makeText(
                this,
                when(result){
                    UserDAO.USER_NOT_EXIST -> "用户不存在，请检查"
                    UserDAO.INCORRECT_PASSWORD -> "密码不正确，请检查密码"
                    UserDAO.AUTH_SUCCESS -> "登录成功"
                    else -> "未知错误"
                },
                Toast.LENGTH_SHORT
            ).show()
            if (result == UserDAO.AUTH_SUCCESS){
                startActivity(Intent(this,MainSwitchActivity::class.java))
            }
        }
    }
}