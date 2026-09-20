package com.example.bookswap.data.repo

import com.example.bookswap.data.fuenteData.DatosHardcodeados
import com.example.bookswap.data.modelo.Libro

class LibroRepositorioTemp : LibroRepositorio {
    private val libros = DatosHardcodeados.libros

    override suspend fun obtenerLibros(): List<Libro> = libros

    override suspend fun obtenerLibroPorId(id: String): Libro? =
        libros.find { it.id == id }
}