package com.example.bookswap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDAO {

    @Query("SELECT * FROM libros WHERE disponible = 1 ORDER BY fechaPublicacion DESC")
    fun observarDisponibles(): Flow<List<LibroEntidad>>

    @Query("SELECT * FROM libros WHERE id = :id")
    suspend fun buscarPorId(id: String): LibroEntidad?

    @Query("SELECT COUNT(*) FROM libros")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(libro: LibroEntidad)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(libros: List<LibroEntidad>)
}