package com.example.billingbudpoe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    private lateinit var incomeButton: Button
    private lateinit var expenseButton: Button
    private lateinit var investmentButton: Button
    private lateinit var reportButton: Button
    private lateinit var settingsButton: Button
    private lateinit var logoutButton: Button
    private lateinit var setBalanceButton: Button
    private lateinit var menuIcon: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var balanceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        incomeButton = findViewById(R.id.income)
        expenseButton = findViewById(R.id.expenses)
        investmentButton = findViewById(R.id.investment)
        reportButton = findViewById(R.id.report)
        settingsButton = findViewById(R.id.settings)
        logoutButton = findViewById(R.id.logout)
        setBalanceButton = findViewById(R.id.setBalance)
        menuIcon = findViewById(R.id.imageView4)
        fullNameTextView = findViewById(R.id.fullName)
        balanceTextView = findViewById(R.id.balance)

        // Show user's display name
        val user = FirebaseAuth.getInstance().currentUser
        fullNameTextView.text = user?.displayName ?: "User"

        // Navigation buttons
        incomeButton.setOnClickListener {
            startActivity(Intent(this, Income::class.java))
        }

        expenseButton.setOnClickListener {
            startActivity(Intent(this, Expense::class.java))
        }

        investmentButton.setOnClickListener {
            startActivity(Intent(this, Investment::class.java))
        }

        reportButton.setOnClickListener {
            startActivity(Intent(this, Reports::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Set Balance dialog
        setBalanceButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.activity_set_balance, null)
            val editBalance = dialogView.findViewById<EditText>(R.id.editBalance)
            val saveButton = dialogView.findViewById<Button>(R.id.btnSaveBalance)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            saveButton.setOnClickListener {
                val input = editBalance.text.toString().trim()
                if (input.isNotEmpty()) {
                    val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("user_balance", input)
                        apply()
                    }
                    Toast.makeText(this, "Balance saved", Toast.LENGTH_SHORT).show()
                    updateBalanceDisplay()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }

        // Popup menu
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

    override fun onResume() {
        super.onResume()
        updateBalanceDisplay()
    }

    private fun updateBalanceDisplay() {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val savedBalance = sharedPref.getString("user_balance", "0.00")
        balanceTextView.text = "R $savedBalance"
    }
}

