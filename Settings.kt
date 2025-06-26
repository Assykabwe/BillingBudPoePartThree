package com.example.billingbudpoe

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings : AppCompatActivity() {
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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

        // Language Spinner
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        val languages = arrayOf(
            "English", "Afrikaans", "Zulu", "Xhosa", "Sesotho", "Tswana",
            "Swahili", "Portuguese", "French", "Arabic", "Spanish", "Hindi",
            "Mandarin", "Shona", "Xitsonga"
        )
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        languageSpinner.adapter = languageAdapter
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, "Language set to: ${languages[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Currency Spinner
        val currencySpinner: Spinner = findViewById(R.id.currencySpinner)
        val currencies = arrayOf(
            "Rands (ZAR)", "Dollars (USD)", "Euros (EUR)", "Pounds (GBP)", "Yen (JPY)",
            "Naira (NGN)", "Shillings (KES)", "Francs (XOF)", "Rupees (INR)", "Canadian Dollar (CAD)",
            "Australian Dollar (AUD)", "Swiss Franc (CHF)", "Yuan (CNY)", "Dirham (AED)", "Peso (MXN)"
        )
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        currencySpinner.adapter = currencyAdapter
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, "Currency set to: ${currencies[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Notification Switch
        val notificationSwitch: Switch = findViewById(R.id.notificationSwitch)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        // MFA
        val mfaText: TextView = findViewById(R.id.mfaText)
        mfaText.setOnClickListener {
            Toast.makeText(this, "Multi-factor authentication clicked", Toast.LENGTH_SHORT).show()
        }

        // Helpline -> Opens FAQActivity
        val helplineText: TextView = findViewById(R.id.helplineText)
        helplineText.setOnClickListener {
            val faqIntent = Intent(this, FAQActivity::class.java)
            startActivity(faqIntent)
        }

        // Share
        val shareText: TextView = findViewById(R.id.shareText)
        shareText.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this amazing budgeting app!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }
}