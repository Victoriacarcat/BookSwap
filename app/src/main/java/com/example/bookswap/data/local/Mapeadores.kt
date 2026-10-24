package com.example.bookswap.data.local

import com.example.bookswap.data.modelo.CategoriaLibro
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.modelo.Libro

fun LibroEntidad.aDominio(): Libro = Libro(
    id = id,
    titulo = titulo,
    autor = autor,
    genero = runCatching { CategoriaLibro.valueOf(genero) }.getOrDefault(CategoriaLibro.OTRO),
    estado = EstadoLibro.valueOf(estado),
    imagenResId = imagenResId,
    lat = lat,
    lng = lng,
    idDueño = idDueno,
    disponible = disponible,
    descripcion = descripcion,
    notaPublicante = notaPublicante,
    imagenUri = imagenUri
)

fun Libro.aEntidad(): LibroEntidad = LibroEntidad(
    id = id,
    titulo = titulo,
    autor = autor,
    genero = genero.name,
    estado = estado.name,
    imagenResId = imagenResId,
    imagenUri = imagenUri,
    lat = lat,
    lng = lng,
    idDueno = idDueño,
    disponible = disponible,
    descripcion = descripcion,
    notaPublicante = notaPublicante
)