package com.example.plantingapp.fragments

import com.example.plantingapp.R
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.plantingapp.Constants
import com.example.plantingapp.animators.FadeAnimator
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.time.LocalDate
import kotlin.math.roundToInt
class MainFragment : Fragment() {

    private lateinit var locationManager: TencentLocationManager
    private var isLocationStarted = false
    //初始化控件
    private lateinit var AreaTextView: TextView
    private lateinit var WeatherTextView: TextView
    private lateinit var TemperatureRangeTextView: TextView
    private lateinit var MoistureTextView: TextView
    private lateinit var WindTextView: TextView
    private lateinit var RainTextView: TextView
    private lateinit var NextClimateTextView: TextView
    private lateinit var TemperatureTextView: TextView
    private lateinit var LoadingCardView: CardView
    private lateinit var LoadingWeather: TextView
    private lateinit var LoadingWeatherAgain: LinearLayout
    private lateinit var WeatherPhoto: ImageView
    private lateinit var AttentionCardView: CardView
    private lateinit var Attention: TextView
    private lateinit var layerAnimator: FadeAnimator

    //SharedPreferences文件
    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceFileName = "weather_info"
    private var isFirstLoad = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        //初始化
        sharedPreferences = requireActivity().getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)

        //绑定控件
        AreaTextView = view.findViewById(R.id.area)
        WeatherTextView = view.findViewById(R.id.weather)
        TemperatureRangeTextView = view.findViewById(R.id.temperature_range)
        MoistureTextView = view.findViewById(R.id.moisture)
        WindTextView = view.findViewById(R.id.wind)
        RainTextView = view.findViewById(R.id.rain)
        TemperatureTextView = view.findViewById(R.id.currentTemperature)
        LoadingCardView = view.findViewById(R.id.loadingCardView)
        LoadingWeather = view.findViewById(R.id.loadingWeather)
        LoadingWeatherAgain = view.findViewById(R.id.loadingWeatherAgain)
        AttentionCardView = view.findViewById(R.id.attentionCardView)
        Attention = view.findViewById(R.id.attention)
        WeatherPhoto = view.findViewById(R.id.weatherPhoto)
        NextClimateTextView = view.findViewById(R.id.nextClimate)
        layerAnimator = FadeAnimator(LoadingCardView).setDuration(200)


        // 初始化定位SDK，但是被移动到onResume方法中
