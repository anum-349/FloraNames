package com.example.project.ui.screens

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.project.R
import com.example.project.dao.FavoriteDao
import com.example.project.dao.NameDao

class NameDetailsActivity : BaseActivity() {

    private lateinit var favDao: FavoriteDao
    private lateinit var dao: NameDao

    // UI Elements
    private lateinit var tvName: TextView
    private lateinit var tvArabicName: TextView
    private lateinit var tvMeaningEng: TextView
    private lateinit var tvMeaningUrdu: TextView
    private lateinit var tvOrigin: TextView
    private lateinit var tvQuranRef: TextView
    private lateinit var tvStrength: TextView
    private lateinit var tvWeakness: TextView
    private lateinit var tvCareer: TextView
    private lateinit var tvRelationship: TextView
    private lateinit var tvSpiritual: TextView
    private lateinit var tvTraits: TextView
    private lateinit var btnPlayAudio: ImageButton
    private lateinit var btnFavorite: ImageButton

    // Data State
    private var pronunciation: String = ""
    private var mediaPlayer: MediaPlayer? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_details)

        dao = NameDao(this)
        favDao = FavoriteDao(this)

        val nameId = intent.getIntExtra("name_id", -1)
        if (nameId != -1) {
            dao.markNameAsViewed(nameId)
            createBinding()
            readNameDetails(nameId)
        } else {
            Log.e("NameDetail", "Invalid name_id received")
            finish()
        }
    }

    private fun createBinding() {
        tvName = findViewById(R.id.tvName)
        tvArabicName = findViewById(R.id.tvArabicName)
        tvMeaningEng = findViewById(R.id.tvMeaningEng)
        tvMeaningUrdu = findViewById(R.id.tvMeaningUrdu)
        tvOrigin = findViewById(R.id.tvOrigin)
        tvQuranRef = findViewById(R.id.tvQuranRef)
        tvStrength = findViewById(R.id.tvStrength)
        tvWeakness = findViewById(R.id.tvWeakness)
        tvCareer = findViewById(R.id.tvCareer)
        tvRelationship = findViewById(R.id.tvRelationship)
        tvSpiritual = findViewById(R.id.tvSpiritualMessage)
        tvTraits = findViewById(R.id.tvTraits)
        btnPlayAudio = findViewById(R.id.btnPlayAudio)
        btnFavorite = findViewById(R.id.btnFavorite)
    }

    private fun readNameDetails(nameId: Int) {
        val cursor = dao.readNamesData(nameId)
        val numerologySet = mutableSetOf<Int>()
        var nameText = ""
        val traitsByNumber = mutableMapOf<Int, MutableList<String>>() // Collect all traits here

        cursor.use { c ->
            if (!c.moveToFirst()) {
                Toast.makeText(this, "Name not found", Toast.LENGTH_SHORT).show()
                return
            }

            do {
                // Populate basic info only once
                if (nameText.isEmpty()) {
                    nameText = c.getString(c.getColumnIndexOrThrow("name"))
                    tvName.text = nameText
                    tvArabicName.text = c.getString(c.getColumnIndexOrThrow("arabic_name"))
                    tvMeaningEng.text = c.getString(c.getColumnIndexOrThrow("meaning_eng"))
                    tvMeaningUrdu.text = c.getString(c.getColumnIndexOrThrow("meaning_urdu"))
                    tvOrigin.text = c.getString(c.getColumnIndexOrThrow("origin"))
                    tvQuranRef.text = c.getString(c.getColumnIndexOrThrow("quran_ref"))

                    val pronIndex = c.getColumnIndex("pronunciation")
                    pronunciation = if (pronIndex != -1) c.getString(pronIndex) ?: "" else ""

                    tvStrength.text = "Strength: ${c.getString(c.getColumnIndexOrThrow("strength"))}"
                    tvWeakness.text = "Weakness: ${c.getString(c.getColumnIndexOrThrow("weakness"))}"
                    tvCareer.text = "Career: ${c.getString(c.getColumnIndexOrThrow("career_suggestion"))}"
                    tvRelationship.text = "Relationship: ${c.getString(c.getColumnIndexOrThrow("relationship_style"))}"
                    tvSpiritual.text = c.getString(c.getColumnIndexOrThrow("spiritual_message"))
                }

                // Collect numerology numbers
                val num = c.getInt(c.getColumnIndexOrThrow("numerology_number"))
                numerologySet.add(num)

                // Collect traits
                val traitNamesConcat = c.getString(c.getColumnIndexOrThrow("trait_names"))
                val traitDescriptionsConcat = c.getString(c.getColumnIndexOrThrow("trait_descriptions"))

                if (!traitNamesConcat.isNullOrEmpty() && !traitDescriptionsConcat.isNullOrEmpty()) {
                    val namesList = traitNamesConcat.split(",").map { it.trim() }
                    val descList = traitDescriptionsConcat.split(",").map { it.trim() }

                    val list = traitsByNumber.getOrPut(num) { mutableListOf() }
                    for (i in namesList.indices) {
                        val traitDisplay = if (i < descList.size) "${namesList[i]}: ${descList[i]}" else namesList[i]
                        list.add(traitDisplay)
                    }
                }

            } while (c.moveToNext())
        }
        // Display all traits grouped by numerology number
        val traitsDisplay = StringBuilder()
        traitsByNumber.forEach { (num, list) ->
            traitsDisplay.append("Traits:\n")
            list.forEach { traitsDisplay.append("• $it\n") }
            traitsDisplay.append("\n")
        }
        tvTraits.text = traitsDisplay.toString()

        setupButtons(nameId, nameText)
    }

    private fun setupButtons(nameId: Int, nameText: String) {
        // Handle Audio
        btnPlayAudio.setOnClickListener {
            if (pronunciation.isNotEmpty()) {
                val audioResId = resources.getIdentifier(pronunciation, "raw", packageName)
                if (audioResId != 0) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(this, audioResId)
                    btnPlayAudio.background = ContextCompat.getDrawable(this, R.drawable.btn_audio_played_bg)

                    mediaPlayer?.setOnCompletionListener {
                        // Revert background to primary after audio ends
                        btnPlayAudio.background = ContextCompat.getDrawable(this, R.drawable.button_play_audio_bg)
                    }
                    mediaPlayer?.start()
                } else {
                    Toast.makeText(this, "Audio file not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Audio not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Favorite
        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favourite_filled else R.drawable.ic_favourite
            )

            if (isFavorite) {
                favDao.addToFavorite(nameId)
                Toast.makeText(this, "$nameText added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                favDao.removeFromFavorite(nameId)
                Toast.makeText(this, "$nameText removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
