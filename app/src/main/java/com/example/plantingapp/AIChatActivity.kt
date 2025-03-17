package com.example.plantingapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantingapp.Msg
import com.example.plantingapp.MsgAdapter
import com.example.plantingapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIChatActivity : AppCompatActivity(), View.OnClickListener {
    private val msgList = ArrayList<Msg>()
    private var adapter: MsgAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var send: Button
    private lateinit var inputText: EditText

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // 连接超时时间
        .readTimeout(60, TimeUnit.SECONDS)   // 读取超时时间
        .writeTimeout(60, TimeUnit.SECONDS)  // 写入超时时间
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aichat)
        initMsg()
        recyclerView = findViewById(R.id.recyclerView)
        send = findViewById(R.id.send)
        inputText = findViewById(R.id.inputText)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = MsgAdapter(msgList)
        recyclerView.adapter = adapter
        send.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            send -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    recyclerView.scrollToPosition(msgList.size - 1)
                    inputText.setText("")
                    sendMessageToAI(content)
                }
            }
        }
    }

    private fun sendMessageToAI(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = """{"model": "llama3.2", "prompt": "$message", "stream": false}"""
                val body = json.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("http://10.0.2.2:11434/api/generate") // 替换为实际的 Ollama 服务器 URL
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // 打印响应状态码和内容
                println("Response Code: ${response.code}")
                println("Response Body: $responseBody")  // 打印响应内容

                val aiResponse = responseBody?.let { parseAIResponse(it) }

                withContext(Dispatchers.Main) {
                    if (aiResponse != null) {
                        val msg = Msg(aiResponse, Msg.TYPE_RECEIVED)
                        msgList.add(msg)
                        adapter?.notifyItemInserted(msgList.size - 1)
                        recyclerView.scrollToPosition(msgList.size - 1)
                    } else {
                        // 如果 AI 回复为空，显示错误消息
                        val msg = Msg("Failed to get AI response", Msg.TYPE_RECEIVED)
                        msgList.add(msg)
                        adapter?.notifyItemInserted(msgList.size - 1)
                        recyclerView.scrollToPosition(msgList.size - 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // 显示网络错误消息
                    val msg = Msg("Network error: ${e.message}", Msg.TYPE_RECEIVED)
                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    recyclerView.scrollToPosition(msgList.size - 1)
                }
            }
        }
    }

    private fun parseAIResponse(response: String): String {
        return try {
            val jsonObject = JSONObject(response)
            if (jsonObject.has("response")) {
                jsonObject.getString("response")
            } else {
                "Unexpected response format: no 'response' key"
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            "JSON parsing error: ${e.message}"
        } catch (e: Exception) {
            e.printStackTrace()
            "General error: ${e.message}"
        }.also { println("Parsed response: $it") }
    }

    private fun initMsg() {
        val msg1 = Msg("Hello! How can I assist you today?", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
    }
}