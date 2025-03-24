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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.io.File

class ModifyActivity : AppCompatActivity() {

    private companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val CROP_IMAGE_REQUEST = 2
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        checkPermissionResult()
    }

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

        // 显示当前用户名（这里可以保留获取当前用户名的逻辑，也可以直接从数据库获取固定 userId = 1 的用户名显示）
        val currentUsername = getCurrentUsername()
        if (currentUsername != null) {
            nicknameEdit.setText(currentUsername)
        }

        // 头像点击事件处理
        avatarImage.setOnClickListener {
            requestPermissions()
        }

        // 昵称编辑相关逻辑
        nicknameTitle.setOnClickListener {
            val newNickname = nicknameEdit.text.toString().trim()
            if (newNickname.isEmpty()) {
                Toast.makeText(this, "修改昵称不能为空", Toast.LENGTH_SHORT).show()
            } else if (newNickname.length < 5) {
                Toast.makeText(this, "昵称得大于等于5", Toast.LENGTH_SHORT).show()
            } else {
                // 直接使用固定的 userId = 1 更新数据库中的昵称
                val dbHelper = DBHelper(this)
                val db = dbHelper.writableDatabase
                val updateQuery = "UPDATE user SET username =? WHERE userId = 1"
                db.execSQL(updateQuery, arrayOf(newNickname))
                db.close()

                Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show()
                // 更新 DataExchange 中的用户名
                DataExchange.setCurrentUsername(newNickname)
                // 关闭当前页面，返回上一页
                finish()
            }
        }

        // 为头像标题添加点击事件监听器
        avatarTitle.setOnClickListener {
            if (avatarImage.drawable == null) {
                Toast.makeText(this, "请点击相框选择新头像", Toast.LENGTH_SHORT).show()
            } else {
                // 如果有照片，将图片保存到数据库
                val drawable = avatarImage.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    saveBitmapToDatabase(bitmap)
                    Toast.makeText(this, "头像更新成功", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    // 获取当前用户名（这里获取的用户名可以用于显示初始值，也可以考虑直接从数据库获取 userId = 1 的用户名）
    private fun getCurrentUsername(): String? {
        return DataExchange.getCurrentUsername()
    }

    private fun requestPermissions() {
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            }
            android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.TIRAMISU -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                )
            }
            else -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun checkPermissionResult() {
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) -> {
                // Android 13及以上完整照片和视频访问权限
                openGallery()
            }
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED -> {
                // Android 14及以上部分照片和视频访问权限
                openGallery()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                // Android 12及以下完整本地读写访问权限
                openGallery()
            }
            else -> {
                // 无本地读写访问权限
                Toast.makeText(this, "没有权限访问相册", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun cropImage(uri: Uri) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(uri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 256)
        cropIntent.putExtra("outputY", 256)
        cropIntent.putExtra("return-data", true)
        startActivityForResult(cropIntent, CROP_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        cropImage(selectedImageUri)
                    }
                }
                CROP_IMAGE_REQUEST -> {
                    val avatarImage = findViewById<ImageView>(R.id.avatar_image)
                    val croppedImageUri = data?.data
                    if (croppedImageUri != null) {
                        Glide.with(this)
                            .load(croppedImageUri)
                            .centerCrop()
                            .into(avatarImage)
                        saveImageToDatabase(croppedImageUri)
                    } else {
                        val extras = data?.extras
                        if (extras != null) {
                            val croppedBitmap = extras.getParcelable<Bitmap>("data")
                            if (croppedBitmap != null) {
                                avatarImage.setImageBitmap(croppedBitmap)
                                saveBitmapToDatabase(croppedBitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveImageToDatabase(uri: Uri) {
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase
        // 直接使用固定的 userId = 1 更新数据库中的头像
        val updateQuery = "UPDATE user SET user_avatar =? WHERE userId = 1"
        db.execSQL(updateQuery, arrayOf(uri.toString()))
        db.close()
    }

    private fun saveBitmapToDatabase(bitmap: Bitmap) {
        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase
        val imageUri = saveBitmapToInternalStorage(bitmap)
        // 直接使用固定的 userId = 1 更新数据库中的头像
        val updateQuery = "UPDATE user SET user_avatar =? WHERE userId = 1"
        db.execSQL(updateQuery, arrayOf(imageUri.toString()))
        db.close()
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri {
        val fileName = "avatar.jpg"
        val outputStream = openFileOutput(fileName, MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        val fileUri = Uri.fromFile(File(filesDir, fileName))
        Log.d("ModifyActivity", "Saved bitmap to internal storage: $fileUri")
        return fileUri
    }
}