package com.example.plantingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    private lateinit var receiver: LogoutReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addAct(this)
    }

    override fun onResume() {
        super.onResume()
        val itft = IntentFilter()
        itft.addAction("com.example.plantingapp.LOGOUT")
        receiver = LogoutReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            registerReceiver(receiver, itft, RECEIVER_EXPORTED)
        } else {
            // For older versions
            registerReceiver(receiver, itft)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeAct(this)
    }

    inner class LogoutReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val v = LayoutInflater.from(context).inflate(R.layout.dialog_log_out_wzc, null)
            val cancel = v.findViewById<TextView>(R.id.cancel)
            val confirm = v.findViewById<TextView>(R.id.make_sure)

            val dialog = AlertDialog.Builder(context,R.style.CustomDialogTheme)
                .setView(v)
                .create()

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            confirm.setOnClickListener {
                ActivityCollector.finishAll()
                // 这中间的数据变化，就交给你来写了
                val i = Intent(context,LoginActivity::class.java)
                context.startActivity(i)
            }

            dialog.show()
        }
    }

}