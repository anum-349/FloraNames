package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.project.R

class CategoriesActivity : BaseActivity() {

    private lateinit var cardBoys: CardView
    private lateinit var cardGirls: CardView
    private lateinit var cardQuranic: CardView
    private lateinit var cardProphet: CardView
    private lateinit var cardUnique: CardView
    private lateinit var cardModern: CardView
    private lateinit var cardShort: CardView
    private lateinit var cardLong: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories) // XML you provided

        // Initialize CardViews
        cardBoys = findViewById(R.id.cardBoys)
        cardGirls = findViewById(R.id.cardGirls)
        cardQuranic = findViewById(R.id.cardQuranic)
        cardProphet = findViewById(R.id.cardProphet)
        cardUnique = findViewById(R.id.cardUnique)
        cardModern = findViewById(R.id.cardModern)
        cardShort = findViewById(R.id.cardShort)
        cardLong = findViewById(R.id.cardLong)

        // Set click listeners
        cardBoys.setOnClickListener { openCategory(1) }
        cardGirls.setOnClickListener { openCategory(2) }
        cardQuranic.setOnClickListener { openCategory(3) }
        cardProphet.setOnClickListener { openCategory(4) }
        cardUnique.setOnClickListener { openCategory(5) }
        cardModern.setOnClickListener { openCategory(6) }
        cardShort.setOnClickListener { openCategory(7) }
        cardLong.setOnClickListener { openCategory(8) }
    }

    // Function to open CategoryDetailActivity and pass the selected category
    private fun openCategory(categoryId: Int) {
        val intent = Intent(this, CategoryDetailActivity::class.java)
        intent.putExtra("category", categoryId)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
    }
}
