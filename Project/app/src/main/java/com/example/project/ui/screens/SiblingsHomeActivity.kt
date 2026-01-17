package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R
import com.example.project.dao.NameDao

class SiblingsHomeActivity : AppCompatActivity() {

    private lateinit var sibling1Input: EditText
    private lateinit var sibling2Input: EditText
    private lateinit var generateButton: AppCompatButton
    private lateinit var nameDao: NameDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siblings_home)

        sibling1Input = findViewById(R.id.sibling1Input)
        sibling2Input = findViewById(R.id.sibling2Input)
        generateButton = findViewById(R.id.generateButton)
        nameDao = NameDao(this)

        generateButton.setOnClickListener {
            val sibling1 = sibling1Input.text.toString().trim()
            val sibling2 = sibling2Input.text.toString().trim()

            if (sibling1.isEmpty() && sibling2.isEmpty()) {
                Toast.makeText(this, "Please enter at least one name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Fetch existing names from DB
            val existingNames = mutableListOf<Triple<Int, String, String>>() // id, name, meaning
            val cursor = nameDao.getAllNames()
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow("id"))
                    val name = it.getString(it.getColumnIndexOrThrow("name"))
                    val meaning = it.getString(it.getColumnIndexOrThrow("meaning_eng"))
                    existingNames.add(Triple(id, name, meaning))
                }
            }

            // Generate twinning names
            val generatedNames = generateTwinningNames(sibling1, sibling2)
            val generatedTriples = generatedNames.map { Triple(null, it, "Machine-generated") } // null ID for machine-generated

            // Merge with existing names and remove duplicates by name
            val finalList = (existingNames + generatedTriples).distinctBy { it.second }

            goToResultPage(finalList)
        }
    }

    // Generate Twinning Names Logic
    private fun generateTwinningNames(name1: String, name2: String): List<String> {
        val results = mutableListOf<String>()

        if (name1.isNotEmpty() && name2.isNotEmpty()) {
            val n1First = name1.take(3)
            val n2Last = name2.takeLast(3)
            results.add((n1First + n2Last).replaceFirstChar { it.uppercase() })
            results.add((n2Last + n1First).replaceFirstChar { it.uppercase() })
        } else if (name1.isNotEmpty()) {
            results.add(name1 + "a")
            results.add(name1 + "o")
            results.add(name1 + "y")
        } else if (name2.isNotEmpty()) {
            results.add(name2 + "a")
            results.add(name2 + "o")
            results.add(name2 + "y")
        }

        return results
    }

    // Pass data to Result Page
    private fun goToResultPage(nameList: List<Triple<Int?, String, String>>) {
        val intent = Intent(this, SiblingsResultActivity::class.java)

        val ids = nameList.map { it.first ?: -1 }.toIntArray() // -1 for machine-generated
        val names = nameList.map { it.second }.toTypedArray()
        val meanings = nameList.map { it.third }.toTypedArray()

        intent.putExtra("ids", ids)
        intent.putExtra("names", names)
        intent.putExtra("meanings", meanings)

        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
    }
}
