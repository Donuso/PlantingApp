package com.example.plantingapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.plantingapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

class WeatherFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var cityName: String = "北京" // 默认城市

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        // 初始化UI组件
        val tvLocation: TextView = view.findViewById(R.id.myLocation)
        val tvWeather: TextView = view.findViewById(R.id.tv_weather)

        // 初始化位置服务
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 检查位置权限
        checkLocationPermission()

        return view
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 已有权限，获取位置
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // 创建位置请求
        val locationRequest = LocationRequest.create().apply {
            var priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            var interval = 10000
            var fastestInterval = 5000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    getCityName(it)
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getCityName(location: Location) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses?.isNotEmpty() == true) {
                cityName = addresses[0].locality ?: "北京"
                updateLocationUI(cityName)
                sendRequestOkHttp(cityName)
            }
        } catch (e: Exception) {
            Log.e("WeatherFragment", "获取地理位置失败: ${e.message}")
            sendRequestOkHttp(cityName) // 使用默认城市
        }
    }

    private fun updateLocationUI(city: String) {
        view?.findViewById<TextView>(R.id.myLocation)?.text = "当前位置：$city"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "需要位置权限来获取当地天气",
                        Toast.LENGTH_LONG
                    ).show()
                    sendRequestOkHttp(cityName) // 使用默认城市
                }
            }
        }
    }

    // 以下保持你原有的网络请求和数据处理逻辑（需要做少量调整）
    private fun sendRequestOkHttp(city: String) {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://v1.yiketianqi.com/free/v2030?appid=38248357&appsecret=J0tQ6CvV&city=$city")
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                responseData?.let {
                    if (it.isNotEmpty()) {
                        parseWeatherData(it)
                    } else {
                        showError("空响应")
                    }
                } ?: showError("无响应数据")

            } catch (e: Exception) {
                Log.e("WeatherFragment", "网络请求异常: ${e.message}")
                showError("网络连接失败")
            }
        }.start()
    }

    private fun parseWeatherData(json: String) {
        try {
            val obj = JSONObject(json)
            val data = buildString {
                append("今天是：${obj.optString("date")}\n")
                append("星期：${obj.optString("week")}\n")
                append("地区：${obj.optString("city")}\n")
                append("天气：${obj.optString("wea")}\n")
                append("当前温度：${obj.optString("tem")}\n")
                append("最高温度：${obj.optString("tem1")}\n")
                append("最低温度：${obj.optString("tem2")}\n")
                append("风力：${obj.optString("win")}\n")
                append("风速：${obj.optString("win_speed")}")
            }
            updateWeatherUI(data)
        } catch (e: Exception) {
            Log.e("WeatherFragment", "数据解析失败: ${e.message}")
            showError("数据解析失败")
        }
    }

    private fun updateWeatherUI(data: String) {
        activity?.runOnUiThread {
            view?.findViewById<TextView>(R.id.tv_weather)?.text = data
        }
    }

    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            view?.findViewById<TextView>(R.id.tv_weather)?.text = "无法获取天气数据"
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}