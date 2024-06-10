package com.example.aihelper

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var etUrl: EditText
    private lateinit var etModelName: EditText
    private lateinit var etUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        etUrl = findViewById(R.id.etUrl)
        etModelName = findViewById(R.id.etModelName)
        etUsername = findViewById(R.id.etUsername)
        val btnSave: Button = findViewById(R.id.btnSave)

        loadSettings()

        btnSave.setOnClickListener {
            saveSettings()
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun saveSettings() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("MODEL_URL", etUrl.text.toString())
            putString("MODEL_NAME", etModelName.text.toString())
            putString("USERNAME", etUsername.text.toString())
            apply()
        }
    }

    private fun loadSettings() {
        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE) ?: return
        etUrl.setText(sharedPref.getString("MODEL_URL", "http://10.0.2.2:11434/"))
        etModelName.setText(sharedPref.getString("MODEL_NAME", "llama3"))
        etUsername.setText(sharedPref.getString("USERNAME", "User"))
    }
}
