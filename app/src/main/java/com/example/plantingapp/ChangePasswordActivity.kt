package com.example.plantingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.cardview.widget.CardView

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val oldPasswordEdit: EditText = findViewById(R.id.old_password_edit)
        val newPasswordEdit: EditText = findViewById(R.id.new_password_edit)
        val confirmPasswordEdit: EditText = findViewById(R.id.confirm_password_edit)
        val confirmCardView: Button = findViewById(R.id.confirm_button)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
            val intent = Intent(this, MeActivity::class.java)
            startActivity(intent)
            finish()
        }

        confirmCardView.setOnClickListener {
            val oldPassword = oldPasswordEdit.text.toString()
            val newPassword = newPasswordEdit.text.toString()
            val confirmPassword = confirmPasswordEdit.text.toString()

            if (newPassword.length < 8) {
                Toast.makeText(
                    this,
                    "新密码至少8位",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!newPassword.matches("^[A-Za-z0-9]+$".toRegex())) {
                Toast.makeText(
                    this,
                    "新密码只能包含字母和数字",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(
                    this,
                    "两次输入的新密码不一致",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 这里可以添加与服务器交互修改密码的逻辑，例如使用 Retrofit 等网络库
            // 目前只是简单提示密码修改成功
            Toast.makeText(
                this,
                "密码修改成功",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}