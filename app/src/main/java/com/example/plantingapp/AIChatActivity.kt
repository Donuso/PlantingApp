package com.example.plantingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.adapter.MsgAdapter
import com.example.plantingapp.item.MsgItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AIChatActivity : BaseActivity(), View.OnClickListener {
    private val msgItemList = ArrayList<MsgItem>()
    private var adapter: MsgAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var send: ImageButton
    private lateinit var inputText: EditText
    private lateinit var backgroundSend: CardView
    private lateinit var backMain:TextView
    private lateinit var rootView:ConstraintLayout
    // 请别忘记返回主页按钮
    // 阿里云DashScope配置
    private val API_KEY = "sk-ea3444620b0d4468b6610b30d02ecbc1" // 替换为你的阿里云API Key
    private val DASHSCOPE_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
    private val MODEL_NAME = "qwen-turbo" // 或其他阿里云支持的模型

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aichat)
        initMsg()
        recyclerView = findViewById(R.id.recyclerView)
        send = findViewById(R.id.send)
        inputText = findViewById(R.id.inputText)
        backgroundSend = findViewById(R.id.bg_send)
        backMain = findViewById(R.id.return_button)
        rootView = findViewById(R.id.root)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = MsgAdapter(msgItemList)
        recyclerView.adapter = adapter
        send.setOnClickListener(this)
        rootView.setOnClickListener(this)
        backMain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            send -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    val msgItem = MsgItem(content, MsgItem.TYPE_SENT)
                    addMsg(msgItem)
                }
            }
            rootView -> {
                val imm2 = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(inputText.windowToken, 0)
            }
            backMain -> {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun sendMessageToAI(message: String) {
        addMsg(
            MsgItem(
                "", MsgItem.TYPE_RECEIVED, MsgItem.STATUS_START_LOADING
            )
        )
        val pos = msgItemList.size - 1
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 构建阿里云DashScope API请求
                val json = JSONObject().apply {
                    put("model", MODEL_NAME)
                    put("input", JSONObject().apply {
                        put("messages", JSONArray().apply {
                            put(JSONObject().apply {
                                put("role", "user")
                                put("content", message)
                            })
                        })
                    })
                    put("parameters", JSONObject().apply {
                        put("result_format", "message")
                    })
                }.toString()

                val body = json.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(DASHSCOPE_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-DashScope-SSE", "enable")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        CoroutineScope(Dispatchers.Main).launch {
                            recyclerView.scrollToPosition(pos)
                            adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_FAILED, "网络错误，对话加载失败")
                            adapter?.notifyItemChanged(pos)
                        }
                        this@AIChatActivity.runOnUiThread {
                            backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this@AIChatActivity, R.color.themeDarkGreen))
                            send.isClickable = true
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        try {
                            val responseBody = response.body?.source()
                            var buffer = ""
                            responseBody?.use { source ->
                                while (!source.exhausted()) {
                                    val line = source.readUtf8Line()
                                    if (line != null && line.isNotEmpty()) {
                                        try {
                                            // Skip non-JSON lines and empty lines
                                            val trimmedLine = line.trim()
                                            if (trimmedLine.isEmpty() ||
                                                (!trimmedLine.startsWith("{") && !trimmedLine.startsWith("data:"))) {
                                                continue
                                            }

                                            // Remove "data:" prefix if present
                                            val jsonString = if (trimmedLine.startsWith("data:")) {
                                                trimmedLine.substring(5).trim()
                                            } else {
                                                trimmedLine
                                            }

                                            val jsonObject = JSONObject(jsonString)
                                            if (jsonObject.has("output")) {
                                                val output = jsonObject.getJSONObject("output")
                                                if (output.has("choices")) {
                                                    val choices = output.getJSONArray("choices")
                                                    if (choices.length() > 0) {
                                                        val choice = choices.getJSONObject(0)
                                                        if (choice.has("message")) {
                                                            val message = choice.getJSONObject("message")
                                                            val partialResponse = message.getString("content")
                                                            buffer += partialResponse
                                                            CoroutineScope(Dispatchers.Main).launch {
                                                                adapter?.updateReceivedMessage(pos, buffer)
                                                                recyclerView.scrollToPosition(pos)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (jsonObject.has("usage")) {
                                                // Response is complete
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    recyclerView.scrollToPosition(pos)
                                                    adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_SUCCESSFULLY, buffer)
                                                }
                                            }
                                        } catch (e: JSONException) {
                                            Log.e("JSONParse", "Error parsing line: $line", e)
                                            continue
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            CoroutineScope(Dispatchers.Main).launch {
                                recyclerView.scrollToPosition(pos)
                                adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_FAILED, "解析 AI 响应时出错: ${e.message}")
                            }
                        } finally {
                            this@AIChatActivity.runOnUiThread {
                                backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this@AIChatActivity, R.color.themeDarkGreen))
                                send.isClickable = true
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    recyclerView.scrollToPosition(pos)
                    adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_FAILED, "网络错误，对话加载失败")
                    adapter?.notifyItemChanged(pos)
                }
                this@AIChatActivity.runOnUiThread {
                    backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this@AIChatActivity, R.color.themeDarkGreen))
                    send.isClickable = true
                }
            }
        }
    }

    private fun initMsg() {
        val msgItem1 = MsgItem("你好，请问有什么我可以帮助您？", MsgItem.TYPE_RECEIVED)
        msgItemList.add(msgItem1)
    }

    private fun addMsg(item: MsgItem) {
        when (item.type) {
            MsgItem.TYPE_RECEIVED -> {
                msgItemList.add(item)
                adapter?.notifyItemInserted(msgItemList.size - 1)
                recyclerView.scrollToPosition(msgItemList.size - 1)
            }
            MsgItem.TYPE_SENT -> {
                msgItemList.add(item)
                adapter?.notifyItemInserted(msgItemList.size - 1)
                recyclerView.scrollToPosition(msgItemList.size - 1)
                inputText.setText("")
                send.isClickable = false
                backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this, R.color.line_grey_wzc))
                sendMessageToAI(item.content)
            }
        }
    }
}