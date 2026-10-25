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

    override fun observarLibrosDe(idDueño: String): Flow<List<Libro>> =
        libroDao.observarPorDueño(idDueño).map { entidades ->
            entidades.map { it.aDominio() }
        }

    override fun observarLibro(id: String): Flow<Libro?> =
        libroDao.observarPorId(id).map { it?.aDominio() }

    override suspend fun obtenerLibroPorId(id: String): Libro? =
        libroDao.buscarPorId(id)?.aDominio()

    override suspend fun publicarLibro(libro: Libro) {
        libroDao.insertar(libro.aEntidad())
    }

    override suspend fun actualizarLibro(libro: Libro) {
        libroDao.actualizar(
            id = libro.id,
            titulo = libro.titulo,
            autor = libro.autor,
            genero = libro.genero.name,
            estado = libro.estado.name,
            descripcion = libro.descripcion,
            notaPublicante = libro.notaPublicante,
            imagenUri = libro.imagenUri
        )
    }

    suspend fun sembrarDatosIniciales() {
        if (libroDao.contar() == 0) {
            libroDao.insertarTodos(DatosHardcodeados.libros.map { it.aEntidad() })
        } else {
            // Los ids de R.drawable cambian al recompilar (p. ej. al agregar librerías),
            // así que se refrescan en cada arranque para que las portadas no se desfasen
            DatosHardcodeados.libros.forEach {
                libroDao.actualizarImagenRecurso(it.id, it.imagenResId)
            }
        }
    }
}