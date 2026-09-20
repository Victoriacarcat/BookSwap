package com.example.bookswap.data.repo
import com.example.bookswap.data.modelo.Libro

interface LibroRepositorio {
    suspend fun obtenerLibros(): List<Libro>
    suspend fun obtenerLibroPorId(id: String): Libro?
}