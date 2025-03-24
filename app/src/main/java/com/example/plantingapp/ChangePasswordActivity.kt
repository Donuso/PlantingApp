package com.example.plantingapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        dbHelper = DBHelper(this)

        val back = findViewById<ImageButton>(R.id.back_btn)
        val oldPasswordEdit = findViewById<EditText>(R.id.old_password_edit)
        val newPasswordEdit = findViewById<EditText>(R.id.new_password_edit)
        val confirmPasswordEdit = findViewById<EditText>(R.id.confirm_password_edit)
        val confirmCardView = findViewById<TextView>(R.id.confirm_button)

        back.setOnClickListener {
            finish()
        }

        confirmCardView.setOnClickListener {
            val oldPassword = oldPasswordEdit.text.toString()
            val newPassword = newPasswordEdit.text.toString()
            val confirmPassword = confirmPasswordEdit.text.toString()

            if (oldPassword.isEmpty()) {
                Toast.makeText(
                    this,
                    "原密码不得为空",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isOldPasswordCorrect(oldPassword)) {
                Toast.makeText(
                    this,
                    "原密码输入错误",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

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

            if (updatePassword(newPassword)) {
                Toast.makeText(
                    this,
                    "密码修改成功",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "密码修改失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isOldPasswordCorrect(oldPassword: String): Boolean {
        val db = dbHelper.readableDatabase
        val projection = arrayOf("password")
        val selection = "password = ?"
        val selectionArgs = arrayOf(oldPassword)
        val cursor: Cursor = db.query(
            "user",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val isCorrect = cursor.count > 0
        cursor.close()
        return isCorrect
    }

    private fun updatePassword(newPassword: String): Boolean {
        val db = dbHelper.writableDatabase
        val sql = "UPDATE user SET password = ? WHERE userId = '1'"
        return try {
            db.execSQL(sql, arrayOf(newPassword))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}