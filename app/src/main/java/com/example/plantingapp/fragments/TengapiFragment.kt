package com.example.plantingapp.fragments

import com.example.plantingapp.R
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class TengapiFragment : Fragment() {

    private lateinit var locationManager: TencentLocationManager
    private var isLocationStarted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_yuan, container, false)

        // 初始化定位SDK
        initLocation()
        return view
    }

    private fun initLocation() {
        // 必须设置用户同意隐私政策（需要在用户同意后调用）
        TencentLocationManager.setUserAgreePrivacy(true)

        locationManager = TencentLocationManager.getInstance(requireContext())

        // 检查定位权限
        if (checkLocationPermission()) {
            startLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocation()
                } else {
                    Toast.makeText(activity, "需要定位权限才能获取当地天气", Toast.LENGTH_SHORT).show()
                    // 使用默认城市作为回退方案
                    sendRequestOkHttp("广州")
                }
            }
        }
    }

    private fun startLocation() {
        if (isLocationStarted) return

        val request = TencentLocationRequest.create()
            .setInterval(30000) // 定位间隔
            .setAllowGPS(true) // 允许GPS
            .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA) // 获取城市级别

        val listener = object : TencentLocationListener {
            override fun onLocationChanged(
                location: TencentLocation?,
                error: Int,
                reason: String?
            ) {
                when (error) {
                    TencentLocation.ERROR_OK -> {
                        location?.let {
                            Log.d("LocationResult", "原始城市名称: ${it.city}")
                            Log.d("LocationDebug", "定位成功详情: " +
                                    "城市=${it.city} " +
                                    "区=${it.district} " +
                                    "经度=${it.longitude} " +
                                    "纬度=${it.latitude}")
                            val processedCity = it.city.replace("市", "") // 去除市字后缀
                            Log.d("LocationResult", "处理后城市: $processedCity")
                            sendRequestOkHttp(processedCity)
                        }
                    }
                    else -> {
                        Toast.makeText(activity, "定位失败: $reason", Toast.LENGTH_SHORT).show()
                        sendRequestOkHttp("广州") // 失败时使用默认城市
                    }
                }
                // 停止定位防止重复请求
                locationManager.removeUpdates(this)
                isLocationStarted = false
            }

            override fun onStatusUpdate(
                name: String?,
                status: Int,
                desc: String?
            ) {
                // 状态更新处理
            }
        }

        // 开始定位
        val result = locationManager.requestLocationUpdates(request, listener)
        isLocationStarted = true

        when (result) {
            0 -> Log.d("Location", "定位请求成功")
            1 -> Toast.makeText(activity, "设备缺少定位所需硬件", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(activity, "定位密钥错误", Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(activity, "初始化异常", Toast.LENGTH_SHORT).show()
        }
    }

    // 以下原有方法保持不变，只需要修改sendRequestOkHttp的调用方式
    private fun sendRequestOkHttp(city: String) {
        Thread {
            val client = OkHttpClient()
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

    companion object {
        private const val REQUEST_CODE_LOCATION = 1001
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放定位资源
        locationManager.removeUpdates(null)
    }
}