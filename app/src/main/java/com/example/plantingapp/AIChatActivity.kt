package com.example.plantingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AIChatActivity : AppCompatActivity(), View.OnClickListener {
    private val msgItemList = ArrayList<MsgItem>()
    private var adapter: MsgAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var send: ImageButton
    private lateinit var inputText: EditText
    private lateinit var backgroundSend: CardView
    // 请别忘记返回主页按钮
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS) // 连接超时时间
        .readTimeout(60, TimeUnit.SECONDS)   // 读取超时时间
        .writeTimeout(60, TimeUnit.SECONDS)  // 写入超时时间
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aichat)
        initMsg()
        // init history messages from database?
        recyclerView = findViewById(R.id.recyclerView)
        send = findViewById(R.id.send)
        inputText = findViewById(R.id.inputText)
        backgroundSend = findViewById(R.id.bg_send)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = MsgAdapter(msgItemList)
        recyclerView.adapter = adapter
        send.setOnClickListener(this)
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
                // 使用正确的模型名称，设置 stream 为 true
                val json = """{"model": "llama3.2", "prompt": "$message", "stream": true}"""

                val body = json.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("http://10.0.2.2:11434/api/generate") // 替换为实际的 Ollama 服务器 URL
//                  .url("http://172.20.10.4:11434/api/generate") // @wuzichen's API
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        CoroutineScope(Dispatchers.Main).launch {
                            recyclerView.scrollToPosition(pos)
                            adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_FAILED, "网络错误，对话加载失败")
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
                                        val jsonObject = JSONObject(line)
                                        if (jsonObject.has("response")) {
                                            val partialResponse = jsonObject.getString("response")
                                            buffer += partialResponse
                                            CoroutineScope(Dispatchers.Main).launch {
                                                adapter?.updateReceivedMessage(pos, buffer)
                                                recyclerView.scrollToPosition(pos)
                                            }
                                        }
                                        if (jsonObject.has("done") && jsonObject.getBoolean("done")) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                recyclerView.scrollToPosition(pos)
                                                adapter?.stopAnimator(pos, MsgItem.STATUS_LOAD_SUCCESSFULLY, buffer)
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: JSONException) {
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
                }
                this@AIChatActivity.runOnUiThread {
                    backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this@AIChatActivity, R.color.themeDarkGreen))
                    send.isClickable = true
                }
            }
        }
    }

    private fun parseAIResponse(response: String): String {
        return try {
            val jsonObject = JSONObject(response)
            when {
                jsonObject.has("response") -> jsonObject.getString("response")
                jsonObject.has("error") -> {
                    val error = jsonObject.getString("error")
                    Log.e("AI_Error", "Ollama 返回错误: $error")
                    "AI 服务错误: $error\n请检查模型是否已安装"
                }
                else -> "未知响应格式: $response"
            }
        } catch (e: JSONException) {
            Log.e("AI_Error", "JSON 解析错误", e)
            "解析 AI 响应时出错: ${e.message}"
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