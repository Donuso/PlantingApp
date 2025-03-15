package com.example.plantingapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.plantingapp.R
import android.widget.ImageView
import com.example.plantingapp.ModifyActivity
import com.example.plantingapp.ChangePasswordActivity


class MeFragment : Fragment() {

    private lateinit var userAvatar: ImageView

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
            startActivity(intent)
        }

        return view
    }
}