package com.example.project.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.project.database.DBHelper
import com.example.project.models.User

class UserDao(private  val context: Context) {
    private var database = DBHelper(context)

    fun insertDefaultUser() {
        val db = database.writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM user", null)
        cursor.use {
            if (it.moveToFirst() && it.getInt(0) == 0) {
                val cv = ContentValues().apply {
                    put("name", "Parent Name")
                    put("email", "parent@example.com")
                    put("profile_picture", "profile")
                }
                db.insert("user", null, cv)
            }
        }
    }
    fun getUser(): Cursor {
        return database.readableDatabase.rawQuery("SELECT * FROM user LIMIT 1", null)
    }

    fun updateUser(
        name: String? = null,
        email: String? = null,
        profilePicture: String? = null
    ) {
        val db = database.writableDatabase

        name?.let {
            val cv = ContentValues().apply { put("name", it) }
            db.update("user", cv, "id = ?", arrayOf("1"))
        }

        email?.let {
            val cv = ContentValues().apply { put("email", it) }
            db.update("user", cv, "id = ?", arrayOf("1"))
        }

        profilePicture?.let {
            val cv = ContentValues().apply { put("profile_picture", it) }
            db.update("user", cv, "id = ?", arrayOf("1"))
        }

        // ------------------------------
        // Save to SharedPreferences for instant header update
        val prefs = context.getSharedPreferences("parent_profile_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        name?.let { editor.putString("name", it) }
        email?.let { editor.putString("email", it) }
        profilePicture?.let { editor.putString("imageUri", it) }
        editor.apply()
    }
}