//        initLocation()
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
            Constants.REQUEST_CODE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocation()
                } else {
                    Toast.makeText(activity, "需要定位权限才能获取当地天气", Toast.LENGTH_SHORT).show()
                    //重新获取权限
                    PermissionRety()
                }
            }
        }
    }

    private fun startLocation() {

        val request = TencentLocationRequest.create()
            .setInterval(30000) // 定位间隔
            .setAllowGPS(true) // 允许GPS
            .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA) // 获取城市级别

        val listener = object : TencentLocationListener {
            override fun onLocationChanged(location: TencentLocation?, error: Int, reason: String?) {
                when (error) {
                    TencentLocation.ERROR_OK -> {
                        location?.let {
                            val city = it.city
                            if (city != null) {
                                Log.d("LocationResult", "原始城市名称: $city")
                                Log.d("LocationDebug", "定位成功详情: " +
                                        "城市=$city " +
                                        "区=${it.district} " +
                                        "经度=${it.longitude} " +
                                        "纬度=${it.latitude}")
                                val processedCity = city.replace("市", "") // 去除市字后缀
                                Log.d("LocationResult", "处理后城市: $processedCity")
                                sendRequestOkHttp(processedCity)
                                if(LoadingCardView.visibility == View.VISIBLE){
                                    requireActivity().runOnUiThread{
                                        layerAnimator.start(false)
                                    }
                                } else {

                                }
                            } else {
                                activity?.runOnUiThread {
                                    Toast.makeText(activity, "定位失败: 无法获取城市信息", Toast.LENGTH_SHORT).show()
                                    LocationRetry() // 显示定位重试按钮
                                }
                            }
                        } ?: run {
                            activity?.runOnUiThread {
                                Toast.makeText(activity, "定位失败: 位置信息为空", Toast.LENGTH_SHORT).show()
                                LocationRetry() // 显示定位重试按钮
                            }
                        }
                    }
                    else -> {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "定位失败: $reason", Toast.LENGTH_SHORT).show()
                            LocationRetry() // 显示定位重试按钮
                        }
                    }
                }
                // 停止定位防止重复请求
                locationManager.removeUpdates(this)
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

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // 以下原有方法保持不变，只需要修改sendRequestOkHttp的调用方式
    private fun sendRequestOkHttp(city: String) {
        if (!isNetworkAvailable(requireContext())) {
            activity?.runOnUiThread {
                Toast.makeText(activity, "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show()
                WeatherRetry(city) // 显示天气重试按钮
            }
            return
        }
        Thread {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url("https://v1.yiketianqi.com/free/v2030?appid=53198836&appsecret=b4UesmFt&city=$city")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                Log.i("Response", "1")
                val responsedata: String? = response.body?.string()
                Log.i("weatherS", responsedata.toString())
                Log.i("Responsedata", "1")
                if (responsedata != null) {
                    Log.i("kong", "1")
                    getdata(responsedata)
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "获取数据失败", Toast.LENGTH_SHORT).show()
                        Log.i("fail", "1")

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    if (isAdded && !isDetached) { // 确保 Fragment 仍然附加到 Activity
                        Toast.makeText(activity, "网络请求错误: ${e.message}", Toast.LENGTH_SHORT).show()
                        WeatherRetry(city) // 显示天气重试按钮
                    }
                }
            }
        }.start()
    }


    private fun getdata(res: String) {
        val jsondata = JSONObject(res)

        val city: String = jsondata.optString("city")
        if (city.isNotEmpty()) {
            val wea = jsondata.optString("wea")
            var tem = jsondata.optString("tem")
            Log.i("wendu", tem)
            var tem1 = jsondata.optString("tem1")
            var tem2 = jsondata.optString("tem2")
            val win_speed = jsondata.optString("win_speed")
            val humidity = jsondata.optString("humidity")
            val rain_pcpn = jsondata.optString("rain_pcpn")
            val wea_img = jsondata.optString("wea_img")
            val temFloat = tem.toFloatOrNull() ?: 0.0f
            val temInt = temFloat.roundToInt()
            tem = temInt.toString()
            val tem1Float = tem1.toFloatOrNull() ?: 0.0f
            val tem1Int = tem1Float.roundToInt()
            tem1 = tem1Int.toString()
            val tem2Float = tem2.toFloatOrNull() ?: 0.0f
            val tem2Int = tem2Float.roundToInt()
            tem2 = tem2Int.toString()
            val nextTerm = getNextSolarTerm()
            // 解析 alarm 数组
            val alarmArray = jsondata.optJSONArray("alarm")
            val alarmTitles = mutableListOf<String>()
            if (alarmArray != null) {
                for (i in 0 until alarmArray.length()) {
                    val alarmObject = alarmArray.getJSONObject(i)
                    val alarmTitle = alarmObject.optString("alarm_title")
                    if (alarmTitle.isNotEmpty()) {
                        alarmTitles.add(alarmTitle)
                    }
                }
            }


            // 存储到 SharedPreferences
            requireActivity().getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE).apply {
                edit().apply {
                    putString("city", city)
                    putString("wea", wea)
                    putString("tem", tem)
                    putString("tem1", tem1)
                    putString("tem2", tem2)
                    putString("win_speed", win_speed)
                    putString("humidity", humidity)
                    putString("rain_pcpn",rain_pcpn)
                    putString("nextTerm",nextTerm)
                    putString("wea_img",wea_img)
                    putString("alarm_titles", alarmTitles.joinToString(", "))
                    apply()
                }
            }
        }
        loadWeatherData()
    }
    private fun loadWeatherData() {
        val prefs = requireActivity().getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
        Log.i("Response","2")
        requireActivity().runOnUiThread{
            AreaTextView.text = prefs.getString("city", "--")
            TemperatureTextView.text = "${prefs.getString("tem", "--")}℃"
            TemperatureRangeTextView.text = "${prefs.getString("tem1", "--")}℃~${prefs.getString("tem2", "--")}℃"
            MoistureTextView.text = "${prefs.getString("humidity", "--")}%"
            WeatherTextView.text = prefs.getString("wea", "-- --")
            LoadingWeatherIcon(prefs)
            WindTextView.text = prefs.getString("win_speed", "--")
            RainTextView.text = "${prefs.getString("rain_pcpn", "--")}mm"
            NextClimateTextView.text = prefs.getString("nextTerm", "--")
            val alarmTitle = prefs.getString("alarm_title",null)
            if (alarmTitle != null){
                AttentionCardView.setVisibility(View.VISIBLE)
                Attention.text = alarmTitle
            }
        }
    }
    fun getNextSolarTerm(): String {
        val solarTerms = Constants.SOLAR_TERMS  // 直接访问伴生对象中的常量
        val currentDate = LocalDate.now()
        var nextTerm = ""
        var found = false

        for ((term, date) in solarTerms) {
            if (currentDate < date && !found) {
                nextTerm = term
                found = true
            }
        }

        if (nextTerm.isEmpty()) {
            nextTerm = solarTerms.first().first
        }

        return nextTerm
    }
    private fun LoadingWeatherIcon(prefs: SharedPreferences){
        val weaImgValue = prefs.getString("wea_img", null)
        when (weaImgValue){
            "qing" -> WeatherPhoto.setImageResource(R.drawable.icon_sun_zhj)
            "yin" -> WeatherPhoto.setImageResource(R.drawable.icon_cloudy_zhj)
            "yun" -> WeatherPhoto.setImageResource(R.drawable.icon_cloud_zhj)
            "shachen" -> WeatherPhoto.setImageResource(R.drawable.icon_dusty_zhj)
            "yu" -> WeatherPhoto.setImageResource(R.drawable.icon_rainy_zhj)
            "xue" -> WeatherPhoto.setImageResource(R.drawable.icon_snowy_zhj)
            "wu" -> WeatherPhoto.setImageResource(R.drawable.icon_foggy_zhj)
            "bingbao" -> WeatherPhoto.setImageResource(R.drawable.icon_hail_icon)
            "lei" -> WeatherPhoto.setImageResource(R.drawable.icon_thunder_zhj)
        }
    }
    private fun PermissionRety(){
        LoadingCardView.setVisibility(View.VISIBLE)
        LoadingCardView.setOnClickListener {
            requestLocationPermission()
        }
    }
    private fun LocationRetry(){
        layerAnimator.start(true)
        LoadingCardView.setOnClickListener {
            startLocation()
        }
    }
    private fun WeatherRetry(city: String){
        LoadingCardView.setVisibility(View.VISIBLE)
        LoadingCardView.setOnClickListener {
            sendRequestOkHttp(city)
        }
    }

