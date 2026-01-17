package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import  com.example.project.R
import androidx.core.content.ContextCompat
import com.example.project.dao.NameDao
import android.graphics.Typeface
import android.widget.GridLayout

class DashboardActivity : BaseActivity() {

    private lateinit var dao: NameDao
    private lateinit var cardNumberology: LinearLayout
    private lateinit var cardBoy: LinearLayout
    private lateinit var cardSibling: LinearLayout
    private lateinit var cardCategories: LinearLayout
    private lateinit var cardGirl: LinearLayout
    private lateinit var cardNameCombiner: LinearLayout
    private lateinit var recentContainer: GridLayout
    private lateinit var dayName: TextView
    private lateinit var dayMeaning: TextView
    private lateinit var btnViewAll: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setBottomNavActive(R.id.navHome)

        cardNumberology = findViewById(R.id.card_numberology)
        cardBoy = findViewById(R.id.card_boy)
        cardGirl = findViewById(R.id.card_girl)
        cardNameCombiner = findViewById(R.id.card_parent_name)
        cardCategories = findViewById(R.id.categories)
        cardSibling = findViewById(R.id.sibling)

        dayName = findViewById(R.id.dayName)
        dayMeaning = findViewById(R.id.dayMeaning)

        btnViewAll = findViewById(R.id.btnViewAll)

        recentContainer = findViewById(R.id.recentContainer)

        setDailyNames()
        setRecentNames()
        setupCards()
        setupViewAllButton()
    }

    private fun setDailyNames(){
        dao = NameDao(this)
        val cursor = dao.getDailyName()

        cursor.use { c ->
            while (c.moveToNext()) {
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val meaning = c.getString(c.getColumnIndexOrThrow("meaning_eng"))
                dayName.text = name
                dayMeaning.text = meaning
            }
        }
    }

    private fun setRecentNames() {
        dao = NameDao(this)
        recentContainer.removeAllViews()  // GridLayout container

        val cursor = dao.getRecentViewedNames(4)

        cursor.use { c ->
            while (c.moveToNext()) {
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val meaning = c.getString(c.getColumnIndexOrThrow("meaning_eng"))

                val itemLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(8, 8, 8, 8)
                    }
                    setBackgroundResource(R.drawable.card)
                }

                val tvName = TextView(this).apply {
                    text = name
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(R.color.colorTextPrimary)
                }

                val tvMeaning = TextView(this).apply {
                    text = "Meaning: $meaning"
                    textSize = 14f
                    setTextColor(R.color.colorTextSecondary)
                }

                itemLayout.addView(tvName)
                itemLayout.addView(tvMeaning)
                recentContainer.addView(itemLayout)
            }
        }
    }

    private fun setupCards() {
        cardNumberology.setOnClickListener {
            val intent = Intent(this, NumberologyHomeActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

        cardBoy.setOnClickListener {
            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("category", 1)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())        }

        cardGirl.setOnClickListener {
            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("category", 2)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())        }

        cardNameCombiner.setOnClickListener {
            val intent = Intent(this, NameCombinerActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

        cardCategories.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

        cardSibling.setOnClickListener {
            val intent = Intent(this, SiblingsHomeActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

    }

    private fun setupViewAllButton() {
        btnViewAll.setOnClickListener {
            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("category", -1)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
        }
    }

}
