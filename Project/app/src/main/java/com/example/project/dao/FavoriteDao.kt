package com.example.project.dao

import android.content.Context
import android.database.Cursor
import com.example.project.database.DBHelper

class FavoriteDao(context: Context) {

    var db = DBHelper(context)

    fun addToFavorite(nameId: Int) {
        db.writableDatabase.execSQL(
            """
        INSERT OR IGNORE INTO favorite (name_id)
        VALUES (?)
        """,
            arrayOf(nameId)
        )
    }

    fun getFavoriteNames(): Cursor {
        val query = """
        SELECT 
            f.id AS fav_id,
            n.id AS name_id,
            n.name,
            n.meaning_eng
        FROM favorite f
        JOIN name n ON n.id = f.name_id
        ORDER BY f.added_at DESC
    """
        return db.readableDatabase.rawQuery(query, null)
    }

    fun removeFromFavorite(nameId: Int) {
        db.writableDatabase.execSQL(
            "DELETE FROM favorite WHERE name_id = ?",
            arrayOf(nameId)
        )
    }

}