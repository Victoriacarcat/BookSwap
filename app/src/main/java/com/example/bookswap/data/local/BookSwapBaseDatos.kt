package com.example.bookswap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LibroEntidad::class],
    version = 1,
    exportSchema = false
)
abstract class BookSwapBaseDatos : RoomDatabase() {

    abstract fun libroDao(): LibroDAO

    companion object {
        @Volatile
        private var instancia: BookSwapBaseDatos? = null

        fun obtener(contexto: Context): BookSwapBaseDatos =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BookSwapBaseDatos::class.java,
                    "bookswap.db"
                ).build().also { instancia = it }
            }
    }
}