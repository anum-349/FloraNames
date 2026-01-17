package com.example.project.ui.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.example.project.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.imageview.ShapeableImageView
import com.example.project.dao.UserDao

class ParentProfileActivity : BaseActivity() {

    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var profileImage: ShapeableImageView
    private lateinit var editProfileIcon: ImageView
    private lateinit var parentName: TextView
    private lateinit var parentEmail: TextView

    private lateinit var userDao: UserDao

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                profileImage.setImageURI(uri)
                userDao.updateUser(profilePicture = uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Save preference
        val prefs = getSharedPreferences("parent_profile_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_profile_setting)
        setBottomNavActive(R.id.navProfile)

        switchDarkMode = findViewById(R.id.switch_dark_mode)
        profileImage = findViewById(R.id.profile_image)
        editProfileIcon = findViewById(R.id.edit_profile_icon)
        parentName = findViewById(R.id.parent_name)
        parentEmail = findViewById(R.id.parent_email)

        userDao = UserDao(this)
        userDao.insertDefaultUser() // Ensure default user exists

        loadUserData()
        switchDarkMode.isChecked = isDarkMode
        // Edit picture
        profileImage.setOnClickListener { chooseImage() }
        editProfileIcon.setOnClickListener { chooseImage() }

        // Edit Name
        parentName.setOnClickListener {
            editDialog("Edit Name", parentName.text.toString()) { new ->
                parentName.text = new
                userDao.updateUser(name = new)
                updatePlaceholderIfNeeded()
            }
        }

        // Edit Email
        parentEmail.setOnClickListener {
            editDialog("Edit Email", parentEmail.text.toString()) { new ->
                parentEmail.text = new
                userDao.updateUser(email = new)
            }
        }

        // Dark mode ON/OFF
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )

            // Save preference
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
        }

    }

    /** Load user info from DB */
    private fun loadUserData() {
        val cursor = userDao.getUser()
        cursor.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val email = it.getString(it.getColumnIndexOrThrow("email"))
                val profileUri = it.getString(it.getColumnIndexOrThrow("profile_picture"))

                parentName.text = name
                parentEmail.text = email

                if (!profileUri.isNullOrEmpty()) {
                    try {
                        profileImage.setImageURI(Uri.parse(profileUri))
                    } catch (e: SecurityException) {
                        profileImage.setImageBitmap(generateLetterPlaceholder(name))
                    }
                } else {
                    profileImage.setImageBitmap(generateLetterPlaceholder(name))
                }
            }
        }
    }

    /** Pick image */
    private fun chooseImage() {
        imagePickerLauncher.launch(arrayOf("image/*"))
    }

    /** Generate circular placeholder */
    private fun generateLetterPlaceholder(name: String): Bitmap {
        val firstLetter = name.firstOrNull()?.uppercase() ?: "?"
        val size = 300
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintBg = Paint().apply {
            color = Color.parseColor("#FFB6C1")
            style = Paint.Style.FILL
        }

        val paintText = Paint().apply {
            color = Color.WHITE
            textSize = 150f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintBg)
        canvas.drawText(firstLetter, size / 2f, size / 1.6f, paintText)

        return bitmap
    }

    private fun updatePlaceholderIfNeeded() {
        if (userDao.getUser().use { it.moveToFirst(); it.getString(it.getColumnIndexOrThrow("profile_picture")) }.isNullOrEmpty()) {
            profileImage.setImageBitmap(generateLetterPlaceholder(parentName.text.toString()))
        }
    }

    /** Edit text dialog */
    private fun editDialog(title: String, oldValue: String, callback: (String) -> Unit) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = view.findViewById<EditText>(R.id.dialog_edit_text)
        editText.setText(oldValue)

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val newValue = editText.text.toString().trim()
                if (newValue.isNotEmpty()) callback(newValue)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_box_bg)
        dialog.show()
    }
}