//    companion object {
//        private const val REQUEST_CODE_LOCATION = 1001
//    }

    override fun onPause() {
        super.onPause()
        // 释放定位资源
        locationManager.removeUpdates(null)
    }


    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            // 首次加载：触发定位和天气请求
            initLocation() // 初始化定位（会触发天气请求）
            isFirstLoad = false // 标志位置为 false，后续不再触发
        } else {
            val prefs = requireActivity().getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
            AreaTextView.text = prefs.getString("city", "--")
            TemperatureTextView.text = "${prefs.getString("tem", "--")}℃"
            TemperatureRangeTextView.text = "${prefs.getString("tem1", "--")}℃~${prefs.getString("tem2", "--")}℃"
            MoistureTextView.text = "${prefs.getString("humidity", "--")}%"
            WeatherTextView.text = prefs.getString("wea", "-- --")
            LoadingWeatherIcon(prefs)
            WindTextView.text = prefs.getString("win_speed", "--")
            RainTextView.text = "${prefs.getString("rain_pcpn", "--")}mm"
            NextClimateTextView.text = prefs.getString("nextTerm", "--")
            val alarmTitle = prefs.getString("alarm_title",null)
            if (alarmTitle != null){
                AttentionCardView.setVisibility(View.VISIBLE)
                Attention.text = alarmTitle
            }
        }
    }



}