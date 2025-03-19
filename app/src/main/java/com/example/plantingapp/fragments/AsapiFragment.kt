package com.example.plantingapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
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
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class AsapiFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvWeather: TextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_yuan, container, false)
        tvWeather = view.findViewById(R.id.tv_weather)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 检查并请求定位权限
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastLocation()
        }

        return view
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        // 使用空合并操作符处理可空返回值
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) ?: emptyList()
                        if (addresses.isNotEmpty()) {
                            val city = addresses[0].locality ?: "未知城市"
                            sendRequestOkHttp(city)
                        } else {
                            Toast.makeText(requireContext(), "无法获取城市信息", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "地理编码错误", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "无法获取位置信息", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "获取位置信息失败", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "定位权限未授予，无法获取位置信息", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        activity?.runOnUiThread {
            if (data.isNotEmpty()) {
                tvWeather.text = data
            } else {
                tvWeather.error = "无法访问该地区天气"
            }
        }
    }
}
