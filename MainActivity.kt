package com.example.billingbudpoe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.login).setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        findViewById<Button>(R.id.signup).setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }
}