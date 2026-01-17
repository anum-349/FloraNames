package com.example.project.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DBHelper(context: Context) : SQLiteOpenHelper(context, DBNAME, null, DB_VERSION) {

    companion object {
        const val DBNAME = "velora_baby.db"
        const val DB_VERSION = 1
    }

    private val appContext = context.applicationContext

    init {
        copyDatabaseIfNeeded()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Name table
        db.execSQL(
            """ 
     CREATE TABLE IF NOT EXISTS name (
      id INTEGER PRIMARY KEY AUTOINCREMENT, 
        name TEXT, 
        arabic_name TEXT, 
        meaning_eng TEXT, 
        meaning_urdu TEXT, 
        origin TEXT, 
        quran_ref TEXT, 
        pronunciation TEXT,
        gender TEXT CHECK (gender IN ('BOY', 'GIRL', 'UNISEX')) ) 
""".trimIndent()
        )
        // Category table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS category ( 
         id INTEGER PRIMARY KEY AUTOINCREMENT, 
         category_name TEXT ) 
         """.trimIndent()
        )
        // NameCategory table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS name_category (
          id INTEGER PRIMARY KEY AUTOINCREMENT, 
name_id INTEGER, 
category_id INTEGER, 
FOREIGN KEY(name_id) REFERENCES name(id) ON DELETE CASCADE, 
FOREIGN KEY(category_id) REFERENCES category(id) ON DELETE CASCADE ) 
""".trimIndent()
        )
        // Numerology interpretation table
        db.execSQL(
            """ 
CREATE TABLE IF NOT EXISTS numerology ( 
         id INTEGER PRIMARY KEY AUTOINCREMENT,
number INTEGER, 
category TEXT, 
strength TEXT, 
weakness TEXT, 
career_suggestion TEXT, 
relationship_style TEXT, 
spiritual_message TEXT ) 
""".trimIndent()
        )
        // NameNumerology table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS name_numerology (
          id INTEGER PRIMARY KEY AUTOINCREMENT, 
name_id INTEGER, 
expression_number INTEGER, 
soul_urge_number INTEGER, 
personality_number INTEGER, 
life_path_number INTEGER, 
birthday_number INTEGER, 
FOREIGN KEY(name_id) REFERENCES name(id) ON DELETE CASCADE ) 
""".trimIndent()
        )
        // Trait table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS trait ( 
         id INTEGER PRIMARY KEY AUTOINCREMENT, 
         trait_name TEXT, 
         category TEXT, 
         description TEXT ) 
         """.trimIndent()
        )
        // NumerologyTrait mapping table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS numerology_trait (
          id INTEGER PRIMARY KEY AUTOINCREMENT, 
numerology_number INTEGER, 
numerology_category TEXT, 
trait_id INTEGER, 
FOREIGN KEY(trait_id) REFERENCES trait(id) ON DELETE CASCADE ) 
""".trimIndent()
        )
        // Favorite table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS favorite (
          id INTEGER PRIMARY KEY AUTOINCREMENT, 
name_id INTEGER, 
added_at TEXT DEFAULT CURRENT_TIMESTAMP, 
FOREIGN KEY(name_id) REFERENCES name(id) ON DELETE CASCADE ) 
""".trimIndent()
        )
        // User table
        db.execSQL(
            """ 
         CREATE TABLE IF NOT EXISTS user ( 
         id INTEGER PRIMARY KEY AUTOINCREMENT, 
         name TEXT, 
         email TEXT,
          profile_picture TEXT, 
created_at TEXT DEFAULT CURRENT_TIMESTAMP )
 """.trimIndent()
        )

        db.execSQL(
            """
        CREATE TABLE recent_viewed (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name_id INTEGER NOT NULL,
            viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY(name_id) REFERENCES name(id)
        )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE name ADD COLUMN pronunciation TEXT")
        }
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS numerology_trait")
        db.execSQL("DROP TABLE IF EXISTS name_numerology")
        db.execSQL("DROP TABLE IF EXISTS numerology")
        db.execSQL("DROP TABLE IF EXISTS trait")
        db.execSQL("DROP TABLE IF EXISTS favorite")
        db.execSQL("DROP TABLE IF EXISTS name_category")
        db.execSQL("DROP TABLE IF EXISTS category")
        db.execSQL("DROP TABLE IF EXISTS name")
        db.execSQL("DROP TABLE IF EXISTS user")
        db.execSQL("DROP TABLE IF EXISTS recent_viewed")
        onCreate(db)
    }
    private fun copyDatabaseIfNeeded() {
        val dbFile = appContext.getDatabasePath(DBNAME)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            appContext.assets.open(DBNAME).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        return super.getWritableDatabase()
    }
}
