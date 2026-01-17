package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R
import com.example.project.dao.NameDao

class NameCombinerActivity : BaseActivity() {

    private lateinit var dao: NameDao
    private lateinit var fatherNameInput: EditText
    private lateinit var motherNameInput: EditText
    private lateinit var generateBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_combiner)

        dao = NameDao(this)

        fatherNameInput = findViewById(R.id.fatherNameInput)
        motherNameInput = findViewById(R.id.motherNameInput)
        generateBtn = findViewById(R.id.combineButton)

        generateBtn.setOnClickListener {
            val father = fatherNameInput.text.toString().trim()
            val mother = motherNameInput.text.toString().trim()

            if (father.isEmpty() || mother.isEmpty()) {
                Toast.makeText(this, "Please enter both names", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate new names
            val generated = generateNameCombinations(father, mother)

            // Get matching existing names from DB
            val matchingExisting = findMatchingExistingNames(father, mother)

// Convert to a unified list of (id, name, meaning)
            val allNames = mutableListOf<Triple<Int?, String, String>>()
            allNames.addAll(matchingExisting.map { Triple(it.first, it.second, it.third) })
            allNames.addAll(generated.map { Triple(null, it.first, it.second) }) // generated have null IDs

// Flags for generated names
            val isGeneratedList = mutableListOf<Boolean>()
            isGeneratedList.addAll(List(matchingExisting.size) { false })
            isGeneratedList.addAll(List(generated.size) { true })

            // Send to next page
            goToResultPage(allNames, isGeneratedList)
        }
    }

    // Generate combined names
    private fun generateNameCombinations(father: String, mother: String): List<Pair<String, String>> {
        val fatherPart = father.take(3)
        val motherPart = mother.takeLast(3)

        val combination1 = (fatherPart + motherPart).replaceFirstChar { it.uppercase() }
        val combination2 = (motherPart + fatherPart).replaceFirstChar { it.uppercase() }
        val combination3 = (father.take(2) + mother.take(2)).replaceFirstChar { it.uppercase() }

        return listOf(
            combination1 to "Combined name from parents",
            combination2 to "Combined name from parents",
            combination3 to "Combined name from parents"
        )
    }

    // Use Triple<nameId, name, meaning> for DB names
    private fun findMatchingExistingNames(father: String, mother: String): List<Triple<Int, String, String>> {
        val result = mutableListOf<Triple<Int, String, String>>()
        val cursor = dao.getMatchingNames(father, mother)

        cursor.use { c ->
            while (c.moveToNext()) {
                val id = c.getInt(c.getColumnIndexOrThrow("id")) // <--- DB name ID
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val meaning = c.getString(c.getColumnIndexOrThrow("meaning_eng"))
                result.add(Triple(id, name, meaning))
            }
        }

        return result
    }

    // Pass to next activity
    private fun goToResultPage(
        nameList: List<Triple<Int?, String, String>>,
        isGeneratedList: List<Boolean>
    ) {
        val intent = Intent(this, CombinedNameResultActivity::class.java)

        val nameIds = nameList.map { it.first?.toString() ?: "" }.toTypedArray() // null → empty
        val names = nameList.map { it.second }.toTypedArray()
        val meanings = nameList.map { it.third }.toTypedArray()
        val isGenerated = isGeneratedList.toBooleanArray()

        intent.putExtra("name_ids", nameIds)
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
