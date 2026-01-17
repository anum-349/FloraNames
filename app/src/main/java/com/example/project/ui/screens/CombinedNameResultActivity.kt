package com.example.project.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R
import android.app.ActivityOptions

class CombinedNameResultActivity : BaseActivity() {

    private lateinit var topNameText: TextView
    private lateinit var topNameMeaning: TextView
    private lateinit var allNamesLayout: LinearLayout
    private lateinit var backButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combined_name_result)

        topNameText = findViewById(R.id.topNameText)
        topNameMeaning = findViewById(R.id.topNameMeaning)
        allNamesLayout = findViewById(R.id.allNamesLayout)
        backButton = findViewById(R.id.generateButton)

        // Get data from intent
        val nameIds = intent.getStringArrayExtra("name_ids") ?: arrayOf()
        val names = intent.getStringArrayExtra("names") ?: arrayOf()
        val meanings = intent.getStringArrayExtra("meanings") ?: arrayOf()
        val isGeneratedArray =
            intent.getBooleanArrayExtra("isGenerated") ?: BooleanArray(names.size) { true }

        if (names.isNotEmpty() && meanings.isNotEmpty()) {
            // Top recommendation (first name in the list)
            topNameText.text = names[0]
            topNameMeaning.text = meanings[0]

            // Display all names
            val isGeneratedArray =
                intent.getBooleanArrayExtra("isGenerated") ?: BooleanArray(names.size) { true }

            for (i in names.indices) {
                val name = names[i]
                val meaning = meanings[i]
                val generated = isGeneratedArray[i]

                val itemLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(20, 20, 20, 20)
                    setBackgroundResource(R.drawable.card)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 0, 0, 15)
                    layoutParams = params
                }

                val nameText = TextView(this).apply {
                    text = name
                    textSize = 18f
                    setTextColor(resources.getColor(R.color.colorTextPrimary, null))
                }

                val meaningText = TextView(this).apply {
                    text = meaning
                    textSize = 14f
                    setTextColor(resources.getColor(R.color.colorTextSecondary, null))
                }

                itemLayout.addView(nameText)
                itemLayout.addView(meaningText)

                // 🔹 Click listener
                itemLayout.setOnClickListener {
                    if (isGeneratedArray[i]) {
                        Toast.makeText(
                            this,
                            "This name is machine-generated, cannot view details",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(this, NameDetailsActivity::class.java)
                        intent.putExtra("name_id", nameIds[i].toInt()) // pass DB ID
                        startActivity(intent)
                    }
                }

                allNamesLayout.addView(itemLayout)
            }
        }

        backButton.setOnClickListener {
            finish() // Go back to previous page
        }
    }

    private fun goToResultPage(
        nameList: List<Pair<String, String>>,
        isGeneratedList: List<Boolean>
    ) {
        val intent = Intent(this, CombinedNameResultActivity::class.java)

        val names = nameList.map { it.first }.toTypedArray()
        val meanings = nameList.map { it.second }.toTypedArray()
        val isGenerated = isGeneratedList.toBooleanArray() // true if machine generated

        intent.putExtra("names", names)
        intent.putExtra("meanings", meanings)
        intent.putExtra("isGenerated", isGenerated)

        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
    }

}
