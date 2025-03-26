package com.example.plantingapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.common.BaseConfig
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.plantingapp.dao.UserDAO
import java.io.File
import com.example.plantingapp.item.DataExchange
import com.example.plantingapp.item.LogPicItem
import com.example.plantingapp.item.MeItem

class ModifyActivity : BaseActivity() {

    private lateinit var avatarImage:ImageView
    private lateinit var nicknameEdit:EditText
    private lateinit var dao:UserDAO
    private lateinit var back:ImageButton
    private lateinit var rootView:LinearLayout
    private lateinit var confirm:TextView

    private var originalName = ""

    private lateinit var picPickerLauncher:ImagePickerLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        avatarImage = findViewById(R.id.avatar_image)
        nicknameEdit = findViewById(R.id.nickname_edit)
        back = findViewById(R.id.back_btn)
        rootView = findViewById(R.id.main)
        confirm = findViewById(R.id.confirm_button)
        dao = UserDAO(this)
        picPickerLauncher = registerImagePicker { result: List<Image> ->
            val uri = result[0].uri
            val storedUri = Utils.storeSinglePicture(this,uri)
            storedUri?.let {
                Glide.with(this).load(storedUri).into(avatarImage)
                dao.updateUserAvatarYZR(
                    MeItem(
                        DataExchange.USERID!!.toInt(),
                        "",
                        storedUri.toString()
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsr()
        addOnListeners()
    }

    private fun loadUsr(){
        val usr = dao.getUserByIdYZR(DataExchange.USERID!!.toInt())
        if(usr!=null){
            nicknameEdit.setText(usr.username)
            originalName = usr.username
            if(usr.userAvatar != null){
               Glide.with(this).load(usr.userAvatar).into(avatarImage)
            }
        }else{
            Toast.makeText(this,"用户信息加载失败",Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOnListeners(){
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        avatarImage.setOnClickListener {
            picPickerLauncher.launch(
                config = ImagePickerConfig{
                    isShowCamera = false
                    arrowColor = R.color.themeDarkGreen
                    language = "zh-rCN"
                    mode = ImagePickerMode.SINGLE
                    imageTitle = "选择一张图片"
                    doneButtonText = "完成"
                }
            )
        }
        confirm.setOnClickListener {
            if(nicknameEdit.text.toString().length < 5){
                Toast.makeText(this,"用户名太短，应不少于5个字符",Toast.LENGTH_SHORT).show()
            }else if(originalName != nicknameEdit.text.toString()){
                val resultName = dao.updateUserNameYZR(
                    MeItem(
                        DataExchange.USERID!!.toInt(),
                        nicknameEdit.text.toString(),
                        null
                    )
                )
                if(resultName == MeItem.FAILED_ALTER_USERNAME){
                    Toast.makeText(this,"用户名已存在，请尝试其他名称",Toast.LENGTH_SHORT).show()
                }else{
                    onBackPressedDispatcher.onBackPressed()
                }
            }else{
                onBackPressedDispatcher.onBackPressed()
            }
        }
        rootView.setOnClickListener {
            val imm2 = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm2.hideSoftInputFromWindow(nicknameEdit.windowToken, 0)
        }
    }
}