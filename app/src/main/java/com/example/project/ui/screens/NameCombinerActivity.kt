package com.example.project.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R

class NameCombinerActivity : BaseActivity() {

    private lateinit var fatherNameInput: EditText
    private lateinit var motherNameInput: EditText
    private lateinit var generateBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_combiner)

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

            // 🔑 Navigate to Category Detail Page
            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("categoryName", "Parent Names")
            intent.putExtra("name1", father)
            intent.putExtra("name2", mother)

            startActivity(intent)
        }
    }
}
