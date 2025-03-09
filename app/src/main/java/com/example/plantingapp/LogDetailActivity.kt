package com.example.plantingapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.plantingapp.network.WeatherResponse
import com.example.plantingapp.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LogDetailActivity : AppCompatActivity() {
    private lateinit var addImageButton: TextView
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private lateinit var currentPhotoPath: String
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_detail)

        // 获取传递过来的数据
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "未知植物"
        val date = intent.getStringExtra("DATE") ?: "20xx年xx月xx日"
        val status = intent.getStringExtra("STATUS")
        val weather = intent.getStringExtra("WEATHER")

        // 设置植物名称和日期
        val plantNameTextView = findViewById<TextView>(R.id.plant_name)
        plantNameTextView.text = plantName
        val dateTextView = findViewById<TextView>(R.id.date)
        dateTextView.text = date

        // 显示状态和天气
        displayStatusAndWeather(status, weather)

        // 设置增加图片的按钮点击事件
        addImageButton = findViewById(R.id.add_image)
        imageView = findViewById(R.id.image_preview) // 假设您已经在布局中添加了一个ImageView用于显示图片
        addImageButton.setOnClickListener {
            showImageOptions()
        }

        // 设置编辑日期的按钮点击事件
        val editDateButton = findViewById<Button>(R.id.datechange_button)
        editDateButton.setOnClickListener {
            // 打开日期选择界面
            val intent = Intent(this, DateSelectionActivity::class.java)
            startActivity(intent)
        }

        // 设置分析按钮的点击事件
        val fenxiButton = findViewById<Button>(R.id.fenxi_button)
        fenxiButton.setOnClickListener {
            // 打开图片选择界面
            val intent = Intent(this, ImageSelectionActivity::class.java)
            startActivity(intent)
        }

        // 设置增加标签的按钮点击事件
        val addTagButton = findViewById<TextView>(R.id.add_tag_button)
        addTagButton.setOnClickListener {
            // 打开标签选择界面
            val intent = Intent(this, NoteSelectActivity::class.java)
            startActivity(intent)
        }

        // 初始化天气信息
        weatherTextView = findViewById(R.id.weather)
        fetchWeatherData("Beijing", "e664d59007e32f779d1d864cc8f349b6")
    }

    private fun displayStatusAndWeather(status: String?, weather: String?) {
        // 获取主布局
        val mainLayout = findViewById<LinearLayout>(R.id.main)

        // 创建叶片颜色标签
        val leafColorView = createStatusView("叶片颜色: $status")
        mainLayout.addView(leafColorView)

        // 创建天气标签
        val weatherTextView = createStatusView("当日天气: $weather")
        mainLayout.addView(weatherTextView)
    }

    private fun createStatusView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(8, 8, 8, 8)
        textView.background = resources.getDrawable(R.drawable.tag_background, theme) // 确保有这个drawable资源
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        return textView
    }

    private fun showImageOptions() {
        val options = arrayOf("拍摄", "手机相册")
        AlertDialog.Builder(this)
            .setTitle("选择图片")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> dispatchPickImageIntent()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.plantingapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun dispatchPickImageIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    imageView.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        imageView.setImageURI(it)
                    }
                }
            }
        } else {
            Toast.makeText(this, "图片选择已取消", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherData(city: String, apiKey: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    weatherData?.let {
                        val weatherDescription = it.weather[0].description
                        val temperature = it.main.temp
                        val cityName = it.name
                        weatherTextView.text = "当日天气: $weatherDescription, 温度: $temperature°C, 城市: $cityName"
                    }
                } else {
                    weatherTextView.text = "无法获取天气数据"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherTextView.text = "网络请求失败"
            }
        })
    }
}