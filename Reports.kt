package com.example.billingbudpoe

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class Reports : AppCompatActivity() {
    private lateinit var menuIcon: ImageView
    private lateinit var chartContainer: LinearLayout
    private lateinit var monthSpinner: Spinner
    private lateinit var inputIncome: EditText
    private lateinit var inputExpense: EditText
    private lateinit var applyButton: Button

    data class ChartData(val month: String, var income: Float, var expenses: Float) {
        val total: Float
            get() = income - expenses
    }

    private val chartDataMap = mutableMapOf(
        "January" to ChartData("January", 50000f, 28000f),
        "February" to ChartData("February", 30000f, 25000f),
        "March" to ChartData("March", 50000f, 19000f),
        "April" to ChartData("April", 50000f, 50000f),
        "May" to ChartData("May", 50000f, 50000f),
        "June" to ChartData("June", 50000f, 19000f)

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize views
        chartContainer = findViewById(R.id.chartContainer)
        monthSpinner = findViewById(R.id.monthSpinner)
        inputIncome = findViewById(R.id.inputIncome)
        inputExpense = findViewById(R.id.inputExpense)
        applyButton = findViewById(R.id.applyButton)
        menuIcon = findViewById(R.id.imageView4)

        val monthList = chartDataMap.keys.toList()
        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monthList)

        // Display chart for selected month
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = monthList[position]
                val selectedData = chartDataMap[selectedMonth]!!
                inputIncome.setText(selectedData.income.toInt().toString())
                inputExpense.setText(selectedData.expenses.toInt().toString())
                updateChart(selectedData)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Apply changes on button click
        applyButton.setOnClickListener {
            val selectedMonth = monthSpinner.selectedItem.toString()
            val income = inputIncome.text.toString().toFloatOrNull() ?: 0f
            val expenses = inputExpense.text.toString().toFloatOrNull() ?: 0f

            val updatedData = ChartData(selectedMonth, income, expenses)
            chartDataMap[selectedMonth] = updatedData
            updateChart(updatedData)
        }

        // Menu logic
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

    private fun updateChart(data: ChartData) {
        chartContainer.removeAllViews()

        val title = TextView(this).apply {
            text = "REPORT FOR - ${data.month.uppercase()}"
            textSize = 24f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
            setPadding(0, 30, 0, 30)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        chartContainer.addView(title)

        val rowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 10, 0, 30)
        }

        val pieChart = PieChart(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 600, 1f)
        }

        val entries = arrayListOf(
            PieEntry(data.income, "Income"),
            PieEntry(data.expenses, "Expenses")
        )

        val dataSet = PieDataSet(entries, null).apply {
            sliceSpace = 3f
            selectionShift = 5f
            colors = listOf(Color.parseColor("#9C27B0"), Color.parseColor("#CE93D8"))
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(14f)
            setValueTypeface(Typeface.DEFAULT_BOLD)
            setValueTextColor(Color.WHITE)
        }

        pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(false)
            setRotationAngle(0f)
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1000, Easing.EaseInOutQuad)
            legend.isEnabled = false
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            this.data = pieData
            invalidate()
        }

        val label = TextView(this).apply {
            text = "Income: R${data.income.toInt()}\nExpenses: R${data.expenses.toInt()}\nTotal: R${data.total.toInt()}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.DKGRAY)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        rowLayout.addView(pieChart)
        rowLayout.addView(label)
        chartContainer.addView(rowLayout)
    }
}