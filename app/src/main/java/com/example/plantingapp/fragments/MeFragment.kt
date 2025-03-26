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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.plantingapp.animators.PicAnimator
import com.example.plantingapp.dao.UserDAO
import com.example.plantingapp.item.DataExchange

class MeFragment : Fragment() {

    private lateinit var userAvatar: ImageView
    private lateinit var userNickname: TextView
    private lateinit var userAlterEntry: TextView
    private lateinit var settingsEntry:TextView
    private lateinit var userPasswordAlter:TextView
    private lateinit var dao:UserDAO
    private lateinit var noAvatar:ImageView
    private lateinit var record1:TextView
    private lateinit var record2:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)

        userAvatar = view.findViewById(R.id.user_avatar)
//        userAvatar.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
//        userAvatar.isZoomEnabled = false
//        userAvatar.isPanEnabled = false
        userNickname = view.findViewById(R.id.user_nickname)
        userPasswordAlter = view.findViewById(R.id.change_password)
        settingsEntry = view.findViewById(R.id.settings)
        userAlterEntry = view.findViewById(R.id.user_nickname_hint)
        noAvatar = view.findViewById(R.id.no_avatar)
        record1 = view.findViewById(R.id.record_count1)
        record2 = view.findViewById(R.id.todo_count1)
        dao = UserDAO(requireContext())

        addOnListeners()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUsr()
        loadExtra()
    }

    private fun loadUsr(){
        val usr = dao.getUserByIdYZR(DataExchange.USERID!!.toInt())
        if(usr!=null){
            userNickname.text = usr.username
            if(usr.userAvatar == null){
                noAvatar.visibility = View.VISIBLE
                userAvatar.visibility = View.GONE
            }else{
                noAvatar.visibility = View.GONE
                userAvatar.visibility = View.VISIBLE
                Glide.with(requireContext()).load(Uri.parse(usr.userAvatar)).into(userAvatar)
                val picAnimator = PicAnimator(requireContext(),usr.userAvatar.toString(),"image_transition")
                picAnimator.attachTo(userAvatar)
            }
        }else{
            Toast.makeText(requireContext(),"用户信息加载失败",Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOnListeners(){
        userAlterEntry.setOnClickListener {
            startActivity(
                Intent(requireContext(),ModifyActivity::class.java)
            )
        }
        settingsEntry.setOnClickListener {
            // 预留
        }
        userPasswordAlter.setOnClickListener {
            startActivity(
                Intent(requireContext(),ChangePasswordActivity::class.java)
            )
        }
    }

    private fun loadExtra(){
        val sp = requireContext().getSharedPreferences(
            "${DataExchange.USERID}_prefs",Context.MODE_PRIVATE)
        var count1 = 0
        var count2 = 0
        with(sp){
            count1 = getInt("logs_recorded_count",0)
            count2 = getInt("todo_finished_count",0)
        }
        record1.text = "$count1"
        record2.text = "$count2"
    }

}