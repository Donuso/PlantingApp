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
import com.example.plantingapp.dao.UserDAO
import com.example.plantingapp.item.DataExchange

class ChangePasswordActivity : BaseActivity() {

    private lateinit var dao:UserDAO
    private lateinit var back : ImageButton
    private lateinit var oldPasswordEdit :EditText
    private lateinit var newPasswordEdit :EditText
    private lateinit var confirmPasswordEdit :EditText
    private lateinit var confirmCardView :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        dao = UserDAO(this)
        back = findViewById<ImageButton>(R.id.back_btn)
        oldPasswordEdit = findViewById<EditText>(R.id.old_password_edit)
        newPasswordEdit = findViewById<EditText>(R.id.new_password_edit)
        confirmPasswordEdit = findViewById<EditText>(R.id.confirm_password_edit)
        confirmCardView = findViewById<TextView>(R.id.confirm_button)

        addOnListeners()

    }

    private fun addOnListeners(){
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        confirmCardView.setOnClickListener {
            if(oldPasswordEdit.text.toString().isEmpty()
                || newPasswordEdit.text.toString().isEmpty()
                || confirmPasswordEdit.text.toString().isEmpty()){
                Toast.makeText(this,"请您完整的填写所有条目",Toast.LENGTH_SHORT).show()
            }else if(newPasswordEdit.text.toString() != confirmPasswordEdit.text.toString()){
                Toast.makeText(this,"您输入的两次新密码不同，请检查",Toast.LENGTH_SHORT).show()
            }else{
                val result = dao.updatePasswordYZR(
                    DataExchange.USERID!!.toInt(),
                    oldPasswordEdit.text.toString(),
                    newPasswordEdit.text.toString()
                )
                when(result){
                    UserDAO.PASSWORD_OLD_NOT_MATCH -> {
                        Toast.makeText(this,"您输入的旧密码不匹配",Toast.LENGTH_SHORT).show()
                    }
                    UserDAO.PASSWORD_NEW_INVALID -> {
                        Toast.makeText(this,"您的新密码不符合规则要求",Toast.LENGTH_SHORT).show()
                    }
                    UserDAO.PASSWORD_SUCCESS -> {
                        Toast.makeText(this,"密码修改成功",Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                    UserDAO.PASSWORD_ALTER_FAILED -> {
                        Toast.makeText(this,"错误：数据库修改失败",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

}