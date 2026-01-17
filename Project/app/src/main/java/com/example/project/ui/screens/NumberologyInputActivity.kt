package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R

class NumberologyInputActivity : AppCompatActivity() {

    private lateinit var dayDropdown: AutoCompleteTextView
    private lateinit var monthDropdown: AutoCompleteTextView
    private lateinit var yearDropdown: AutoCompleteTextView
    private lateinit var generateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numberology_input)

        dayDropdown = findViewById(R.id.dayDropdown)
        monthDropdown = findViewById(R.id.monthDropdown)
        yearDropdown = findViewById(R.id.yearDropdown)
        generateButton = findViewById(R.id.generateButton)

        setupDropdowns()
        setupClickListener()
    }

    private fun setupDropdowns() {

        // Day 1–31
        val days = (1..31).map { it.toString() }
        dayDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, days))

        // Month Names
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, months))

        // Years 1950–2030
        val years = (1950..2030).map { it.toString() }
        yearDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, years))
    }

    private fun setupClickListener() {

        generateButton.setOnClickListener {

            val day = dayDropdown.text.toString()
            val month = monthDropdown.text.toString()
            val year = yearDropdown.text.toString()

            if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Please select complete Date of Birth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert month name → month number
            val monthNumber = getMonthNumber(month)

            // Final date: yyyyMMdd
            val dobString = year + padNumber(monthNumber) + padNumber(day.toInt())

            val numerologyNumber = calculateLifePathNumber(dobString)

            // send to result page
            val intent = Intent(this, NumberologyResultActivity::class.java)
            intent.putExtra("NUMBER", numerologyNumber)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())        }
    }

    private fun getMonthNumber(month: String): Int {
        return when (month) {
            "January" -> 1
            "February" -> 2
            "March" -> 3
            "April" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "August" -> 8
            "September" -> 9
            "October" -> 10
            "November" -> 11
            "December" -> 12
            else -> 1
        }
    }

    // Always format as 01, 02, 03, ...
    private fun padNumber(num: Int): String = if (num < 10) "0$num" else num.toString()

    /**
     * NUMEROLOGY RULE:
     * Sum digits → reduce to single digit
     * Exception: master numbers 11, 22, 33 must remain
     */
    private fun calculateLifePathNumber(dob: String): Int {

        var sum = dob.map { it.toString().toInt() }.sum()

        // Keep reducing until single digit or master number
        while (!(sum == 11 || sum == 22 || sum == 33 || sum < 10)) {
            sum = sum.toString().map { it.toString().toInt() }.sum()
        }

        return sum
    }
}
