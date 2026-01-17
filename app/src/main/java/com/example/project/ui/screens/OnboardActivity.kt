package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R

class OnboardActivity : AppCompatActivity() {

    private lateinit var btnNext: TextView
    private lateinit var btnSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        val onboardingImages = arrayOf(R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.onboard_1)
        val onboardingTitles = arrayOf(
            "Welcome to Velora Baby",
            "Explore Beautiful Names",
            "Understand Name Meanings",
            "Save Your Favorites",
            "Begin Your Naming Journey"
        )

        val onboardingDescriptions = arrayOf(
            "Your companion in finding meaningful, modern, and traditional baby names for your little one.",

            "Browse a wide collection of baby names categorized by gender, origin, and style to find the perfect match.",

            "Learn the meaning, pronunciation, and cultural significance behind every name you explore.",

            "Shortlist the names you love by saving them to your favorites for easy access anytime.",

            "Start exploring, comparing, and choosing the name that feels just right for your baby."
        )

        var currentStep = 0

        fun updateOnboardingStep() {
            val imageView = findViewById<ImageView>(R.id.onboardImage)
            val titleView = findViewById<TextView>(R.id.welcomeTitle)
            val descView = findViewById<TextView>(R.id.onboardDescription)
            val btnNext = findViewById<TextView>(R.id.btnNext)

            val fadeOutDuration = 200L
            imageView.animate().alpha(0f).setDuration(fadeOutDuration).withEndAction {
                imageView.setImageResource(onboardingImages[currentStep])
                titleView.text = onboardingTitles[currentStep]
                descView.text = onboardingDescriptions[currentStep]

                imageView.animate().alpha(1f).setDuration(fadeOutDuration).start()
                titleView.animate().alpha(1f).setDuration(fadeOutDuration).start()
                descView.animate().alpha(1f).setDuration(fadeOutDuration).start()
            }.start()

            btnNext.text = if (currentStep == onboardingImages.size - 1) "Get Started" else "Next"
        }

        btnNext.setOnClickListener {
            if (currentStep < onboardingImages.size - 1) {
                currentStep++
                updateOnboardingStep()
            } else {
                navigateToHome()
            }
        }

        btnSkip.setOnClickListener {
            navigateToHome()
        }

    }

    private fun navigateToHome() {
        val intent = Intent(this, DashboardActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
        finish()
    }

}
