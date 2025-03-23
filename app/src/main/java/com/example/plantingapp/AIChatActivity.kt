package com.example.plantingapp

import android.os.Bundle
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIChatActivity : BaseActivity(), View.OnClickListener {
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
                "",MsgItem.TYPE_RECEIVED,MsgItem.STATUS_START_LOADING
            )
        )
        val pos = msgItemList.size - 1
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = """{"model": "deepseek-r1:1.5b", "prompt": "$message", "stream": false}"""
//                val json = """{"model": "llama 3.2", "prompt": "$message", "stream": false}"""

                val body = json.toRequestBody(mediaType)
                val request = Request.Builder()
//                    .url("http://10.0.2.2:11434/api/generate") // 替换为实际的 Ollama 服务器 URL
                    .url("http://172.20.10.4:11434/api/generate") // @wuzichen's API
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
                        recyclerView.scrollToPosition(pos)
                        adapter?.stopAnimator(pos,MsgItem.STATUS_LOAD_SUCCESSFULLY,aiResponse)
                    } else {
                        // 如果 AI 回复为空，显示错误消息
                        recyclerView.scrollToPosition(pos)
                        adapter?.stopAnimator(pos,MsgItem.STATUS_LOAD_FAILED,"未知错误，对话加载失败")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    recyclerView.scrollToPosition(pos)
                    adapter?.stopAnimator(pos,MsgItem.STATUS_LOAD_FAILED,"网络错误，对话加载失败")
                }
            }
            this@AIChatActivity.runOnUiThread{
                backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this@AIChatActivity,R.color.themeDarkGreen))
                send.isClickable = true
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
        val msgItem1 = MsgItem("你好，请问有什么我可以帮助您？", MsgItem.TYPE_RECEIVED)
        msgItemList.add(msgItem1)
    }

    private fun addMsg(item:MsgItem){
        when(item.type){
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
                backgroundSend.setCardBackgroundColor(ContextCompat.getColor(this,R.color.line_grey_wzc))
                sendMessageToAI(item.content)
            }
        }
    }

}