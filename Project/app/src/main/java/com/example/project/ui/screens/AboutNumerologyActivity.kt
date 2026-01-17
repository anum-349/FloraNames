package com.example.project.ui.screens

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.project.R

class AboutNumerologyActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_numerology)
        loadInfoCards()
    }

    private fun loadInfoCards() {
        val cards = listOf(
            InfoCard(
                "What is Numerology?",
                "Numerology is the study of numbers and their influence on personality, destiny, and life events."
            ),
            InfoCard(
                "What is Numerology Calculator?",
                "A numerology calculator uses your name and birth date to find important numbers that describe personality and life path.",
            ),
            InfoCard(
                "Benefits of Numerology",
                """
        • Helps understand personality  
        • Supports better name selection  
        • Improves compatibility insights  
        • Guides meaningful decisions
        """.trimIndent()            ),
            InfoCard(
                "What is Life Path Number?",
                "Calculated from birth date. Shows life purpose, challenges, and natural abilities.",
            ),
            InfoCard(
                "What is Destiny Number?",
                "Calculated from full name. Represents life goals, talents, and future direction.",
            ),
            InfoCard(
                "How Life Path Number is Calculated?",
                "Add birth date digits and reduce to a single digit (except 11, 22, 33).",
            ),
            InfoCard(
                "What is Soul Urge Number?",
                "Calculated from vowels in the name. Reflects inner desires and emotional needs.",
            ),
            InfoCard(
                "What is Personality Number?",
                "Calculated from consonants in the name. Shows how others perceive you.",
            ),
            InfoCard(
                "What are Master Numbers?",
                "Numbers 11, 22, and 33 are master numbers and are not reduced further."
            )
        )

        val container = findViewById<LinearLayout>(R.id.cardContainer)

        cards.forEach { card ->
            val view = layoutInflater.inflate(R.layout.item_info_card, container, false)

            view.findViewById<TextView>(R.id.tvTitle).text = card.title
            view.findViewById<TextView>(R.id.tvDescription).text = card.description

            container.addView(view)
        }
    }
}
