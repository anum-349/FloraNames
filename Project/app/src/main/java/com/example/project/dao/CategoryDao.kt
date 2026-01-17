package com.example.project.dao

import android.content.Context
import android.util.Log
import com.example.project.database.DBHelper
import com.example.project.models.Gender
import com.example.project.models.Name
import com.example.project.models.NameCategory
import kotlin.arrayOf

class CategoryDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun getNamesWithCategory(categoryId: Int): List<NameCategory> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<NameCategory>()
        val seenNames = mutableSetOf<Int>()

        val cursor = if (categoryId == -1) {
            db.rawQuery(
                """
                SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
            """.trimIndent(),null
            )
        } else if (categoryId == 1){
            db.rawQuery(
                """
                SELECT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
                Where n.gender = "BOY"
            """.trimIndent(),null
            )
        } else if (categoryId == 2){
            db.rawQuery(
                """
                SELECT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
                Where n.gender = "GIRL"
            """.trimIndent(),null
            )
        } else if (categoryId == 7){
            db.rawQuery(
                """
                SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
                where LENGTH(n.name) < 5
            """.trimIndent(),null
            )
        } else if (categoryId == 8){
            db.rawQuery(
                """
                SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
                where LENGTH(n.name) > 5
            """.trimIndent(),null
            )
        } else {
            db.rawQuery(
                """
                SELECT DISTINCT n.id AS name_id, n.name, n.meaning_eng, n.gender,
                       c.id AS category_id, c.category_name
                FROM name n
                LEFT JOIN name_category nc ON n.id = nc.name_id
                LEFT JOIN category c ON nc.category_id = c.id
                WHERE nc.category_id = ?
            """.trimIndent(), arrayOf(categoryId.toString())
            )
        }

        cursor.use { c ->
            while (c.moveToNext()) {
                val nameId = c.getInt(c.getColumnIndexOrThrow("name_id"))

                // skip duplicate names
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

                var categoryIdVal = -1
                var categoryNameVal = "All"
                if (categoryId == -1) {
                    categoryIdVal = -1
                    categoryNameVal = "All"
                } else if (categoryId == 1) {
                    categoryIdVal = 1
                    categoryNameVal = "BOYS"
                } else if (categoryId == 2) {
                    categoryIdVal = 2
                    categoryNameVal = "GIRLS"
                } else if (categoryId == 7) {
                    categoryIdVal = 7
                    categoryNameVal = "Short Names"
                }else if (categoryId == 8) {
                    categoryIdVal = 8
                    categoryNameVal = "Long Names"
                }
                else {
                    categoryIdVal = c.getInt(c.getColumnIndexOrThrow("category_id"))
                    categoryNameVal = c.getString(c.getColumnIndexOrThrow("category_name"))
                }

                val nameCategory = NameCategory(name, categoryIdVal, categoryNameVal)
                Log.i("DBHelper", "Loaded NameCategory: $nameCategory, categoryId: $categoryId")
                list.add(nameCategory)
            }
        }

        return list
    }
}
