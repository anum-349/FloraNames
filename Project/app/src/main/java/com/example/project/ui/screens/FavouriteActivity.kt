package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.project.R
import com.example.project.dao.FavoriteDao

class FavouriteActivity : BaseActivity() {

    private lateinit var favoritesContainer: LinearLayout
    private lateinit var dao: FavoriteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)
        setBottomNavActive(R.id.navFavorites)
        favoritesContainer = findViewById(R.id.favoritesContainer)
        dao = FavoriteDao(this)

        loadFavoriteNames()
    }

    private fun loadFavoriteNames() {
        favoritesContainer.removeAllViews()

        val cursor = dao.getFavoriteNames()

        cursor.use { c ->
            if (!c.moveToFirst()) {
                return
            }

            do {
                val favId = c.getInt(c.getColumnIndexOrThrow("fav_id"))
                val nameId = c.getInt(c.getColumnIndexOrThrow("name_id"))
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val meaning = c.getString(c.getColumnIndexOrThrow("meaning_eng"))

                val card = layoutInflater.inflate(
                    R.layout.item_favourite_name,
                    favoritesContainer,
                    false
                )

                val tvName = card.findViewById<TextView>(R.id.tvFavoriteName)
                val tvMeaning = card.findViewById<TextView>(R.id.tvFavoriteMeaning)
                val btnRemove = card.findViewById<ImageView>(R.id.btnRemove)

                tvName.text = name
                tvMeaning.text = meaning

                // 🔹 OPEN NAME DETAILS
                card.setOnClickListener {
                    val intent = Intent(this, NameDetailsActivity::class.java)
                    intent.putExtra("name_id", nameId)

                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    startActivity(intent, options.toBundle())
                }

                btnRemove.setOnClickListener {
                    dao.removeFromFavorite(nameId)
                    favoritesContainer.removeView(card)
                }

                favoritesContainer.addView(card)

            } while (c.moveToNext())
        }
    }
}
