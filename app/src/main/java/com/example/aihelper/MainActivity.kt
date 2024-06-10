package com.example.aihelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AIViewModel
    private lateinit var etInput: EditText
    private lateinit var btnSend: Button
    private lateinit var tvUsername: TextView
    private lateinit var chatAdapter: ChatAdapter
    private var lastMessageIndex: Int? = null
    private lateinit var modelName: String
    private lateinit var aiUrl: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = AIRepository()
        val factory = AIViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AIViewModel::class.java)

        etInput = findViewById(R.id.etInput)
        btnSend = findViewById(R.id.btnSend)
        tvUsername = findViewById(R.id.tvUsername)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)

        chatAdapter = ChatAdapter(mutableListOf())
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivityForResult(Intent(this, SettingsActivity::class.java), 1)
        }

        btnSend.setOnClickListener {
            if (btnSend.text == "Send") {
                val prompt = etInput.text.toString()
                chatAdapter.addMessage(ChatMessage("You: $prompt", true))
                etInput.text.clear()
                btnSend.text = "Stop"
                lastMessageIndex = null
                viewModel.sendPrompt(prompt, modelName)
                scrollToBottom()
            } else {
                viewModel.stopGeneration()
                btnSend.text = "Send"
            }
        }

        viewModel.response.observe(this, Observer { response ->
            if (lastMessageIndex == null) {
                lastMessageIndex = chatAdapter.itemCount
                chatAdapter.addMessage(ChatMessage("AI: $response", false))
            } else {
                chatAdapter.updateLastMessage(ChatMessage("AI: $response", false))
            }
            scrollToBottom()

            if (viewModel.isGenerationComplete) {
                btnSend.text = "Send"
            }
        })

        loadSettings()
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE) ?: return
        tvUsername.text = sharedPref.getString("USERNAME", "User")
        modelName = sharedPref.getString("MODEL", "llama3") ?: "llama3"
        aiUrl = sharedPref.getString("AI_URL", "http://10.0.2.2:11434/") ?: "http://10.0.2.2:11434/"
    }

    private fun scrollToBottom() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
        recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadSettings()
        }
    }
}

