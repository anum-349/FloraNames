package com.example.project.ui.screens

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.project.R

class NamingTipsActivity : BaseActivity() {

    private lateinit var containerLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naming_tips)

        containerLayout = findViewById(R.id.tipsContainer)

        // List of tips WITHOUT data class
        val tips = listOf(
            Pair(
                "How to Choose a Meaningful Name",
                "• Look for names with positive meanings.\n" +
                        "• Consider names of Prophets, righteous figures, or virtues.\n" +
                        "• Avoid names with negative or inappropriate meanings.\n" +
                        "• Keep the name simple and easy to pronounce."
            ),
            Pair(
                "What Meanings Matter",
                "• Spiritual meaning — reflects goodness or virtue.\n" +
                        "• Traits — courage, wisdom, kindness.\n" +
                        "• Aspirational qualities — hope or success.\n" +
                        "• Avoid meanings with negative connotations."
            ),
            Pair(
                "How Numerology Works",
                "• Each letter has a number.\n" +
                        "• Numbers sum to form a final value.\n" +
                        "• Some believe this influences personality.\n" +
                        "• Use numerology only as a simple guide."
            )
        )

        // Add Tips Dynamically
        tips.forEachIndexed { index, tip ->
            val (title, content) = tip

            val titleView = TextView(this).apply {
                text = title
                textSize = 18f
                setTextColor(ContextCompat.getColor(this@NamingTipsActivity, R.color.colorTextPrimary))
                setPadding(0, 0, 0, 8)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val contentView = TextView(this).apply {
                text = content
                textSize = 15f
                setTextColor(ContextCompat.getColor(this@NamingTipsActivity, R.color.colorTextSecondary))
            }

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundResource(
                    if (index % 2 == 0) R.drawable.card else R.drawable.card
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

                addView(titleView)
                addView(contentView)
            }

            containerLayout.addView(itemLayout)
        }
    }
}
