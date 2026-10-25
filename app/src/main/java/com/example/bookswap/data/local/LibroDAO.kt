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

    @Query("SELECT * FROM libros WHERE idDueño = :idDueño ORDER BY fechaPublicacion DESC")
    fun observarPorDueño(idDueño: String): Flow<List<LibroEntidad>>

    @Query("SELECT * FROM libros WHERE id = :id")
    fun observarPorId(id: String): Flow<LibroEntidad?>

    @Query("SELECT * FROM libros WHERE id = :id")
    suspend fun buscarPorId(id: String): LibroEntidad?

    @Query("SELECT COUNT(*) FROM libros")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(libro: LibroEntidad)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(libros: List<LibroEntidad>)

    // Solo los campos editables: no toca ubicación, dueño ni fecha de publicación
    @Query(
        """
        UPDATE libros SET titulo = :titulo, autor = :autor, genero = :genero, estado = :estado,
            descripcion = :descripcion, notaPublicante = :notaPublicante, imagenUri = :imagenUri
        WHERE id = :id
        """
    )
    suspend fun actualizar(
        id: String,
        titulo: String,
        autor: String,
        genero: String,
        estado: String,
        descripcion: String,
        notaPublicante: String,
        imagenUri: String?
    )

    @Query("UPDATE libros SET imagenResId = :imagenResId WHERE id = :id")
    suspend fun actualizarImagenRecurso(id: String, imagenResId: Int)
}