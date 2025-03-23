package com.example.plantingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.plantingapp.R

class ModifyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        val back = findViewById<ImageButton>(R.id.back_btn)

        back.setOnClickListener {
            finish()
        }

        val avatarTitle: TextView = findViewById(R.id.avatar_title)
        val avatarImage: ImageView = findViewById(R.id.avatar_image)
        val nicknameTitle: TextView = findViewById(R.id.nickname_title)
        val nicknameEdit: EditText = findViewById(R.id.nickname_edit)

        // 头像点击事件处理
        avatarImage.setOnClickListener {
            // 这里可以添加打开相册或相机的逻辑，例如使用 Intent 调用系统相册或相机应用
            // 示例代码（未完整实现，仅作示意）：
            // val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // 昵称编辑相关逻辑
        nicknameTitle.setOnClickListener {
            val newNickname = nicknameEdit.text.toString().trim()
            if (newNickname.isEmpty()) {
                Toast.makeText(this, "修改昵称不能为空", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show()
                // 这里可以添加保存新昵称到数据库或其他操作的逻辑
            }
        }
    }

    // 以下是处理从相册或相机返回结果的方法示例（未完整实现，仅作示意）
    // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //     super.onActivityResult(requestCode, resultCode, data)
    //     if (resultCode == RESULT_OK) {
    //         when (requestCode) {
    //             PICK_IMAGE_REQUEST -> {
    //                 val selectedImageUri: Uri? = data?.data
    //                 if (selectedImageUri!= null) {
    //                     avatarImage.setImageURI(selectedImageUri)
    //                 }
    //             }
    //             TAKE_PHOTO_REQUEST -> {
    //                 val photo: Bitmap? = data?.extras?.get("data") as Bitmap?
    //                 if (photo!= null) {
    //                     avatarImage.setImageBitmap(photo)
    //                 }
    //             }
    //         }
    //     }
    // }

    // 这里定义从相册选择图片和拍照的请求码
    // private const val PICK_IMAGE_REQUEST = 1
    // private const val TAKE_PHOTO_REQUEST = 2
}