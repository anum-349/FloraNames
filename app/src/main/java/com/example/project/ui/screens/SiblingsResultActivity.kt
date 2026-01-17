package com.example.project.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R

class SiblingsResultActivity : BaseActivity() {

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

        val ids = intent.getIntArrayExtra("ids") ?: intArrayOf()
        val names = intent.getStringArrayExtra("names") ?: arrayOf()
        val meanings = intent.getStringArrayExtra("meanings") ?: arrayOf()

        if (names.isNotEmpty() && meanings.isNotEmpty() && ids.isNotEmpty()) {
            // Top recommendation
            topNameText.text = names[0]
            topNameMeaning.text = meanings[0]

            // Show all names
            for (i in names.indices) {
                val name = names[i]
                val meaning = meanings[i]
                val id = ids[i]

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
                allNamesLayout.addView(itemLayout)

                // Click listener
                itemLayout.setOnClickListener {
                    if (id != -1) {
                        // Existing name: open NameDetailActivity
                        val intent = Intent(this, NameDetailsActivity::class.java)
                        intent.putExtra("name_id", id)
                        startActivity(intent)
                    } else {
                        // Machine-generated name
                        Toast.makeText(this, "This name is machine-generated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        backButton.setOnClickListener { finish() }
    }
}
