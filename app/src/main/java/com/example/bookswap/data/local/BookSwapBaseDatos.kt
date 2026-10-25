package com.example.bookswap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LibroEntidad::class, SolicitudEntidad::class],
    version = 2,
    exportSchema = false
)
abstract class BookSwapBaseDatos : RoomDatabase() {

    abstract fun libroDao(): LibroDAO
    abstract fun solicitudDao(): SolicitudDAO

    companion object {
        @Volatile
        private var instancia: BookSwapBaseDatos? = null

        // Pasa una base de datos de la versión 1 a la 2 sin perder los libros guardados
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Tabla nueva para las solicitudes de intercambio
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `solicitudes` (
                        `id` TEXT NOT NULL,
                        `idLibroSolicitado` TEXT NOT NULL,
                        `idLibroOfrecido` TEXT NOT NULL,
                        `idSolicitante` TEXT NOT NULL,
                        `idDueño` TEXT NOT NULL,
                        `mensaje` TEXT NOT NULL,
                        `estado` TEXT NOT NULL,
                        `fecha` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                // 2. Renombrar libros.idDueno -> idDueño. SQLite en Android < 11 no permite
                // renombrar columnas, así que se crea la tabla nueva, se copian los datos y se reemplaza
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `libros_nueva` (
                        `id` TEXT NOT NULL,
                        `titulo` TEXT NOT NULL,
                        `autor` TEXT NOT NULL,
                        `genero` TEXT NOT NULL,
                        `estado` TEXT NOT NULL,
                        `imagenResId` INTEGER NOT NULL,
                        `imagenUri` TEXT,
                        `lat` REAL NOT NULL,
                        `lng` REAL NOT NULL,
                        `idDueño` TEXT NOT NULL,
                        `disponible` INTEGER NOT NULL,
                        `descripcion` TEXT NOT NULL,
                        `notaPublicante` TEXT NOT NULL,
                        `fechaPublicacion` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `libros_nueva` (`id`, `titulo`, `autor`, `genero`, `estado`, `imagenResId`,
                        `imagenUri`, `lat`, `lng`, `idDueño`, `disponible`, `descripcion`, `notaPublicante`,
                        `fechaPublicacion`)
                    SELECT `id`, `titulo`, `autor`, `genero`, `estado`, `imagenResId`,
                        `imagenUri`, `lat`, `lng`, `idDueno`, `disponible`, `descripcion`, `notaPublicante`,
                        `fechaPublicacion`
                    FROM `libros`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `libros`")
                db.execSQL("ALTER TABLE `libros_nueva` RENAME TO `libros`")
            }
        }

        fun obtener(contexto: Context): BookSwapBaseDatos =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    BookSwapBaseDatos::class.java,
                    "bookswap.db"
                ).addMigrations(MIGRACION_1_2)
                    .build().also { instancia = it }
            }
    }
}
