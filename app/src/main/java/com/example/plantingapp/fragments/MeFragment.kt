package com.example.plantingapp.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.plantingapp.DBHelper
import com.example.plantingapp.R
import com.example.plantingapp.ModifyActivity
import com.example.plantingapp.ChangePasswordActivity
import java.io.File
import android.app.Activity
import android.content.Intent
import com.example.plantingapp.DataExchange

class MeFragment : Fragment() {

    private lateinit var userAvatar: ImageView
    private lateinit var userNickname: TextView

    private val modifyActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 当 ModifyActivity 返回 RESULT_OK 时，刷新头像显示
            updateAvatar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)

        // 找到用户头像的 ImageView
        userAvatar = view.findViewById(R.id.user_avatar)
        userNickname = view.findViewById(R.id.user_nickname)

        val changePasswordCardView = view.findViewById<TextView>(R.id.change_password)
        changePasswordCardView.setOnClickListener {
            // 处理修改密码的逻辑，比如跳转到修改密码页面
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val settingsView = view.findViewById<TextView>(R.id.settings)
        settingsView.setOnClickListener {
            // 处理设置的逻辑，比如跳转到设置页面
        }

        val userNicknameHint = view.findViewById<TextView>(R.id.user_nickname_hint)
        userNicknameHint.setOnClickListener {
            val intent = Intent(requireContext(), ModifyActivity::class.java)
            modifyActivityLauncher.launch(intent)
        }

        // 添加头像点击事件处理
        userAvatar.setOnClickListener {
            val avatarUri = getAvatarUri()
            if (avatarUri != null) {
                showEnlargedAvatarDialog(avatarUri)
            }
        }

        // 获取数据库实例并查询用户名和头像
        updateAvatar()
        updateNickname()

        return view
    }

    private fun getAvatarUri(): Uri? {
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase
        val projection = arrayOf("user_avatar")
        val selection = "userId =?"
        val selectionArgs = arrayOf("1")
        val cursor: Cursor = db.query(
            "user",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var avatarUri: Uri? = null
        if (cursor.moveToFirst()) {
            val avatarColumnIndex = cursor.getColumnIndex("user_avatar")
            if (avatarColumnIndex != -1) {
                val avatarPath = cursor.getString(avatarColumnIndex)
                if (avatarPath != null) {
                    avatarUri = Uri.parse(avatarPath)
                }
            }
        }

        cursor.close()
        db.close()
        return avatarUri
    }

    private fun updateAvatar() {
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase
        val projection = arrayOf("user_avatar")
        val selection = "userId =?"
        val selectionArgs = arrayOf("1")
        val cursor: Cursor = db.query(
            "user",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val avatarColumnIndex = cursor.getColumnIndex("user_avatar")
            if (avatarColumnIndex != -1) {
                val avatarUri = cursor.getString(avatarColumnIndex)
                Log.d("MeFragment", "Retrieved avatar URI: $avatarUri")
                if (avatarUri != null) {
                    setAvatarImage(Uri.parse(avatarUri))
                }
            }
        }

        cursor.close()
        db.close()
    }

    private fun updateNickname() {
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase
        val projection = arrayOf("username")
        val selection = "userId =?"
        val selectionArgs = arrayOf("1")
        val cursor: Cursor = db.query(
            "user",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val usernameColumnIndex = cursor.getColumnIndex("username")
            if (usernameColumnIndex != -1) {
                val username = cursor.getString(usernameColumnIndex)
                userNickname.text = username
            } else {
                userNickname.text = "默认昵称"
            }
        }

        cursor.close()
        db.close()
    }

    private fun setAvatarImage(uri: Uri) {
        try {
            val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
            userAvatar.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("MeFragment", "Error setting avatar image: ${e.message}", e)
            val file = File(requireContext().filesDir, "avatar.jpg")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                userAvatar.setImageBitmap(bitmap)
            }
        }
    }

    private fun showEnlargedAvatarDialog(uri: Uri) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_enlarge_avatar)
        val enlargedImageView = dialog.findViewById<ImageView>(R.id.full_screen_image)

        try {
            val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
            enlargedImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("MeFragment", "Error loading enlarged avatar: ${e.message}", e)
        }

        // 获取设备屏幕尺寸
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val minScreenSize = Math.min(screenWidth, screenHeight)

        // 设置头像的大小为设备最短边的长度
        val layoutParams = enlargedImageView.layoutParams
        layoutParams.width = minScreenSize
        layoutParams.height = minScreenSize
        enlargedImageView.layoutParams = layoutParams

        enlargedImageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            // 对话框关闭时的处理逻辑（如果有）
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        // 在页面恢复时重新查询数据库，以显示更新后的昵称和头像
        updateAvatar()
        updateNickname()
    }
}