package com.example.bookswap.data.repo

import com.example.bookswap.data.fuenteData.DatosHardcodeados
import com.example.bookswap.data.local.LibroDAO
import com.example.bookswap.data.local.aDominio
import com.example.bookswap.data.local.aEntidad
import com.example.bookswap.data.modelo.Libro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibroRepositorioRoom(
    private val libroDao: LibroDAO
) : LibroRepositorio {

    override fun observarLibros(): Flow<List<Libro>> =
        libroDao.observarDisponibles().map { entidades ->
            entidades.map { it.aDominio() }
        }

    override suspend fun obtenerLibroPorId(id: String): Libro? =
        libroDao.buscarPorId(id)?.aDominio()

    override suspend fun publicarLibro(libro: Libro) {
        libroDao.insertar(libro.aEntidad())
    }

    suspend fun sembrarSiVacio() {
        if (libroDao.contar() == 0) {
            libroDao.insertarTodos(DatosHardcodeados.libros.map { it.aEntidad() })
        }
    }
}