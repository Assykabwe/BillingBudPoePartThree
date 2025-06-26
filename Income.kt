package com.example.billingbudpoe

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.app.DatePickerDialog
import android.content.Intent
import java.text.DateFormatSymbols
import java.util.*

class Income : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var incomeText: TextView
    private lateinit var expenseText: TextView
    private lateinit var minGoalEditText: EditText
    private lateinit var maxGoalEditText: EditText
    private lateinit var saveGoalsButton: Button
    private lateinit var budgetDetailButton: Button
    private lateinit var pieChart: PieChart
    private lateinit var datePickerText: TextView
    private lateinit var budgetDetailText: TextView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)

        balanceText = findViewById(R.id.balanceText)
        incomeText = findViewById(R.id.incomeText)
        expenseText = findViewById(R.id.expenseText)
        minGoalEditText = findViewById(R.id.minGoalEditText)
        maxGoalEditText = findViewById(R.id.maxGoalEditText)
        saveGoalsButton = findViewById(R.id.saveGoalsButton)
        budgetDetailButton = findViewById(R.id.budgetDetailButton)
        pieChart = findViewById(R.id.budgetChart)
        datePickerText = findViewById(R.id.datePickerText)
        budgetDetailText = findViewById(R.id.budgetDetailText)

        loadBudgetAndSetup()

        val backIcon: ImageView = findViewById(R.id.imageView3)
        menuIcon = findViewById(R.id.imageView4) // Make sure this matches the menu icon ID
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
        backIcon.setOnClickListener {
            finish()
        }

        saveGoalsButton.setOnClickListener {
            val minGoal = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoal = maxGoalEditText.text.toString().toDoubleOrNull()

            if (minGoal != null && maxGoal != null) {
                val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("min_goal", minGoal.toString())
                    putString("max_goal", maxGoal.toString())
                    apply()
                }
                Toast.makeText(this, "Goals saved!", Toast.LENGTH_SHORT).show()
                loadBudgetAndSetup()
            } else {
                Toast.makeText(this, "Enter valid numbers for both goals.", Toast.LENGTH_SHORT).show()
            }
        }

        budgetDetailButton.setOnClickListener {
            Toast.makeText(this, "Budget detail updated below.", Toast.LENGTH_SHORT).show()
        }

        val calendar = Calendar.getInstance()
        datePickerText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, _ ->
                val monthName = DateFormatSymbols().months[month]
                datePickerText.text = "$monthName $year"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }
    }

    private fun loadBudgetAndSetup() {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val balanceStr = sharedPref.getString("user_balance", "0.00") ?: "0.00"
        val minGoal = sharedPref.getString("min_goal", "0.00") ?: "0.00"
        val maxGoal = sharedPref.getString("max_goal", "0.00") ?: "0.00"

        val balance = balanceStr.toDoubleOrNull() ?: 0.0
        val income = balance / 2
        val expenses = balance / 2

        balanceText.text = "R ${"%,.2f".format(balance)}"
        incomeText.text = "Income: R ${"%,.2f".format(income)}"
        expenseText.text = "Expenses: R ${"%,.2f".format(expenses)}"

        budgetDetailText.text = """
            Budget: R ${"%,.2f".format(balance)}
            Income: R ${"%,.2f".format(income)}
            Expenses: R ${"%,.2f".format(expenses)}
            Min Goal: R ${minGoal}
            Max Goal: R ${maxGoal}
        """.trimIndent()

        setupPieChart(income, expenses)
    }

    private fun setupPieChart(income: Double, expenses: Double) {
        val entries = arrayListOf<PieEntry>()
        if (income > 0) entries.add(PieEntry(income.toFloat(), "Income"))
        if (expenses > 0) entries.add(PieEntry(expenses.toFloat(), "Expenses"))

        val dataSet = PieDataSet(entries, "Budget").apply {
            colors = listOf(
                android.graphics.Color.parseColor("#4CAF50"), // Green
                android.graphics.Color.parseColor("#F44336")  // Red
            )
            sliceSpace = 3f
            selectionShift = 5f
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.WHITE
        }

        pieChart.apply {
            data = PieData(dataSet)
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(android.graphics.Color.TRANSPARENT)
            setEntryLabelColor(android.graphics.Color.BLACK)
            setEntryLabelTextSize(12f)
            centerText = "Budget"
            setCenterTextSize(18f)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.textSize = 14f
            animateY(1000)
            invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBudgetAndSetup()
    }
}

