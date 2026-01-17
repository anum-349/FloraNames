package com.example.project.ui.screens

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project.R
import com.example.project.dao.UserDao
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.ImageView

open class BaseActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView
    private lateinit var dao: UserDao

    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base, null)
        val container = baseLayout.findViewById<FrameLayout>(R.id.container)

        layoutInflater.inflate(layoutResID, container, true)
        super.setContentView(baseLayout)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        dao = UserDao(this)
        setupDrawer()
        setupBottomNav()
        displayUserProfile()
    }

    override fun onResume() {
        super.onResume()
        displayUserProfile()
    }


    protected fun displayUserProfile() {
        val profileImage = findViewById<ShapeableImageView>(R.id.profilePic)
        val profileName = findViewById<TextView>(R.id.tvWelcome)
        val tvDate = findViewById<TextView>(R.id.tvDate)

        val dao = UserDao(this)
        val cursor = dao.getUser()
        cursor.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val profileUri = it.getString(it.getColumnIndexOrThrow("profile_picture"))

                profileName.text = "Welcome, $name"
                if (!profileUri.isNullOrEmpty()) {
                    try {
                        profileImage.setImageURI(Uri.parse(profileUri))
                    } catch (e: SecurityException) {
                        profileImage.setImageURI(Uri.parse("profile"))
                    }
                } else {
                    profileImage.setImageURI(Uri.parse("profile"))
                }
            }
        }
        val currentDate = SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date())
        tvDate.text = currentDate
    }

    private fun setupDrawer() {
        val menuIcon = findViewById<ImageView>(R.id.ic_sidebar)

        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)

            when (item.itemId) {
                R.id.nav_tips -> {
                    val intent = Intent(this, NamingTipsActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

                R.id.nav_islamic_guides -> {
                    val intent = Intent(this, IslamicGuideLinesActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

                R.id.nav_favorites -> {
                    val intent = Intent(this, FavouriteActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

                R.id.nav_settings -> {
                    val intent = Intent(this, ParentProfileActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)

                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

                R.id.numerology_about -> {
                    val intent = Intent(this, AboutNumerologyActivity::class.java)

                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    startActivity(intent, options.toBundle())
                }

            }
            true
        }
    }

    private fun setupBottomNav() {
        findViewById<ImageButton>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

        findViewById<ImageButton>(R.id.navFavorites)?.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }

        findViewById<ImageButton>(R.id.navProfile)?.setOnClickListener {
            val intent = Intent(this, ParentProfileActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            startActivity(intent, options.toBundle())
        }
    }

    protected fun setBottomNavActive(activeId: Int) {
        findViewById<ImageButton>(R.id.navHome)?.isSelected = activeId == R.id.navHome
        findViewById<ImageButton>(R.id.navFavorites)?.isSelected = activeId == R.id.navFavorites
        findViewById<ImageButton>(R.id.navProfile)?.isSelected = activeId == R.id.navProfile
    }
}
