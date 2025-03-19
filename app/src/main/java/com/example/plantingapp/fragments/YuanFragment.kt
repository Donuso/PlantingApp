package com.example.plantingapp.fragments

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.example.plantingapp.R

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class YuanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_yuan, container, false)

        // 初始化返回按钮并设置点击事件
        val iv_back: ImageView = view.findViewById(R.id.iv_back)
        iv_back.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://hunan.promotion.weather.com.cn/mweather/101070201.shtml")
            startActivity(intent)
        }

        // 初始化搜索按钮并设置点击事件
        val iv_search: ImageView = view.findViewById(R.id.iv_search)
        iv_search.setOnClickListener {
            val edt_city: EditText = view.findViewById(R.id.edt_city)
            val city = edt_city.text.toString()
            if (city.isNotEmpty()) {
                sendRequestOkHttp(city)
            } else {
                edt_city.setError("请输入有效的城市名!")
            }
        }

        return view
    }

    private fun sendRequestOkHttp(city: String) {
        // 开启子线程访问网络数据
        Thread {
            val client = OkHttpClient() // 创建客户端
            val request: Request = Request.Builder()
                .url("https://v1.yiketianqi.com/free/v2030?appid=38248357&appsecret=J0tQ6CvV&city=$city")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                val responsedata: String? = response.body?.string()
                Log.i("WeatherFragment", responsedata.toString())
                if (responsedata != null) {
                    getdata(responsedata)
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "获取数据失败", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(activity, "网络请求错误", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getdata(res: String) {
        val jsondata = JSONObject(res)
        val city: String = jsondata.optString("city")
        var data = ""
        if (city.isNotEmpty()) {
            val date = jsondata.optString("date")
            val wea = jsondata.optString("wea")
            val week = jsondata.optString("week")
            val tem = jsondata.optString("tem")
            val tem1 = jsondata.optString("tem1")
            val tem2 = jsondata.optString("tem2")
            val win_speed = jsondata.optString("win_speed")
            val win = jsondata.optString("win")
            data = "今天是：$date\n星期：$week\n地区:$city\n天气:" +
                    "$wea\n当前温度：$tem\n最高温度：$tem1\n" +
                    "最低温度：$tem2\n风力：$win\n风速：$win_speed"
        }
        show(data)
    }

    private fun show(data: String) {
        val tv_weather: TextView = view?.findViewById(R.id.tv_weather) ?: return
        activity?.runOnUiThread {
            if (data.isNotEmpty()) {
                tv_weather.text = data
            } else {
                tv_weather.setError("无法访问该地区天气")
            }
        }
    }
}
