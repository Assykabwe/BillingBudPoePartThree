package com.example.billingbudpoe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity

class FAQActivity : AppCompatActivity() {
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqactivity)

        // Initialize views
        val backIcon: ImageView = findViewById(R.id.imageView3)
        menuIcon = findViewById(R.id.imageView4) // Make sure this matches the menu icon ID
        // Handle back button click
        backIcon.setOnClickListener {
            finish()
        }
        // Menu icon click opens popup
        menuIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, menuIcon)
            popupMenu.menuInflater.inflate(R.menu.category_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_income -> startActivity(Intent(this, Income::class.java))
                    R.id.menu_expense -> startActivity(Intent(this, Expense::class.java))
                    R.id.menu_investment -> startActivity(Intent(this, Investment::class.java))
                    R.id.menu_reports -> startActivity(Intent(this, Reports::class.java))
                    R.id.menu_settings -> startActivity(Intent(this, Settings::class.java))
                }
                true
            }

            popupMenu.show()
        }
    }
}