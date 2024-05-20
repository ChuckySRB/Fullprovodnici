package com.example.fullprovodnici

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.content.Intent

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        val button1: ImageButton = findViewById(R.id.button1)
        button1.setOnClickListener {
            val intent = Intent(this, RecordLogActivity::class.java)
            startActivity(intent)
        }

        val button3: ImageButton = findViewById(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

}
