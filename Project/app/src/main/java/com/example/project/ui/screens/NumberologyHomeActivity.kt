package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import android.widget.Button

class NumberologyHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numberology_home)

        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.setOnClickListener{
            val intent = Intent(this, NumberologyInputActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
            finish()
        }
    }
}
