package com.example.project.dao

import android.content.Context
import android.database.Cursor
import com.example.project.database.DBHelper

class NumerologyDao(context: Context) {

    private var database = DBHelper(context)

    fun getNumerologyByLifePathNumber(lifePathNumber: Int): Cursor {
        val db = database.readableDatabase

        val query = """
            SELECT 
                n.number,
                n.category,
                n.strength,
                n.weakness,
                n.career_suggestion,
                n.relationship_style,
                n.spiritual_message,
                GROUP_CONCAT(name.name) AS matching_names
            FROM numerology n
            LEFT JOIN name_numerology nn
                ON nn.life_path_number = n.number
            LEFT JOIN name
                ON name.id = nn.name_id
            WHERE n.number = ?
            GROUP BY n.number
        """.trimIndent()

        return db.rawQuery(query, arrayOf(lifePathNumber.toString()))
    }
}
