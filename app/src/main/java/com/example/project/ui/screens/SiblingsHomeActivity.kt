package com.example.project.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project.R
import com.example.project.dao.NameDao

class SiblingsHomeActivity : AppCompatActivity() {

    private lateinit var sibling1Input: EditText
    private lateinit var generateButton: AppCompatButton
    private lateinit var nameDao: NameDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siblings_home)

        sibling1Input = findViewById(R.id.sibling1Input)
        generateButton = findViewById(R.id.generateButton)
        nameDao = NameDao(this)

        generateButton.setOnClickListener {
            val sibling1 = sibling1Input.text.toString().trim()

            if (sibling1.isEmpty()) {
                Toast.makeText(this, "Please enter at least one name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("categoryName", "Name Twining")
            intent.putExtra("name1", sibling1)

            startActivity(intent)
        }
    }
}
