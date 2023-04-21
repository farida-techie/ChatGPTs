package com.malkinfo.chatgpts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val API_KEY = "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"
    lateinit var recyclerView: RecyclerView
    lateinit var welcomeText :TextView
    lateinit var messageEditText:EditText
    lateinit var sendButton:ImageButton
    lateinit var messageList:MutableList<Message>
    lateinit var messageAdapter:MessageAdapter
    val client = OkHttpClient()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messageList = ArrayList()
        recyclerView = findViewById(R.id.recycler_view)
        welcomeText = findViewById(R.id.welcome_text)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_bt)
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        val layoutManger = LinearLayoutManager(this)
        layoutManger.stackFromEnd = true
        recyclerView.layoutManager = layoutManger

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim{ it <= ' '}
            addToChat(question,Message.SENT_BY_ME)
            messageEditText.setText("")
            callAPI(question)
            welcomeText.visibility = View.GONE
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread{
            messageList.add(Message(message,sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }

    }

    fun addResponse(response:String?){
        messageList.removeAt(messageList.size -1)
        addToChat(response!!,Message.SENT_BY_BOT)

    }

    private fun callAPI(question: String) {
        //call okhttp
        messageList.add(Message("Typing...",Message.SENT_BY_BOT))
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        }catch (e:JSONException){
            e.printStackTrace()
        }
        val body :RequestBody = RequestBody.create(JSON,jsonBody.toString())
        val request:Request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    var jsonObject :JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim{it <= ' '})
                    }catch (e:JSONException){
                        e.printStackTrace()
                    }
                }else{
                    addResponse("Failed to load response due to ${response.body.toString()}")
                }
            }

        })

    }
    companion object{
        val JSON :MediaType = "application/json; charset=utf-8".toMediaType()
    }
}