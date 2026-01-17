package com.example.project.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.project.database.DBHelper
import com.example.project.models.Gender
import com.example.project.models.Name
import com.example.project.models.NameCategory

class NameDao(context: Context) {

    private val db = DBHelper(context)

    fun readNamesData(nameId: Int): Cursor {
        val query = """
        SELECT
            n.id AS name_id,
            n.name,
            n.arabic_name,
            n.meaning_eng,
            n.meaning_urdu,
            n.origin,
            n.pronunciation,
            n.quran_ref,
            n.gender,

            nu.number AS numerology_number,
            nu.category AS numerology_category,
            nu.strength,
            nu.weakness,
            nu.career_suggestion,
            nu.relationship_style,
            nu.spiritual_message,

            nn.expression_number,
            nn.soul_urge_number,
            nn.personality_number,
            nn.life_path_number,
            nn.birthday_number,

            GROUP_CONCAT(t.trait_name, ', ') AS trait_names,
            GROUP_CONCAT(t.description, ', ') AS trait_descriptions

        FROM name n
        LEFT JOIN name_numerology nn ON nn.name_id = n.id
        LEFT JOIN numerology nu ON nu.number IN (
            nn.expression_number,
            nn.soul_urge_number,
            nn.personality_number,
            nn.life_path_number,
            nn.birthday_number
        )
        LEFT JOIN numerology_trait nt ON nt.numerology_number = nu.number
        LEFT JOIN trait t ON t.id = nt.trait_id
        WHERE n.id = ?
        GROUP BY n.id

    """.trimIndent()

        Log.i("Data Getted: ", query)
        return db.readableDatabase.rawQuery(
            query,
            arrayOf(nameId.toString())
        )
    }

    fun getDailyName(): Cursor {
        val query = """
        SELECT name, meaning_eng
        FROM name
        ORDER BY RANDOM()
        LIMIT 1
    """
        return db.readableDatabase.rawQuery(query, null)
    }

    fun markNameAsViewed(nameId: Int) {
        val database = db.writableDatabase
        val time = System.currentTimeMillis()

        val updated = database.compileStatement(
            "UPDATE recent_viewed SET viewed_at = ? WHERE name_id = ?"
        ).apply {
            bindLong(1, time)
            bindLong(2, nameId.toLong())
        }.executeUpdateDelete()

        if (updated == 0) {
            database.execSQL(
                """
            INSERT INTO recent_viewed (name_id, viewed_at)
            VALUES (?, ?)
            """,
                arrayOf(nameId, time)
            )
        }
    }

    fun getRecentViewedNames(limit: Int = 4): Cursor {
        val database = db.readableDatabase
        // First, try fetching recent viewed names
        val recentCursor = database.rawQuery("""
        SELECT n.id, n.name, n.meaning_eng
        FROM recent_viewed rv
        JOIN name n ON n.id = rv.name_id
        ORDER BY rv.viewed_at DESC
        LIMIT ?
    """.trimIndent(), arrayOf(limit.toString()))

        // If recentCursor is empty, fallback to first names
        return if (recentCursor.count > 0) {
            recentCursor
        } else {
            recentCursor.close() // close empty cursor
            database.rawQuery("""
            SELECT id, name, meaning_eng
            FROM name
            ORDER BY id ASC
            LIMIT ?
        """.trimIndent(), arrayOf(limit.toString()))
        }
    }

    fun getMatchingNames(father: String, mother: String): List<NameCategory> {
        val database = db.readableDatabase
        val list = mutableListOf<NameCategory>()
        val seenNames = mutableSetOf<Int>()

        val fatherPrefix = father.take(2).lowercase()
        val motherPrefix = mother.take(2).lowercase()

        val query = """
        SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender
        FROM name n
        WHERE LOWER(n.name) LIKE ?
           OR LOWER(n.name) LIKE ?
           OR LOWER(n.name) LIKE ?
           OR LOWER(n.name) LIKE ?
    """.trimIndent()

        val args = arrayOf(
            "$fatherPrefix%",
            "%$fatherPrefix%",
            "$motherPrefix%",
            "%$motherPrefix%"
        )

        val cursor = database.rawQuery(query, args)

        cursor.use { c ->
            while (c.moveToNext()) {
                val nameId = c.getInt(c.getColumnIndexOrThrow("name_id"))
                if (seenNames.contains(nameId)) continue
                seenNames.add(nameId)

                val gender = try {
                    Gender.valueOf(c.getString(c.getColumnIndexOrThrow("gender")).uppercase())
                } catch (e: Exception) {
                    Gender.UNISEX
                }

                val name = Name(
                    id = nameId,
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    meaningEng = c.getString(c.getColumnIndexOrThrow("meaning_eng")),
                    gender = gender
                )

                // Custom category info
                val nameCategory = NameCategory(
                    name = name,
                    categoryId = -2,
                    categoryName = "Parent Names"
                )

                list.add(nameCategory)
            }
        }

        return list
    }

    fun getSiblingNames(nameStr: String): List<NameCategory> {
        val database = db.readableDatabase
        val list = mutableListOf<NameCategory>()
        val seenNames = mutableSetOf<Int>()

        val namePrefix = nameStr.take(2).lowercase()
        val nameSuffix = nameStr.takeLast(2).lowercase()

        val query = """
        SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender
        FROM name n
        WHERE LOWER(n.name) LIKE ?
           OR LOWER(n.name) LIKE ?
    """.trimIndent()

        val args = arrayOf(
            "$namePrefix%",
            "%$nameSuffix"
        )

        val cursor = database.rawQuery(query, args)

        cursor.use { c ->
            while (c.moveToNext()) {
                val nameId = c.getInt(c.getColumnIndexOrThrow("name_id"))
                if (seenNames.contains(nameId)) continue
                seenNames.add(nameId)

                val gender = try {
                    Gender.valueOf(c.getString(c.getColumnIndexOrThrow("gender")).uppercase())
                } catch (e: Exception) {
                    Gender.UNISEX
                }

                val name = Name(
                    id = nameId,
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    meaningEng = c.getString(c.getColumnIndexOrThrow("meaning_eng")),
                    gender = gender
                )

                val nameCategory = NameCategory(
                    name = name,
                    categoryId = -3,
                    categoryName = "Name Twining"
                )

                list.add(nameCategory)
            }
        }

        return list
    }

    fun getAllNames(): Cursor {
        val database = db.readableDatabase
        return database.rawQuery("SELECT name, meaning_eng, id FROM name", null)
    }
}
