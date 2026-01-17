package com.example.project.ui.screens

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.project.R
import com.example.project.dao.NumerologyDao

class NumberologyResultActivity : BaseActivity() {

    private lateinit var dao: NumerologyDao

    private lateinit var nameTitle: TextView
    private lateinit var personalityText: TextView
    private lateinit var strengthsText: TextView
    private lateinit var luckyText: TextView
    private lateinit var matchingNamesText: TextView
    private lateinit var recommendationText: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numberology_result)

        dao = NumerologyDao(this)

        // Initialize views
        nameTitle = findViewById(R.id.nameTitle)
        personalityText = findViewById(R.id.personalityText)
        strengthsText = findViewById(R.id.strengthsText)
        luckyText = findViewById(R.id.luckyText)
        matchingNamesText = findViewById(R.id.matchingNames)
        recommendationText = findViewById(R.id.recommendation)
        backButton = findViewById(R.id.backButton)

        // Get numerology number from intent
        val numerologyNumber = intent.getIntExtra("NUMBER", 1)
        nameTitle.text = "Numerology Number: $numerologyNumber"

        // Fetch numerology info from DB
        val cursor = dao.getNumerologyByLifePathNumber(numerologyNumber)
        cursor.use { c ->
            if (c.moveToFirst()) {
                val personality = c.getString(c.getColumnIndexOrThrow("strength"))
                val weaknesses = c.getString(c.getColumnIndexOrThrow("weakness"))
                val career = c.getString(c.getColumnIndexOrThrow("career_suggestion"))
                val relationship = c.getString(c.getColumnIndexOrThrow("relationship_style"))
                val spiritual = c.getString(c.getColumnIndexOrThrow("spiritual_message"))
                val matchingNamesList = c.getString(c.getColumnIndexOrThrow("matching_names"))?.split(",") ?: emptyList()

                personalityText.text = personality
                strengthsText.text = weaknesses
                luckyText.text = career
                matchingNamesText.text = matchingNamesList.joinToString("\n") { "• $it" }
                recommendationText.text =
                    "🌟 Recommended Name: ${matchingNamesList.firstOrNull() ?: "No Name"}\n" +
                            "Relationship Style: $relationship\nSpiritual Message: $spiritual"
            } else {
                personalityText.text = "No data found"
                strengthsText.text = "-"
                luckyText.text = "-"
                matchingNamesText.text = "-"
                recommendationText.text = "-"
            }
        }

        backButton.setOnClickListener { finish() }
    }
}
