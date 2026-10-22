package com.example.bookswap.data.repo

import com.example.bookswap.data.modelo.Libro
import kotlinx.coroutines.flow.Flow

interface LibroRepositorio {
    fun observarLibros(): Flow<List<Libro>>
    suspend fun obtenerLibroPorId(id: String): Libro?
    suspend fun publicarLibro(libro: Libro)
}