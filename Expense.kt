package com.example.billingbudpoe

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class Expense : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private val expenseList = mutableListOf<ExpenseEntry>()
    private var photoBitmap: Bitmap? = null
    private lateinit var menuIcon: ImageView
    private lateinit var barChart: BarChart
    private lateinit var goalTextView: TextView
    private lateinit var expenseListView: ListView
    private lateinit var categorySpinner: Spinner
    private lateinit var badgeButton: Button
    private lateinit var badgeIcon: ImageView
    private lateinit var addExpenseButton: Button
    private lateinit var fromDateBtn: Button
    private lateinit var toDateBtn: Button
    private lateinit var dateRangeDisplay: TextView

    private var selectedStartDate: String = ""
    private var selectedEndDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        val backIcon: ImageView = findViewById(R.id.imageView3)
        menuIcon = findViewById(R.id.imageView4)
        badgeIcon = findViewById(R.id.badgeIcon)
        badgeButton = findViewById(R.id.badgeButton)
        barChart = findViewById(R.id.expenseBarChart)
        goalTextView = findViewById(R.id.goalTextView)
        expenseListView = findViewById(R.id.expenseListView)
        categorySpinner = findViewById(R.id.categorySpinner)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        fromDateBtn = findViewById(R.id.fromDateButton)
        toDateBtn = findViewById(R.id.toDateButton)
        dateRangeDisplay = findViewById(R.id.dateRangeDisplay)

        addExpenseButton.isEnabled = false

        backIcon.setOnClickListener { finish() }

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

        badgeButton.setOnClickListener {
            startActivity(Intent(this, BadgeHistory::class.java))
        }

        badgeIcon.setOnClickListener {
            startActivity(Intent(this, BadgeHistory::class.java))
        }

        val addCategoryButton: Button = findViewById(R.id.addCategoryButton)
        val photoButton: Button = findViewById(R.id.photoButton)

        val categories = mutableListOf("Food", "Travel", "Shopping", "Transport", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.minimumHeight = 48
        categorySpinner.setPadding(0, 24, 0, 24)

        addCategoryButton.setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Add Category")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val newCategory = input.text.toString()
                    if (newCategory.isNotEmpty()) {
                        categories.add(newCategory)
                        spinnerAdapter.notifyDataSetChanged()
                    }
                }.setNegativeButton("Cancel", null)
                .show()
        }

        addExpenseButton.setOnClickListener {
            if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "Please select both From and To dates first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amountInput = EditText(this)
            amountInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            amountInput.hint = "Amount (e.g. 150.00)"

            val descInput = EditText(this)
            descInput.hint = "Description (optional)"

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 40, 50, 10)
            layout.addView(amountInput)
            layout.addView(descInput)

            AlertDialog.Builder(this)
                .setTitle("Add Expense")
                .setView(layout)
                .setPositiveButton("Add") { _, _ ->
                    val amountStr = amountInput.text.toString()
                    val description = descInput.text.toString().takeIf { it.isNotBlank() }

                    val amount = amountStr.toFloatOrNull()
                    if (amount == null || amount <= 0f) {
                        Toast.makeText(this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val category = categorySpinner.selectedItem.toString()

                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateStr = sdf.format(Date())

                    val entry = ExpenseEntry(category, amount, dateStr, description)
                    expenseList.add(entry)

                    Toast.makeText(this, "Expense added.", Toast.LENGTH_SHORT).show()

                    updateExpenseListView()
                    maybeRenderChart()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        photoButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1001)
        }

        fromDateBtn.setOnClickListener {
            showDatePickerDialog { date ->
                selectedStartDate = date
                updateDateRangeDisplay()
                maybeRenderChart()
                checkEnableAddExpense()
            }
        }

        toDateBtn.setOnClickListener {
            showDatePickerDialog { date ->
                selectedEndDate = date
                updateDateRangeDisplay()
                maybeRenderChart()
                checkEnableAddExpense()
            }
        }
    }

    private fun checkEnableAddExpense() {
        addExpenseButton.isEnabled = selectedStartDate.isNotEmpty() && selectedEndDate.isNotEmpty()
    }

    private fun updateDateRangeDisplay() {
        if (::dateRangeDisplay.isInitialized) {
            if (selectedStartDate.isNotEmpty() && selectedEndDate.isNotEmpty()) {
                dateRangeDisplay.text = "Range: $selectedStartDate to $selectedEndDate"
            } else {
                dateRangeDisplay.text = ""
            }
        }
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(formattedDate)
        }
        DatePickerDialog(
            this,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateExpenseListView() {
        val displayList = expenseList.map {
            val descText = it.description?.let { d -> " - $d" } ?: ""
            "[${it.date}] ${it.category}: R${"%.2f".format(it.amount)}$descText"
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        expenseListView.adapter = adapter
    }

    private fun maybeRenderChart() {
        if (selectedStartDate.isNotEmpty() && selectedEndDate.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = sdf.parse(selectedStartDate)
            val endDate = sdf.parse(selectedEndDate)

            if (startDate == null || endDate == null) return
            if (startDate.after(endDate)) {
                Toast.makeText(this, "'From' date cannot be after 'To' date.", Toast.LENGTH_SHORT).show()
                return
            }

            val filteredExpenses = expenseList.filter {
                val expenseDate = sdf.parse(it.date)
                expenseDate != null && !expenseDate.before(startDate) && !expenseDate.after(endDate)
            }

            val categoryTotals = filteredExpenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount.toDouble() }.toFloat() }

            if (categoryTotals.isEmpty()) {
                barChart.clear()
                goalTextView.text = "No expenses in selected range"
                return
            }

            val categories = categoryTotals.keys.toList()
            val values = categoryTotals.values.toList()

            val minGoal = 500f
            val maxGoal = 3000f

            showExpenseBarChart(categories, values, minGoal, maxGoal)
        }
    }

    private fun showExpenseBarChart(categories: List<String>, values: List<Float>, minGoal: Float, maxGoal: Float) {
        val entries = values.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
        val dataSet = BarDataSet(entries, "Spending")
        dataSet.color = getColor(R.color.purple)

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(categories)
        barChart.xAxis.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f
        barChart.invalidate()

        goalTextView.text = "Min Goal: R${minGoal} | Max Goal: R${maxGoal}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            photoBitmap = data?.extras?.get("data") as Bitmap
            Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
        }
    }
}

data class ExpenseEntry(
    val category: String,
    val amount: Float,
    val date: String,
    val description: String? = null
)
